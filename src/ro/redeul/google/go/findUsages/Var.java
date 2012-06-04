package ro.redeul.google.go.findUsages;

import com.intellij.psi.PsiElement;

import java.util.ArrayList;
import java.util.List;

class Var {
    public final PsiElement element;
    public final List<PsiElement> usages = new ArrayList<PsiElement>();

    Var(PsiElement element) {
        this.element = element;
    }

    public void addUsage(PsiElement use) {
        usages.add(use);
    }

    public boolean isUsed() {
        return usages.size() > 1;
    }

    public boolean isBlank() {
        return "_".equals(element.getText());
    }

    @Override
    public String toString() {
        return "Var{" + element +
               ", usages=" + usages.size() +
               '}';
    }
}
