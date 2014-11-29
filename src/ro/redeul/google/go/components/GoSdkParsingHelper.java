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
import com.intellij.util.CommonProcessors;
import com.intellij.util.FilteringProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.config.sdk.GoAppEngineSdkType;
import ro.redeul.google.go.config.sdk.GoSdkType;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.sdk.GoSdkUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Map<String, String> findPackageMappings(Sdk ownerSdk) {
        Map<String, String> result = new HashMap<String, String>();

        if (ownerSdk.getSdkType() != GoSdkType.getInstance() && ownerSdk.getSdkType() != GoAppEngineSdkType.getInstance())
            return result;

        VirtualFile home = ownerSdk.getHomeDirectory();
        if (home == null) {
            return result;
        }

        // find makefiles
        CommonProcessors.CollectUniquesProcessor<VirtualFile> makefiles = new CommonProcessors.CollectUniquesProcessor<VirtualFile>();
        final VirtualFile sourcesRoot = GoSdkUtil.getSdkSourcesRoot(ownerSdk);
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

        return result;
    }
}
