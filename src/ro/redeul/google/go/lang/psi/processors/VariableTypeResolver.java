package ro.redeul.google.go.lang.psi.processors;

import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.BaseScopeProcessor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/26/11
 * Time: 7:38 PM
 */
public class VariableTypeResolver extends BaseScopeProcessor {
    @Override
    public boolean execute(PsiElement element, ResolveState state) {
        System.out.println("node: " + element.getText());
        return true;
    }
}
