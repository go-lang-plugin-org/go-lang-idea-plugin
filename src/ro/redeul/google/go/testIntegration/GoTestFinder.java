package ro.redeul.google.go.testIntegration;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.testIntegration.TestFinder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GoTestFinder implements TestFinder {
    @Override
    public PsiElement findSourceElement(@NotNull PsiElement from) {
        return from.getContainingFile();
    }

    /**
     * Look for corresponding test file. If the source code file name is XXX.go
     * the test file should be XXX_test.go, if it's found, return it.
     * Otherwise return *_test.go in the containing directory.
     * @param element source code element
     * @return corresponding test file elements.
     */
    @NotNull
    @Override
    public Collection<PsiElement> findTestsForClass(@NotNull PsiElement element) {
        PsiFile containingFile = element.getContainingFile();
        if (containingFile == null) {
            return Collections.emptyList();
        }

        VirtualFile file = containingFile.getVirtualFile();
        if (file == null) {
            return Collections.emptyList();
        }

        String path = file.getPath();
        if (path == null || !path.toLowerCase().endsWith(".go")) {
            return Collections.emptyList();
        }

        String testFileName = path.substring(0, path.length() - 3) + "_test.go";
        VirtualFile testFile = file.getFileSystem().findFileByPath(testFileName);
        if (testFile != null) {
            PsiElement psiFile = getPsiFile(containingFile.getProject(), testFile);
            if (psiFile != null) {
                return Collections.singletonList(psiFile);
            }
            return Collections.emptyList();
        }

        List<PsiElement> tests = new ArrayList<PsiElement>();
        for (VirtualFile virtualFile : file.getParent().getChildren()) {
            if (isTest(virtualFile)) {
                PsiFile psiFile = getPsiFile(containingFile.getProject(), virtualFile);
                if (psiFile != null) {
                    tests.add(psiFile);
                }
            }
        }
        return tests;
    }

    /**
     * The containing file name of test element should be XXX_test.go,
     * the corresponding source code file should be XXX.go
     * @param element test element
     * @return empty collection if no source code is found, or a single element of the source code element.
     */
    @NotNull
    @Override
    public Collection<PsiElement> findClassesForTest(@NotNull PsiElement element) {
        PsiFile containingFile = element.getContainingFile();
        if (containingFile == null) {
            return Collections.emptyList();
        }

        VirtualFile file = containingFile.getVirtualFile();
        if (file == null || !isTest(file)) {
            return Collections.emptyList();
        }

        String path = file.getPath();
        String sourceFileName = path.substring(0, path.length() - 8) + ".go";
        VirtualFile sourceFile = file.getFileSystem().findFileByPath(sourceFileName);
        if (sourceFile != null) {
            PsiElement psiFile = getPsiFile(containingFile.getProject(), sourceFile);
            if (psiFile != null) {
                return Collections.singletonList(psiFile);
            }
        }

        return Collections.emptyList();
    }

    @Override
    public boolean isTest(@NotNull PsiElement element) {
        PsiFile file = element.getContainingFile();
        return file != null && isTest(file.getVirtualFile());
    }

    private boolean isTest(VirtualFile file) {
        return file != null && file.getName().endsWith("_test.go");
    }

    private PsiFile getPsiFile(Project project, VirtualFile file) {
        return PsiManager.getInstance(project).findFile(file);
    }
}
