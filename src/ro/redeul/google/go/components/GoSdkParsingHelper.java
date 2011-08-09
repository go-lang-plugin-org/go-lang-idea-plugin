package ro.redeul.google.go.components;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.AdapterProcessor;
import com.intellij.util.CommonProcessors;
import com.intellij.util.FilteringProcessor;
import com.intellij.util.Function;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.config.sdk.GoSdkType;
import ro.redeul.google.go.lang.psi.GoFile;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 8/9/11
 * Time: 12:52 PM
 */
public class GoSdkParsingHelper implements ApplicationComponent {

    Map<Sdk, Map<String, String>> sdkPackageMappings = new HashMap<Sdk, Map<String, String>>();

    private final static Pattern RE_PACKAGE_TARGET = Pattern.compile("^TARG=([^\\s]+)\\s*$", Pattern.MULTILINE);

    public static GoSdkParsingHelper getInstance() {
        return ApplicationManager.getApplication().getComponent(GoSdkParsingHelper.class);
    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "GoSdkParsingHelper";
    }

    public String getPackageImportPath(Project project, GoFile goFile) {
        if (goFile == null) {
            return "";
        }

        VirtualFile virtualFile = goFile.getVirtualFile();
        if (virtualFile == null) {
            virtualFile = goFile.getUserData(FileBasedIndex.VIRTUAL_FILE);
        }

        if (virtualFile == null || !virtualFile.getName().matches(".*\\.go")) {
            return "";
        }

        ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(project).getFileIndex();

        ProjectJdkTable jdkTable = ProjectJdkTable.getInstance();
        List<Sdk> sdkList = jdkTable.getSdksOfType(GoSdkType.getInstance());

        Sdk ownerSdk = null;

        VirtualFile ownerSdkRoot = null;
        if (projectFileIndex.isInLibrarySource(virtualFile)) {
            VirtualFile classPathRoot = projectFileIndex.getClassRootForFile(virtualFile);

            for (Sdk sdk : sdkList) {
                VirtualFile sdkRoots[] = sdk.getRootProvider().getFiles(OrderRootType.CLASSES);
                for (VirtualFile sdkRoot : sdkRoots) {
                    if (sdkRoot.equals(classPathRoot)) {
                        ownerSdkRoot = sdkRoot;
                        ownerSdk = sdk;
                        break;
                    }
                }

                if (ownerSdk != null) {
                    break;
                }
            }
        }

        if (ownerSdk == null) {
            return "";
        }

        Map<String, String> mappings = sdkPackageMappings.get(ownerSdk);
        if (mappings == null) {
            mappings = findPackageMappings(ownerSdk);
            sdkPackageMappings.put(ownerSdk, mappings);
        }

        String relativePath = VfsUtil.getRelativePath(virtualFile.getParent(), ownerSdkRoot, '/');
        if (relativePath != null ) {
            return mappings.get(relativePath);
        }

        return "";
    }

    private Map<String, String> findPackageMappings(Sdk ownerSdk) {
        Map<String, String> result = new HashMap<String, String>();

        if (ownerSdk.getSdkType() != GoSdkType.getInstance())
            return result;


        VirtualFile home = ownerSdk.getHomeDirectory();
        if (home == null) {
            return result;
        }

        // find libraries
        final VirtualFile packageRoot = home.findFileByRelativePath("pkg");
        if (packageRoot == null) {
            return result;
        }

        CommonProcessors.CollectUniquesProcessor<String> libraryNames = new CommonProcessors.CollectUniquesProcessor<String>();

        GoSdkData sdkData = (GoSdkData) ownerSdk.getSdkAdditionalData();

        final VirtualFile librariesRoot =
                sdkData != null
                    ? packageRoot.findFileByRelativePath(String.format("%s_%s", sdkData.TARGET_OS, sdkData.TARGET_ARCH))
                    : packageRoot;

        if ( librariesRoot == null ) {
            return result;
        }

        VfsUtil.processFilesRecursively(librariesRoot,
                new FilteringProcessor<VirtualFile>(
                        new Condition<VirtualFile>() {
                            @Override
                            public boolean value(VirtualFile virtualFile) {
                                return !virtualFile.isDirectory() && virtualFile.getName().matches(".*\\.a");
                            }
                        },
                        new AdapterProcessor<VirtualFile, String>(
                                libraryNames,
                                new Function<VirtualFile, String>() {
                                    @Override
                                    public String fun(VirtualFile virtualFile) {
                                        String relativePath = VfsUtil.getRelativePath(virtualFile, librariesRoot, '/');
                                        return relativePath != null ? relativePath.replaceAll("\\.a$", "") : "";
                                    }
                                }
                        ))
        );

        Set<String> librariesSet = new HashSet<String>(libraryNames.getResults());

        // find makefiles
        CommonProcessors.CollectUniquesProcessor<VirtualFile> makefiles = new CommonProcessors.CollectUniquesProcessor<VirtualFile>();
        final VirtualFile sourcesRoot = home.findFileByRelativePath("src/pkg");
        if (sourcesRoot == null) {
            return result;
        }

        VfsUtil.processFilesRecursively(sourcesRoot,
                new FilteringProcessor<VirtualFile>(
                        new Condition<VirtualFile>() {
                            @Override
                            public boolean value(VirtualFile virtualFile) {
                                return virtualFile.getName().equals("Makefile");
                            }
                        },
                        makefiles
                ));

        for (VirtualFile makefile : makefiles.getResults() ) {
            try {
                String content = new String(makefile.contentsToByteArray(), "UTF-8");

                Matcher matcher = RE_PACKAGE_TARGET.matcher(content);
                if ( matcher.find() ) {
                    String libraryName = matcher.group(1);
                    if ( librariesSet.contains(libraryName) ) {
                        result.put(VfsUtil.getRelativePath(makefile.getParent(), sourcesRoot, '/'), libraryName);
                    }
                }
            } catch (IOException e) {
                //
            }
        }

        return result;
    }
}
