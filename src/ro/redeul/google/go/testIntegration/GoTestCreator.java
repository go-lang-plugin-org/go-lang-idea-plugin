package ro.redeul.google.go.testIntegration;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.testIntegration.TestCreator;
import ro.redeul.google.go.actions.GoTemplatesFactory;
import ro.redeul.google.go.lang.psi.GoFile;

import static ro.redeul.google.go.testIntegration.TestUtil.getTestFileName;
import static ro.redeul.google.go.testIntegration.TestUtil.isTestFile;

public class GoTestCreator implements TestCreator {
    // Create new test menu item is enabled when there is no corresponding test file for the specified file.
    @Override
    public boolean isAvailable(Project project, Editor editor, PsiFile file) {
        VirtualFile virtualFile = file.getVirtualFile();
        if (virtualFile == null ||
                !virtualFile.getName().toLowerCase().endsWith(".go") ||
                isTestFile(virtualFile)) {
            return false;
        }

        String testFileName = getTestFileName(virtualFile.getPath());
        return virtualFile.getFileSystem().findFileByPath(testFileName) == null;
    }

    @Override
    public void createTest(Project project, Editor editor, final PsiFile file) {
        VirtualFile virtualFile = file.getVirtualFile();
        if (virtualFile == null || !(file instanceof GoFile)) {
            return;
        }

        final String testFileName = getTestFileName(virtualFile.getName());
        VirtualFile dir = virtualFile.getParent();
        if (dir == null) {
            return;
        }

        final PsiDirectory psiDir = PsiManager.getInstance(file.getProject()).findDirectory(dir);
        if (psiDir == null) {
            return;
        }

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                GoTemplatesFactory.Template template = GoTemplatesFactory.Template.GoTestFile;
                String packageName = ((GoFile) file).getPackageName();
                PsiElement test = GoTemplatesFactory.createFromTemplate(psiDir, packageName, testFileName, template);
                if (test != null) {
                    if (test instanceof GoFile) ((GoFile) test).navigate(true);
                }
            }
        });
    }
}
