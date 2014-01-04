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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.config.sdk.GoAppEngineSdkData;
import ro.redeul.google.go.config.sdk.GoAppEngineSdkType;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.config.sdk.GoSdkType;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.sdk.GoSdkUtil;
import ro.redeul.google.go.util.GoUtil;

import java.util.*;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 8/9/11
 * Time: 12:52 PM
 */
public class GoSdkParsingHelper implements ApplicationComponent {

    private final Map<Sdk, Map<String, String>> sdkPackageMappings = new HashMap<Sdk, Map<String, String>>();

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

    @Nullable
    public synchronized String getPackageImportPath(Project project,
                                                    GoFile goFile,
                                                    VirtualFile virtualFile) {

        if (goFile == null) {
            return null;
        }

        if (virtualFile == null || !virtualFile.getName().matches(".*\\.go")) {
            return null;
        }

        ProjectFileIndex projectFileIndex =
            ProjectRootManager.getInstance(project).getFileIndex();

        ProjectJdkTable jdkTable = ProjectJdkTable.getInstance();
        List<Sdk> sdkList = new ArrayList<Sdk>();


        sdkList.addAll(GoSdkUtil.getSdkOfType(GoSdkType.getInstance(), jdkTable));
        sdkList.addAll(GoSdkUtil.getSdkOfType(GoAppEngineSdkType.getInstance(), jdkTable));

        Sdk ownerSdk = null;

        VirtualFile ownerSdkRoot = null;
        if (projectFileIndex.isInLibraryClasses(virtualFile)) {
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
            return null;
        }

        Map<String, String> mappings = sdkPackageMappings.get(ownerSdk);
        if (mappings == null) {
            mappings = findPackageMappings(ownerSdk);
            sdkPackageMappings.put(ownerSdk, mappings);
        }

        String relativePath = VfsUtil.getRelativePath(virtualFile.getParent(), ownerSdkRoot, '/');
        if (relativePath != null && mappings.containsKey(relativePath) ) {
            return mappings.get(relativePath);
        }

        return relativePath;
    }

    private String getPackageImportPathFromProject(ProjectFileIndex projectIndex, VirtualFile virtualFile) {

        VirtualFile contentRoot = projectIndex.getContentRootForFile(virtualFile);
        if ( contentRoot == null ) {
            return "";
        }

        String relativePath = VfsUtil.getRelativePath(virtualFile, contentRoot.getParent(), '/');
        if ( relativePath == null ) {
            return "";
        }

        return "";
    }

    private Map<String, String> findPackageMappings(Sdk ownerSdk) {
        Map<String, String> result = new HashMap<String, String>();

        if (ownerSdk.getSdkType() != GoSdkType.getInstance() && ownerSdk.getSdkType() != GoAppEngineSdkType.getInstance())
            return result;

        VirtualFile home = ownerSdk.getHomeDirectory();
        if (home == null) {
            return result;
        }

        String activeTarget = "";
        VirtualFile goRoot = home;

        if ( ownerSdk.getSdkType() == GoAppEngineSdkType.getInstance() ) {
            goRoot = home.findFileByRelativePath("goroot");

            GoAppEngineSdkData sdkData = (GoAppEngineSdkData) ownerSdk.getSdkAdditionalData();
            if ( sdkData != null && sdkData.TARGET_OS != null && sdkData.TARGET_ARCH != null ) {
                activeTarget = String.format("%s_%s", sdkData.TARGET_OS.getName(), sdkData.TARGET_ARCH.getName());
            }
        } else {
            GoSdkData sdkData = (GoSdkData) ownerSdk.getSdkAdditionalData();
            if ( sdkData != null && sdkData.TARGET_OS != null && sdkData.TARGET_ARCH != null ) {
                activeTarget = String.format("%s_%s", sdkData.TARGET_OS.getName(), sdkData.TARGET_ARCH.getName());
            }
        }

        if ( goRoot == null ) {
            return result;
        }

        // find libraries
        final VirtualFile packageRoot = goRoot.findFileByRelativePath("pkg");
        if (packageRoot == null) {
            return result;
        }

        CommonProcessors.CollectUniquesProcessor<String> libraryNames = new CommonProcessors.CollectUniquesProcessor<String>();

        final VirtualFile librariesRoot = packageRoot.findFileByRelativePath(activeTarget);

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
        final VirtualFile sourcesRoot = home.findFileByRelativePath(ownerSdk.getSdkType() == GoSdkType.getInstance() ? "src/pkg" : "goroot/src/pkg");
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
            String targetName = GoUtil.getTargetFromMakefile(makefile);

            if ( targetName != null && librariesSet.contains(targetName) ) {
                String relativePath = VfsUtil.getRelativePath(makefile.getParent(), sourcesRoot, '/');
                if ( relativePath != null ) {
                    result.put(relativePath, targetName);
                }
            }
        }

        return result;
    }
}
