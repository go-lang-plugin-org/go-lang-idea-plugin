package ro.redeul.google.go.lang.psi.resolve;

import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.Nullable;

public class GoResolveResult implements ResolveResult {

    public static final GoResolveResult NULL = new GoResolveResult(null);

    private final PsiElement element;

    public static GoResolveResult fromElement(@Nullable PsiElement element) {
        if ( element != null && element.isValid())
            return new GoResolveResult(element);
        else
            return NULL;
    }

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

    @Override
    public String toString() {
        StringBuilder elementTree = new StringBuilder();

        elementTree.append(element == null ? element : "" + element.getText().replaceAll("[\n]", "\\n") + " : " + element.isValid() + " ");

        PsiElement elem = element;
        while (elem != null && !elem.isValid() ) {
            elementTree.append("[ ").append(elem.getText().replaceAll("[\n]", "\\n")).append(" ]");
            elementTree.append(" -> ");
            elem = elem.getParent();
        }

        if (elem == null)
            elementTree.append("[ ]");

        return "GoResolveResult{element=" + elementTree +'}';
    }
}
