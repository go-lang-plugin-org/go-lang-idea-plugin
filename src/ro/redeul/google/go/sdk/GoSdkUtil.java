package ro.redeul.google.go.sdk;

import com.google.common.collect.ImmutableMap;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.CapturingProcessHandler;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ModuleRootModel;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.SystemProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.config.sdk.GoAppEngineSdkData;
import ro.redeul.google.go.config.sdk.GoAppEngineSdkType;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.config.sdk.GoSdkType;
import ro.redeul.google.go.config.sdk.GoTargetArch;
import ro.redeul.google.go.config.sdk.GoTargetOs;
import ro.redeul.google.go.ide.GoGlobalSettings;
import ro.redeul.google.go.ide.GoProjectSettings;
import ro.redeul.google.go.lang.stubs.GoNamesCache;
import ro.redeul.google.go.util.GoUtil;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

public class GoSdkUtil {

    private static final Logger LOG = Logger.getInstance(
            "ro.redeul.google.go.sdk.GoSdkUtil");
    private static final String TEST_SDK_PATH = "go.test.sdk.home";

    private static final String DEFAULT_MOCK_PATH = "go/default";

    private static final String ENV_GO_ROOT = "GOROOT";

    // release: "xx"
    private static final Pattern RE_APP_ENGINE_VERSION_MATCHER =
            Pattern.compile("^release: \"([^\"]+)\"$", Pattern.MULTILINE);

    private static final Pattern RE_APP_ENGINE_TIMESTAMP_MATCHER =
            Pattern.compile("^timestamp: ([0-9]+)$", Pattern.MULTILINE);

    private static final Pattern RE_APP_ENGINE_API_VERSIONS_MATCHER =
            Pattern.compile("^api_versions: \\[([^\\]]+)\\]$", Pattern.MULTILINE);

    private static final Pattern RE_OS_MATCHER =
            Pattern.compile("^(?:set )?GOOS=\"?(darwin|freebsd|linux|windows)\"?$",
                    Pattern.MULTILINE);

    private static final Pattern RE_ARCH_MATCHER =
            Pattern.compile("^(?:set )?GOARCH=\"?(386|amd64|arm)\"?$",
                    Pattern.MULTILINE);

    private static final Pattern RE_HOSTOS_MATCHER =
            Pattern.compile("^(?:set )?GOHOSTOS=\"?(darwin|freebsd|openbsd|linux|windows)\"?$",
                    Pattern.MULTILINE);

    private static final Pattern RE_HOSTARCH_MATCHER =
            Pattern.compile("^(?:set )?GOHOSTARCH=\"?(386|amd64|arm)\"?$",
                    Pattern.MULTILINE);

    private static final Pattern RE_ROOT_MATCHER =
            Pattern.compile("^(?:set )?GOROOT=\"?([^\"]+)\"?$", Pattern.MULTILINE);

    private static final ImmutableMap<String, String> GoVersionPackagesPath =
            ImmutableMap.of(
                    "1.0", "/src/pkg",
                    "1.1", "/src/pkg",
                    "1.2", "/src/pkg",
                    "1.3", "/src/pkg",
                    "1.4", "/src"
            );

    public static GoSdkData testGoogleGoSdk(String path) {

        if (!checkFolderExists(path)) {
            LOG.warn("GO SDK: Could not find root directory @ " + path);
            return null;
        }

        if (!checkFolderExists(path, "src")) {
            LOG.warn("GO SDK: Could not find src directory @ " + path + "/src");
            return null;
        }

        if (!checkFolderExists(path, "pkg")) {
            LOG.warn("GO SDK: Could not find src directory @ " + path + "/pkg");
            return null;
        }

        String goCommand = findGoExecutable(path);
        if (goCommand.equals("")) {
            LOG.warn("GO SDK: Could not find go binary in expected locations.");
            return null;
        }

        GoSdkData data = findHostOsAndArch(path, goCommand, new GoSdkData());

        data = findVersion(path, goCommand, data);

        if (data != null) {
            data.GO_GOROOT_PATH = path;
            data.GO_BIN_PATH = goCommand;
            data.version = GoSdkData.LATEST_VERSION;
        }

        return data;
    }

    public static String findGoExecutable(String path) {
        String goCommand = path + "/bin/go";
        if (checkFileExists(goCommand)) {
            return goCommand;
        }

        // Perhaps we're on Windows?
        goCommand = path + "/bin/go.exe";
        if (checkFileExists(goCommand)) {
            return goCommand;
        }

        // Perhaps we are not on Windows but the packers have a different idea
        // on how this should work...
        goCommand = "/usr/bin/go";
        if (checkFileExists(goCommand)) {
            return goCommand;
        }

        // Well then no go executable for us :(
        return "";
    }

    @Nullable
    public static VirtualFile getSdkSourcesRoot(Sdk sdk) {
        final VirtualFile homeDirectory = sdk.getHomeDirectory();

        if (homeDirectory == null) {
            return null;
        }

        String sdkVersion = sdk.getVersionString();
        if (sdkVersion == null || sdkVersion.isEmpty()) {
            return null;
        }

        String sourceRoot = "";

        if (sdk.getSdkType() == GoSdkType.getInstance()) {
            sourceRoot = getPackagesPathForGoVersion(sdkVersion);
        } else if (sdk.getSdkType() == GoAppEngineSdkType.getInstance()) {
            //Float sdkMajorVersion = Float.parseFloat(sdkVersion.substring(0, 3));
            //Float sdkMinorVersion = Float.parseFloat(sdkVersion.substring(4, 6));

            // For now Go AppEngine SDK at version 1.9.15 stil uses goroot/src/pkg
            // When a new version will be released to use the goroot/src layout
            // or any other form of layout, this should be changed as well
            sourceRoot = "goroot/src/pkg";
        }

        if (sourceRoot.isEmpty()) {
            return null;
        }


        if (checkFolderExists(homeDirectory.getPath(), sourceRoot)) {
            return homeDirectory.findFileByRelativePath(sourceRoot);
        }

        LOG.warn("Could not find GO SDK sources root");
        return null;
    }

    @Nullable
    public static String computeGoBuiltinPackagesPath(String goRoot) {
        String goCommand = GoSdkUtil.findGoExecutable(goRoot);
        if (goCommand.equals("")) {
            LOG.warn("GO SDK: Could not find go binary in expected locations.");
            return null;
        }

        GoSdkData data = GoSdkUtil.findHostOsAndArch(goRoot, goCommand, new GoSdkData());

        data = GoSdkUtil.findVersion(goRoot, goCommand, data);
        if (data == null) {
            LOG.warn("GO SDK: Could not detect go version.");
            return null;
        }

        return goRoot + getPackagesPathForGoVersion(data.VERSION_MAJOR);
    }


    private static String getPackagesPathForGoVersion(String goVersionOutput) {
        String sdkVersion = goVersionOutput.substring(2, 5);

        String goPackagesPath = GoVersionPackagesPath.get(sdkVersion);
        if (goPackagesPath == null) {
            return "/src";
        }

        return goPackagesPath;
    }

    public static GoSdkData findVersion(final String path, String goCommand, GoSdkData data) {

        if (data == null)
            return null;

        try {
            GeneralCommandLine command = new GeneralCommandLine();
            command.setExePath(goCommand);
            command.addParameter("version");
            command.withWorkDirectory(path);
            command.getEnvironment().put("GOROOT", path);

            ProcessOutput output = new CapturingProcessHandler(
                    command.createProcess(),
                    Charset.defaultCharset(),
                    command.getCommandLineString()).runProcess();

            if (output.getExitCode() != 0) {
                LOG.error("Go compiler exited with invalid exit code: " + output.getExitCode());
                return null;
            }

            data.VERSION_MAJOR = output.getStdout().replaceAll("go version", "").trim();
            return data;
        } catch (Exception e) {
            LOG.error("Exception while executing the process:", e);
            return null;
        }
    }

    public static GoSdkData findHostOsAndArch(final String path, String goCommand, GoSdkData data) {

        if (data == null)
            return null;

        try {
            GeneralCommandLine command = new GeneralCommandLine();
            command.setExePath(goCommand);
            command.addParameter("env");
            command.withWorkDirectory(path);
            command.getEnvironment().put("GOROOT", path);

            ProcessOutput output = new CapturingProcessHandler(
                    command.createProcess(),
                    Charset.defaultCharset(),
                    command.getCommandLineString()).runProcess();

            if (output.getExitCode() != 0) {
                LOG.error(
                        format(
                                "%s env command exited with invalid exit code: %d",
                                goCommand, output.getExitCode()));
                return null;
            }

            String outputString = output.getStdout();

            Matcher matcher;
            matcher = RE_HOSTOS_MATCHER.matcher(outputString);
            if (matcher.find()) {
                data.TARGET_OS = GoTargetOs.fromString(matcher.group(1));
            }

            matcher = RE_HOSTARCH_MATCHER.matcher(outputString);
            if (matcher.find()) {
                data.TARGET_ARCH = GoTargetArch.fromString(matcher.group(1));
            }
        } catch (ExecutionException e) {
            LOG.error("Exception while executing the process:", e);
            return null;
        }

        if (data.TARGET_ARCH != null && data.TARGET_OS != null)
            return data;

        LOG.warn("Could not determine Target Arch or OS");
        return null;
    }

    public static GoAppEngineSdkData testGoAppEngineSdk(String path) {

        if (!checkFolderExists(path)
                || !checkFileExists(path, "dev_appserver.py")
                || !(checkFileExists(path, "goapp") || checkFileExists(path, "goapp.bat"))
                || !checkFolderExists(path, "goroot")
                || !checkFolderExists(path, "goroot", "pkg"))
            return null;

        if (!checkFileExists(path, "VERSION"))
            return null;


        GoAppEngineSdkData sdkData = new GoAppEngineSdkData();

        sdkData.SDK_HOME_PATH = path;
        sdkData.GOAPP_BIN_PATH = getGoAppBinPath(path);
        sdkData.GO_HOME_PATH = format("%s%sgoroot", path, File.separator);

        String execName = sdkData.GO_HOME_PATH + "/bin/goapp";
        if (isHostOsWindows()) {
            execName += ".exe";
        }
        String homePath = sdkData.GO_HOME_PATH + "/bin";

        GeneralCommandLine command = new GeneralCommandLine();
        if (checkFileExists(execName)) {
            execName = sdkData.GO_HOME_PATH + "/../goapp";
            if (isHostOsWindows()) {
                execName += ".bat";
            }
            homePath = sdkData.GO_HOME_PATH + "/..";

            if (!checkFileExists(execName)) {
                return null;
            }
        }

        command.setExePath(execName);
        command.withWorkDirectory(homePath);
        command.addParameter("env");

        sdkData.TARGET_ARCH = GoTargetArch._amd64;
        sdkData.TARGET_OS = GoTargetOs.Linux;

        try {
            ProcessOutput output = new CapturingProcessHandler(
                    command.createProcess(),
                    Charset.defaultCharset(),
                    command.getCommandLineString()).runProcess();

            if (output.getExitCode() != 0) {
                LOG.error("Go command exited with invalid exit code: " +
                        output.getExitCode());
                return null;
            }

            String outputString = output.getStdout();

            Matcher matcher = RE_OS_MATCHER.matcher(outputString);
            if (matcher.find()) {
                sdkData.TARGET_OS = GoTargetOs.fromString(matcher.group(1));
            }

            matcher = RE_ARCH_MATCHER.matcher(outputString);
            if (matcher.find()) {
                sdkData.TARGET_ARCH = GoTargetArch.fromString(matcher.group(1));
            }
        } catch (ExecutionException e) {
            LOG.error("Exception while executing the process:", e);
        }

        try {
            VirtualFile filePath = VfsUtil.findFileByIoFile(new File(format("%s/VERSION", path)), true);
            if (filePath == null) {
                return null;
            }
            String fileContent = VfsUtil.loadText(filePath);

            Matcher matcher = RE_APP_ENGINE_VERSION_MATCHER.matcher(
                    fileContent);

            if (!matcher.find())
                return null;
            sdkData.VERSION_MAJOR = matcher.group(1);

            matcher = RE_APP_ENGINE_TIMESTAMP_MATCHER.matcher(fileContent);
            if (!matcher.find())
                return null;
            sdkData.VERSION_MINOR = matcher.group(1);

            matcher = RE_APP_ENGINE_API_VERSIONS_MATCHER.matcher(fileContent);
            if (!matcher.find())
                return null;

            sdkData.API_VERSIONS = matcher.group(1);

            sdkData.version = GoAppEngineSdkData.LATEST_VERSION;

        } catch (IOException e) {
            return null;
        }

        return sdkData;
    }

    @NotNull
    public static String getGoAppBinPath(String goAppPath) {
        String goExecName = goAppPath + File.separator + "goapp";

        if (isHostOsWindows()) {
            goExecName = goExecName.concat(".exe");
        }

        return goExecName;
    }

    private static boolean checkFileExists(String path, String child) {
        return checkFileExists(new File(path, child));
    }

    public static boolean checkFileExists(String path) {
        return checkFileExists(new File(path));
    }

    public static boolean checkFileExists(File file) {
        return file.exists() && file.isFile();
    }

    public static boolean checkFolderExists(String path) {
        return checkFolderExists(new File(path));
    }

    public static boolean checkFolderExists(File file) {
        return file.exists() && file.isDirectory();
    }

    public static boolean checkFolderExists(String path, String child) {
        return checkFolderExists(new File(path, child));
    }

    public static boolean checkFolderExists(String path, String child, String child2) {
        return checkFolderExists(new File(new File(path, child), child2));
    }

    private static String getArchivePackerName() {
        return "gopack";
    }

    public static String getCompilerName(GoTargetArch arch) {
        return getBinariesDesignation(arch) + "g";
    }

    public static String getLinkerName(GoTargetArch arch) {
        return getBinariesDesignation(arch) + "l";
    }

    public static String getBinariesDesignation(GoTargetArch arch) {

        switch (arch) {
            case _386:
                return "8";

            case _amd64:
                return "6";

            case _arm:
                return "5";
        }

        return "unknown";
    }

    public static Sdk getGoogleGoSdkForModule(Module module) {

        ModuleRootModel moduleRootModel = ModuleRootManager.getInstance(module);

        Sdk sdk;
        if (!moduleRootModel.isSdkInherited()) {
            sdk = moduleRootModel.getSdk();
        } else {
            sdk = ProjectRootManager.getInstance(module.getProject())
                    .getProjectSdk();
        }

        if (GoSdkType.isInstance(sdk)) {
            return sdk;
        }

        return null;
    }

    public static Sdk getGoogleGoSdkForProject(Project project) {

        Sdk sdk = ProjectRootManager.getInstance(project).getProjectSdk();

        if (GoSdkType.isInstance(sdk)) {
            return sdk;
        }

        return null;
    }

    public static Sdk getGoogleGAESdkForProject(Project project) {

        Sdk sdk = ProjectRootManager.getInstance(project).getProjectSdk();

        if (GoAppEngineSdkType.isInstance(sdk)) {
            return sdk;
        }

        return null;
    }

    public static Sdk getGoogleGoSdkForFile(PsiFile file) {
        ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(
                file.getProject()).getFileIndex();
        Module module = projectFileIndex.getModuleForFile(
                file.getVirtualFile());

        return getGoogleGoSdkForModule(module);
    }

    public static String getToolName(GoTargetArch arch, GoSdkTool tool) {

        String binariesDesignation = getBinariesDesignation(arch);

        switch (tool) {
            case GoCompiler:
                return binariesDesignation + "g";
            case GoLinker:
                return binariesDesignation + "l";
            case GoArchivePacker:
                return "pack";
            case GoMake:
                return "gomake";
            case GoFmt:
                return "gofmt";
        }

        return "";
    }

    public static String resolvePotentialGoogleGoAppEngineHomePath() {

        if (!isSdkRegistered(
                PathManager.getHomePath() + "/bundled/go-appengine-sdk",
                GoAppEngineSdkType
                        .getInstance())) {
            return PathManager.getHomePath() + "/bundled/go-appengine-sdk";
        }

        String path = System.getenv("PATH");
        if (path == null) {
            return null;
        }

        String[] parts = path.split("[:;]+");
        for (String part : parts) {
            if (!isSdkRegistered(part, GoAppEngineSdkType.getInstance())) {
                return part;
            }
        }

        return SystemProperties.getUserHome();
    }


    public static String resolvePotentialGoogleGoHomePath() {

        if (!isSdkRegistered(PathManager.getHomePath() + "/bundled/go-sdk",
                GoSdkType.getInstance())) {
            return PathManager.getHomePath() + "/bundled/go-sdk";
        }

        String goRoot = System.getenv(ENV_GO_ROOT);
        if (goRoot != null && !isSdkRegistered(goRoot,
                GoSdkType.getInstance())) {
            return goRoot;
        }

        String command = "go";
        if (GoUtil.testGoHomeFolder("/usr/lib/go") &&
                new File("/usr/lib/go/bin/go").canExecute()) {
            command = "/usr/lib/go/bin/go";
        } else if (GoUtil.testGoHomeFolder("/usr/local/go") &&
                new File("/usr/local/go/bin/go").canExecute()) {
            command = "/usr/local/go/bin/go";
        }

        GeneralCommandLine goCommandLine = new GeneralCommandLine();

        goCommandLine.setExePath(command);
        goCommandLine.addParameter("env");

        LOG.info("command: " + command);

        try {
            ProcessOutput output = new CapturingProcessHandler(
                    goCommandLine.createProcess(),
                    Charset.defaultCharset(),
                    goCommandLine.getCommandLineString()).runProcess();

            if (output.getExitCode() == 0) {
                String outputString = output.getStdout();

                LOG.info("Output:\n" + outputString);
                Matcher matcher = RE_ROOT_MATCHER.matcher(outputString);
                if (matcher.find()) {
                    LOG.info("Matched: " + matcher.group(1));
                    return matcher.group(1);
                }
            }
        } catch (ExecutionException e) {
            //
        }

        return SystemProperties.getUserHome();
    }

    private static boolean isSdkRegistered(String homePath, SdkType sdkType) {

        VirtualFile homePathAsVirtualFile;
        homePathAsVirtualFile = VfsUtil.findFileByIoFile(new File(homePath), true);
        /* Use the above because the following does not work on Windows:
        try {
            homePathAsVirtualFile = VfsUtil.findFileByURL(
                    new URL(VfsUtil.pathToUrl(homePath)));
        } catch (MalformedURLException e) {
            return true;
        }*/

        if (homePathAsVirtualFile == null || !homePathAsVirtualFile.isDirectory()) {
            return true;
        }

        List<Sdk> registeredSdks = GoSdkUtil.getSdkOfType(sdkType);

        for (Sdk registeredSdk : registeredSdks) {
            if (homePathAsVirtualFile.equals(
                    registeredSdk.getHomeDirectory())) {
                return true;
            }
        }

        return false;
    }


    public static List<Sdk> getSdkOfType(SdkType sdkType) {
        return getSdkOfType(sdkType, ProjectJdkTable.getInstance());
    }

    public static List<Sdk> getSdkOfType(SdkType sdkType, ProjectJdkTable table) {
        Sdk[] sdks = table.getAllJdks();

        List<Sdk> goSdks = new LinkedList<Sdk>();
        for (Sdk sdk : sdks) {
            if (sdk.getSdkType() == sdkType) {
                goSdks.add(sdk);
            }
        }

        return goSdks;
    }

    public static String prependToGoPath(String prependedPath) {
        String sysGoPath = getGoPath();
        return sysGoPath.isEmpty()
                ? prependedPath
                : format("%s%s%s", prependedPath, File.pathSeparator, sysGoPath);
    }

    public static String appendToGoPath(String appendedPath) {
        String sysGoPath = getGoPath();
        return sysGoPath.isEmpty()
                ? appendedPath
                : format("%s%s%s", sysGoPath, File.pathSeparator, appendedPath);
    }

    public static String getProjectGoPath(String projectDir, boolean prependSysGoPath, boolean appendSysGoPath) {
        String goPath = projectDir;
        String sysGoPath = getGoPath();
        if (!sysGoPath.isEmpty()) {
            if (prependSysGoPath) {
                goPath = format("%s%s%s", sysGoPath, File.pathSeparator, goPath);
            }
            if (appendSysGoPath) {
                goPath = format("%s%s%s", goPath, File.pathSeparator, sysGoPath);
            }
        }
        return goPath;
    }

    public static String appendGoPathToPath(String goPath) {
        String binarizedPath = "";
        String[] splitGoPath = goPath.split(File.pathSeparator);

        for (String path : splitGoPath) {
            binarizedPath += path + File.separator + "bin" + File.pathSeparator;
        }

        binarizedPath = binarizedPath.substring(0, binarizedPath.length() - 1);

        String sysPath = getEnvVariable("PATH");
        return sysPath.isEmpty()
                ? binarizedPath
                : format("%s%s%s", sysPath, File.pathSeparator, binarizedPath);
    }

    @NotNull
    public static String getAppEngineDevServer() {
        return getEnvVariable("APPENGINE_DEV_APPSERVER");
    }

    @NotNull
    public static String getSysGoRootPath() {
        return getEnvVariable("GOROOT");
    }

    @NotNull
    public static String getSysGoPathPath() {
        return getEnvVariable("GOPATH").split(File.pathSeparator)[0];
    }

    public static String getGoPath() {
        String savedsGoPath = GoGlobalSettings.getInstance().getGoPath();

        if (!savedsGoPath.isEmpty()) {
            return savedsGoPath;
        }

        return getSysGoPathPath();
    }

    @NotNull
    private static String getEnvVariable(String varName) {
        String variable = System.getenv(varName);
        return variable == null ? "" : variable;
    }

    public static boolean isImportedPackage(Project project, String packageName) {
        if (GoNamesCache.getInstance(project).isGoDefaultPackage(packageName)) {
            return false;
        }

        String topPackageName = packageName.split("/")[0];

        //noinspection SimplifiableIfStatement
        if (!topPackageName.contains(".")) {
            return false;
        }

        return checkFolderExists(getGoPath() + File.separator + "src" + File.separator + topPackageName);
    }

    @NotNull
    public static String getPathToDisplay(final VirtualFile file) {
        if (file == null) {
            return "";
        }
        return FileUtil.toSystemDependentName(file.getPath());
    }

    public static VirtualFile getVirtualFile(String path) {
        File pluginPath = new File(path);

        if (!pluginPath.exists()) {
            return null;
        }

        String url = VfsUtil.pathToUrl(pluginPath.getAbsolutePath());

        return VirtualFileManager.getInstance().findFileByUrl(url);
    }

    public static Map<String, String> getExtendedSysEnv(GoSdkData sdkData, String projectDir, String envVars) {
        boolean prependSysGoPath = false;
        boolean appendSysGoPath = false;

        Project[] projects = ProjectManager.getInstance().getOpenProjects();
        Project project = null;

        for (Project p : projects) {
            if (p.getBasePath().equals(projectDir)) {
                project = p;
                break;
            }
        }

        if (project != null) {
            GoProjectSettings.GoProjectSettingsBean settings = GoProjectSettings.getInstance(project).getState();
            prependSysGoPath = settings.prependGoPath;
            appendSysGoPath = settings.useGoPath;
        }

        return getExtendedSysEnv(sdkData, projectDir, envVars, prependSysGoPath, appendSysGoPath);
    }

    public static Map<String, String> getExtendedSysEnv(GoSdkData sdkData, String projectDir, String envVars, boolean prependSysGoPath, boolean appendSysGoPath) {
        Map<String, String> sysEnv = new HashMap<String, String>(System.getenv());
        String goRoot = getSdkRootPath(sdkData);
        String goPath = getProjectGoPath(projectDir, prependSysGoPath, appendSysGoPath);
        sysEnv.put("GOROOT", goRoot);
        sysEnv.put("GOPATH", goPath);
        sysEnv.put("PATH", appendGoPathToPath(goRoot + File.pathSeparator + goPath));

        if (envVars.length() > 0) {
            String[] envVarsArray = envVars.split(";");
            for (String envVar : envVarsArray) {
                if (!envVar.contains("=")) {
                    continue;
                }
                String[] splitEnvVars = envVar.split("=");
                if (splitEnvVars.length != 2) {
                    continue;
                }
                sysEnv.put(splitEnvVars[0], splitEnvVars[1]);
            }
        }

        return sysEnv;
    }

    public static Map<String, String> getExtendedSysEnv(GoAppEngineSdkData sdkData, String projectDir, String envVars) {
        return getExtendedSysEnv(sdkData, projectDir, envVars, true, false);
    }

    public static Map<String, String> getExtendedSysEnv(GoAppEngineSdkData sdkData, String projectDir, String envVars, boolean prependSysGoPath, boolean appendSysGoPath) {
        Map<String, String> sysEnv = new HashMap<String, String>(System.getenv());
        String goRoot = getSdkRootPath(sdkData);
        String goPath = getProjectGoPath(projectDir, prependSysGoPath, appendSysGoPath);

        sysEnv.put("GOROOT", goRoot);
        sysEnv.put("GOPATH", goPath);
        sysEnv.put("PATH", appendGoPathToPath(goRoot + File.pathSeparator + goPath));

        if (envVars.length() > 0) {
            String[] envVarsArray = envVars.split(";");
            for (String envVar : envVarsArray) {
                if (!envVar.contains("=")) {
                    continue;
                }
                String[] splitEnvVars = envVar.split("=");
                if (splitEnvVars.length != 2) {
                    continue;
                }
                sysEnv.put(splitEnvVars[0], splitEnvVars[1]);
            }
        }

        return sysEnv;
    }

    public static String[] convertEnvMapToArray(Map<String, String> envMap) {
        String[] goEnv = new String[envMap.size()];

        Iterator it = envMap.entrySet().iterator();
        int i = 0;
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            goEnv[i] = pairs.getKey() + "=" + pairs.getValue();
            i++;
        }

        return goEnv;
    }

    public static String[] getExtendedGoEnv(GoSdkData sdkData, String projectDir, String envVars) {
        return convertEnvMapToArray(getExtendedSysEnv(sdkData, projectDir, envVars));
    }

    public static String[] getExtendedGAEEnv(GoAppEngineSdkData sdkData, String projectDir, String envVars) {
        return convertEnvMapToArray(getExtendedSysEnv(sdkData, projectDir, envVars));
    }

    public static String[] getExtendedGoEnv(GoSdkData sdkData, String projectDir, String envVars, boolean prependSysGoPath, boolean appendSysGoPath) {
        return convertEnvMapToArray(getExtendedSysEnv(sdkData, projectDir, envVars, prependSysGoPath, appendSysGoPath));
    }

    public static String[] getExtendedGAEEnv(GoAppEngineSdkData sdkData, String projectDir, String envVars, boolean prependSysGoPath, boolean appendSysGoPath) {
        return convertEnvMapToArray(getExtendedSysEnv(sdkData, projectDir, envVars, prependSysGoPath, appendSysGoPath));
    }

    public static String getSdkRootPath(GoSdkData sdkData) {
        if (sdkData.GO_GOROOT_PATH.isEmpty()) {
            File possibleRoot = new File(sdkData.GO_BIN_PATH).getParentFile();
            try {
                if (new File(possibleRoot.getCanonicalPath().concat("/src")).exists()) {
                    return possibleRoot.getCanonicalPath();
                }
            } catch (IOException ignored) {
                return "";
            }

            try {
                return possibleRoot.getParentFile().getCanonicalPath();
            } catch (IOException e) {
                return "";
            }
        }
        return sdkData.GO_GOROOT_PATH;
    }

    public static String getSdkRootPath(GoAppEngineSdkData sdkData) {
        if (sdkData.GO_HOME_PATH.isEmpty()) {
            File possibleRoot = new File(sdkData.GO_HOME_PATH);
            try {
                if (new File(possibleRoot.getCanonicalPath().concat("/src")).exists()) {
                    return possibleRoot.getCanonicalPath();
                }
            } catch (IOException ignored) {
                return "";
            }

            try {
                return possibleRoot.getParentFile().getCanonicalPath();
            } catch (IOException e) {
                return "";
            }
        }
        return sdkData.GO_HOME_PATH;
    }

    public static Boolean isHostOsWindows() {
        return SystemInfo.isWindows;
    }

    @NotNull
    public static String getGaeExePath() {
        String gaeExec = "goapp";
        if (isHostOsWindows()) {
            gaeExec += ".exe";
        }

        String[] sysPaths = getEnvVariable("PATH").split(File.pathSeparator);

        for (String sysPath : sysPaths) {
            if (!checkFileExists(sysPath + File.separator + gaeExec)) {
                continue;
            }

            return sysPath;
        }

        return "/usr/lib/go_appengine";
    }

    @Nullable
    public static Sdk getProjectSdk(Project project) {
        Sdk sdk = GoSdkUtil.getGoogleGoSdkForProject(project);
        if (sdk != null) {
            return sdk;
        }

        return GoSdkUtil.getGoogleGAESdkForProject(project);
    }

    @Nullable
    public static String getGoExecName(Sdk sdk) {
        if (sdk.getSdkType() instanceof GoSdkType) {
            GoSdkData sdkData = (GoSdkData) sdk.getSdkAdditionalData();
            if (sdkData == null) {
                return null;
            }

            return sdkData.GO_BIN_PATH;
        } else if (sdk.getSdkAdditionalData() instanceof GoAppEngineSdkData) {
            GoAppEngineSdkData sdkData = (GoAppEngineSdkData) sdk.getSdkAdditionalData();
            return sdkData.GOAPP_BIN_PATH;
        }

        return null;
    }

    @Nullable
    public static String[] getGoEnv(Sdk sdk, String projectDir) {
        if (sdk.getSdkType() instanceof GoSdkType) {
            GoSdkData sdkData = (GoSdkData) sdk.getSdkAdditionalData();
            if (sdkData == null) {
                return null;
            }

            return GoSdkUtil.getExtendedGoEnv(sdkData, projectDir, "");
        } else if (sdk.getSdkAdditionalData() instanceof GoAppEngineSdkData) {
            GoAppEngineSdkData sdkData = (GoAppEngineSdkData) sdk.getSdkAdditionalData();
            return GoSdkUtil.getExtendedGAEEnv(sdkData, projectDir, "");
        }

        return null;
    }

    @Nullable
    public static Icon getProjectIcon(Sdk sdk) {
        return getProjectIcon(sdk, 16);
    }

    @Nullable
    public static Icon getProjectIcon(Sdk sdk, Integer size) {
        if (sdk.getSdkType() instanceof GoSdkType) {
            GoSdkData sdkData = (GoSdkData) sdk.getSdkAdditionalData();
            if (sdkData == null) {
                return null;
            }

            switch (size) {
                case 13:
                    return GoIcons.GO_ICON_13x13;
                case 24:
                    return GoIcons.GO_ICON_24x24;
                case 32:
                    return GoIcons.GO_ICON_32x32;
                case 48:
                    return GoIcons.GO_ICON_48x48;
                default:
                    return GoIcons.GO_ICON_16x16;
            }
        } else if (sdk.getSdkAdditionalData() instanceof GoAppEngineSdkData) {
            switch (size) {
                case 13:
                    return GoIcons.GAE_ICON_13x13;
                case 24:
                    return GoIcons.GAE_ICON_24x24;
                case 32:
                    return GoIcons.GAE_ICON_32x32;
                case 48:
                    return GoIcons.GAE_ICON_48x48;
                default:
                    return GoIcons.GAE_ICON_16x16;
            }
        }

        return null;
    }

    public static String[] computeGoCommand(String goName, String goArgs) {
        List<String> result = new ArrayList<String>();
        result.add(goName);

        Pattern pattern = Pattern.compile("((([^\"\\s\\\\]+(\\\\[^\\\\])*)+)|(\"([^\"\\\\]*|\\\\\")*\"))+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(goArgs);
        while (matcher.find()) {
            String group = matcher.group();
            if (group.indexOf('"') == 0 || group.indexOf("'") == 0) {
                result.add(group.substring(1, group.length() - 1));
            } else if (group.indexOf('-') == 0 && group.indexOf(' ') != -1) {
                Collections.addAll(result, group.split(" "));
            } else {
                result.add(group);
            }
        }

        String[] arrRes = new String[result.size()];
        result.toArray(arrRes);

        return arrRes;
    }

    public static String[] computeGoBuildCommand(String goExecName, String goBuilderArgs, String targetName, String goMainFile) {
        String goArgs = String.format(
                "%s %s %s %s %s",
                "build",
                "-o",
                targetName,
                goBuilderArgs,
                goMainFile
        );

        return computeGoCommand(goExecName, goArgs);
    }

    public static String[] computeGoGetCommand(String goExecName, String goGetArgs, String packageName) {
        String goArgs = String.format(
                "%s %s %s",
                "get",
                goGetArgs,
                packageName
        );

        return computeGoCommand(goExecName, goArgs);
    }

    /**
     * Returns the Package, which the file belongs to
     *
     * @param projectRoot Folder, where the project is located
     * @param file        The file, which package we want to find out
     * @return String
     */
    public static String getPackageOfFile(String projectRoot, String file) {
        String pkg = null;

        // More portable solution than "file.startsWith(projectRoot)" as in Windows it creates problem
        // due to path separator character
        String root = new File(projectRoot).getAbsolutePath();
        String child = new File(file).getAbsolutePath();

        if (child.startsWith(root)) {
            String src = File.separator + "src" + File.separator;

            String fileFolder = new File(file).getParent();

            String relativePath = fileFolder.substring(projectRoot.length());
            if (relativePath.startsWith(src)) {
                pkg = relativePath.substring(src.length());
            } else {
                pkg = relativePath;
            }
        }

        return pkg;
    }

    public static String[] computeGoRunCommand(String goExecName, String goBuilderArgs, String goMainFile, String appArgs) {
        String goArgs = String.format(
                "%s %s %s %s",
                "run",
                goBuilderArgs,
                goMainFile,
                appArgs
        );

        return computeGoCommand(goExecName, goArgs);
    }

    public static String getGoImportsExec(String goimportsPath) {
        String execPath;

        if (!goimportsPath.equals("")) {
            execPath = goimportsPath;
        } else {
            execPath = GoSdkUtil.getGoPath() + File.separator + "bin";
        }

        execPath = execPath + File.separator + "goimports";
        if (isHostOsWindows()) {
            execPath += ".exe";
        }

        return execPath;
    }
}
