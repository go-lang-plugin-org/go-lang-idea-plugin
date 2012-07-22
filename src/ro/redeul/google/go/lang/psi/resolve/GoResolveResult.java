package ro.redeul.google.go.lang.psi.resolve;

import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveResult;

public class GoResolveResult implements ResolveResult {

    public static final GoResolveResult NULL = new GoResolveResult(null);

    PsiElement element;

    public GoResolveResult(PsiElement element) {
        this.element = element;
    }

    @Override
    public PsiElement getElement() {
        return element;
    }

    @Override
    public boolean isValidResult() {
        return element != null;
    }
}
