package ro.redeul.google.go.lang.psi.visitors;

import com.intellij.psi.PsiElement;

/**
 * currently, GoRecursiveElementVisitor doesn't work correctly, some elements are not visited, so I write this class.
 */
public class GoRecursiveElementVisitor2 {
    public void visitElement(PsiElement element) {
        beforeVisitElement(element);
        for (PsiElement child : element.getChildren()) {
            visitElement(child);
        }
        afterVisitElement(element);
    }

    protected void beforeVisitElement(PsiElement element) {
    }

    protected void afterVisitElement(PsiElement element) {
    }
}
