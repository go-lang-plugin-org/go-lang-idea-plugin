package ro.redeul.google.go.testIntegration;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;

class TestUtil {
    public static boolean isTestFile(VirtualFile file) {
        return file != null && file.getName().endsWith("_test.go");
    }

    public static PsiFile getPsiFile(Project project, VirtualFile file) {
        return PsiManager.getInstance(project).findFile(file);
    }

    public static String getTestFileName(String sourceFileName) {
        return sourceFileName.substring(0, sourceFileName.length() - 3) + "_test.go";
    }
}
