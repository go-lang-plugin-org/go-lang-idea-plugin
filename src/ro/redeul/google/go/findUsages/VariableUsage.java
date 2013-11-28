package ro.redeul.google.go.findUsages;

import com.intellij.psi.PsiElement;

import java.util.ArrayList;
import java.util.List;

class VariableUsage {
    public final PsiElement element;
    private final List<PsiElement> usages = new ArrayList<PsiElement>();
    public final boolean ignoreAnyProblem;

    VariableUsage(PsiElement element) {
        this(element, false);
    }

    VariableUsage(PsiElement element, boolean ignoreAnyProblem) {
        this.element = element;
        this.ignoreAnyProblem = ignoreAnyProblem;
    }

    public void addUsage(PsiElement use) {
        usages.add(use);
    }

    public boolean isUsed() {
        return usages.size() > 0;
    }

    public boolean isBlank() {
        return "_".equals(element.getText());
    }

    @Override
    public String toString() {
        return "VariableUsage{" + element +
               ", usages=" + usages.size() +
               '}';
    }
}
