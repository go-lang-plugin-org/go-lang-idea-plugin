package ro.redeul.google.go.sdk;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.CapturingProcessHandler;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ModuleRootModel;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.PsiFile;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.config.sdk.GoSdkType;
import ro.redeul.google.go.util.GoUtil;

import java.io.File;
import java.io.FileFilter;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoSdkUtil {

    public static final String PACKAGES = "src/pkg";

    private static final Logger LOG = Logger.getInstance("ro.redeul.google.go.sdk.GoSdkUtil");
    private static final String TEST_SDK_PATH = "go.test.sdk.home";

    private static final String DEFAULT_MOCK_PATH = "go/default";

    // 8g version release.r59 9305
    private static Pattern RE_VERSION_MATCHER = Pattern.compile("([^ ]+) version ([^ ]+) (.+)$");


    @SuppressWarnings({"SynchronizationOnLocalVariableOrMethodParameter"})
    public static GoSdkData testGoogleGoSdk(String path) {

        File rootFile = new File(path);

        // check that the selection was a folder
        if (!rootFile.exists() || !rootFile.isDirectory()) {
            return null;
        }

        // check to see if we have a %GOROOT/src folder
        File srcFolder = new File(rootFile, "src");
        if (!srcFolder.exists() || !srcFolder.isDirectory()) {
            return null;
        }

        // check to see if we have a %GOROOT/pkg folder
        File pkgFolder = new File(rootFile, "pkg");
        if (!pkgFolder.exists() || !pkgFolder.isDirectory()) {
            return null;
        }

        File targets[] = pkgFolder.listFiles(new FileFilter() {
            public boolean accept(File pathName) {
                return pathName.isDirectory() && !pathName.getName().matches("\\.{1,2}");
            }
        });

        if (targets.length != 1 || !targets[0].getName().matches("(windows|linux|darwin|freebsd)_(386|amd64|arm)")) {
            return null;
        }

        String[] target = targets[0].getName().split("_");

        GoSdkData.Os targetOs = GoSdkData.Os.fromString(target[0]);
        GoSdkData.Arch targetArch = GoSdkData.Arch.fromString(target[1]);

        String compilerName = getCompilerName(targetOs, targetArch);

        String binariesPath = System.getenv("GOBIN");
        if (binariesPath == null) {
            binariesPath = path + "/bin";

            if ( ! (new File(binariesPath).isDirectory()) ) {
                binariesPath = "/usr/bin";
            }
        }

        GeneralCommandLine command = new GeneralCommandLine();
        command.setExePath(binariesPath + "/" + compilerName);
        command.addParameter("-V");
        command.setWorkDirectory(binariesPath);

        try {
            ProcessOutput output = new CapturingProcessHandler(
                    command.createProcess(),
                    Charset.defaultCharset(),
                    command.getCommandLineString()).runProcess();

            if (output.getExitCode() != 0) {
                LOG.error("Go compiler exited with invalid exit code: " + output.getExitCode());
                return null;
            }

            String outputString = output.getStdout().replaceAll("[\r\n]+", "");

            Matcher matcher = RE_VERSION_MATCHER.matcher(outputString);
            if ( matcher.matches() ) {
                return new GoSdkData(path, binariesPath, targetOs, targetArch, matcher.group(2), matcher.group(3));
            }

            return null;
        } catch (ExecutionException e) {
            LOG.error("Exception while executing the process:", e);
            return null;
        }
    }

    /**
     * Uses the following to get the go sdk for tests:
     *  1. Uses the path given by the system property go.test.sdk.home, if given
     *  2. Uses the path given by the GOROOT environment variable, if available
     *  3. Uses HOMEPATH/go/default
     * @return the go sdk parameters or array of zero elements if error
     */
    public static GoSdkData getMockGoogleSdk() {
        // Fallback to default home path / default mock path
        String sdkPath = PathManager.getHomePath() + "/" + DEFAULT_MOCK_PATH;

        String testSdkHome = System.getProperty(TEST_SDK_PATH);
        String goRoot = GoUtil.resolvePotentialGoogleGoHomePath();

        // Use the test sdk path before anything else, if available
        if (testSdkHome != null) {
            sdkPath = testSdkHome;
        }
        else if (goRoot != null) {
            sdkPath = goRoot;
        }

        return getMockGoogleSdk(sdkPath);
    }

    public static GoSdkData getMockGoogleSdk(String path) {
        GoSdkData sdkData = testGoogleGoSdk(path);
        if (sdkData != null ) {
            new File(sdkData.BIN_PATH, getCompilerName(sdkData.TARGET_OS, sdkData.TARGET_ARCH)).setExecutable(true);
            new File(sdkData.BIN_PATH, getLinkerName(sdkData.TARGET_OS, sdkData.TARGET_ARCH)).setExecutable(true);
            new File(sdkData.BIN_PATH, getArchivePackerName(sdkData.TARGET_OS, sdkData.TARGET_ARCH)).setExecutable(true);
        }

        return sdkData;
    }

    private static String getArchivePackerName(GoSdkData.Os os, GoSdkData.Arch arch) {
        return "gopack";
    }

    public static String getCompilerName(GoSdkData.Os os, GoSdkData.Arch arch) {
        return getBinariesDesignation(os, arch) + "g";
    }

    public static String getLinkerName(GoSdkData.Os os, GoSdkData.Arch arch) {
        return getBinariesDesignation(os, arch) + "l";
    }

    public static String getBinariesDesignation(GoSdkData.Os os, GoSdkData.Arch arch) {

        switch (arch) {
            case _386:
                return "8";

            case _amd64:
                return  "6";

            case _arm:
                return "5";
        }

        return "unknown";
    }

    public static Sdk getGoogleGoSdkForModule(Module module) {

        ModuleRootModel moduleRootModel = ModuleRootManager.getInstance(module);

        Sdk sdk = null;
        if ( ! moduleRootModel.isSdkInherited() ) {
            sdk = moduleRootModel.getSdk();
        } else {
            sdk = ProjectRootManager.getInstance(module.getProject()).getProjectSdk();
        }

        if ( GoSdkType.isInstance(sdk) ) {
            return sdk;
        }

        return null;
    }

    public static Sdk getGoogleGoSdkForProject(Project project) {

        Sdk sdk = ProjectRootManager.getInstance(project).getProjectSdk();

        if ( GoSdkType.isInstance(sdk) ) {
            return sdk;
        }

        return null;
    }

    public static Sdk getGoogleGoSdkForFile(PsiFile file) {
        ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(file.getProject()).getFileIndex();
        Module module = projectFileIndex.getModuleForFile(file.getVirtualFile());

        return getGoogleGoSdkForModule(module);
    }

    public static String getTool(GoSdkData goSdkData, GoSdkTool tool) {
        return String.format("%s/%s", goSdkData.BIN_PATH, getToolName(goSdkData.TARGET_OS, goSdkData.TARGET_ARCH, tool));
    }

    public static String getToolName(GoSdkData.Os os, GoSdkData.Arch arch, GoSdkTool tool) {

        String binariesDesignation = getBinariesDesignation(os, arch);

        switch (tool) {
            case GoCompiler:
                return binariesDesignation + "g";
            case GoLinker:
                return binariesDesignation + "l";
            case GoArchivePacker:
                return "gopack";
            case GoMake:
                return "gomake";
            case GoFmt:
                return "gofmt";
        }

        return "";
    }
}
