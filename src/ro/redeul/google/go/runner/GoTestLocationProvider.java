package ro.redeul.google.go.runner;

import com.intellij.execution.Location;
import com.intellij.execution.PsiLocation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.testIntegration.TestLocationProvider;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.utils.GoFileUtils;

import java.util.Collections;
import java.util.List;

public class GoTestLocationProvider implements TestLocationProvider {
    public static final String GO_TEST_CASE = "goTestCase";

    @NotNull
    @Override
    public List<Location> getLocation(@NotNull String protocolId, @NotNull String locationData, Project project) {
        if (!GO_TEST_CASE.equals(protocolId)) {
            return Collections.emptyList();
        }

        int pos = locationData.lastIndexOf(':');
        if (pos == -1) {
            return Collections.emptyList();
        }

        String funcName = locationData.substring(pos + 1);
        String packageDir = locationData.substring(0, pos);

        for (VirtualFile file : getPackageDirFiles(project, packageDir)) {
            if (file.getFileType() != GoFileType.INSTANCE ||
                    !file.getNameWithoutExtension().endsWith("_test")) {
                continue;
            }

            GoFile goFile = (GoFile) PsiManager.getInstance(project).findFile(file);
            for (GoFunctionDeclaration func : GoFileUtils.getFunctionDeclarations(goFile)) {
                if (funcName.equals(func.getFunctionName())) {
                    Location location = new PsiLocation<PsiElement>(project, func);
                    return Collections.singletonList(location);
                }
            }
        }

        return Collections.emptyList();
    }

    private VirtualFile[] getPackageDirFiles(Project project, String packageDir) {
        VirtualFile projectBaseDir = project.getBaseDir();
        if (projectBaseDir == null) {
            return VirtualFile.EMPTY_ARRAY;
        }

        VirtualFile dir = projectBaseDir.findFileByRelativePath(packageDir);
        if (dir == null || !dir.isDirectory()) {
            return VirtualFile.EMPTY_ARRAY;
        }

        VirtualFile[] children = dir.getChildren();
        if (children == null) {
            return VirtualFile.EMPTY_ARRAY;
        }
        return children;
    }
}
