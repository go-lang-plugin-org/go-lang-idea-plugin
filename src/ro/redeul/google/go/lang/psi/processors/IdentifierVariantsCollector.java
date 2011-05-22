package ro.redeul.google.go.lang.psi.processors;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.BaseScopeProcessor;
import org.eclipse.jdt.internal.compiler.ast.TrueLiteral;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/22/11
 * Time: 8:35 PM
 */
public class IdentifierVariantsCollector extends BaseScopeProcessor{

    static final String builtInFunctions[] = {
            "append", "cap", "close", "complex", "copy", "imag", "len", "make", "new", "panic", "print", "println", "real", "recover"
    };

    List<LookupElement> variants = new ArrayList<LookupElement>();

    @Override
    public boolean execute(PsiElement element, ResolveState state) {
        if ( element instanceof GoFunctionDeclaration && ! (element instanceof GoMethodDeclaration) ) {
            collectFunctionName((GoFunctionDeclaration) element, state);
        }

        return true;
    }

    private void collectFunctionName(GoFunctionDeclaration functionDeclaration, ResolveState state) {

        // include this if:
        //   - inside the same package
        //   - is a public name

        if ( functionDeclaration.isMain() ) {
            return;
        }

        String functionName = functionDeclaration.getFunctionName();

        String suggestionTypeText = state.get(GoResolveStates.PackageName);

        boolean isCandidate = false;
        if ( GoNamesUtil.isPublicType(functionName) ) {
            isCandidate = true;
        }

        if ( state.get(GoResolveStates.IsOriginalFile) || state.get(GoResolveStates.IsOriginalPackage) ) {
            isCandidate = true;
            suggestionTypeText = "<current>";
        }

        if ( isCandidate ) {

            String visiblePackageName = state.get(GoResolveStates.VisiblePackageName);

            if ( visiblePackageName != null && visiblePackageName.length() > 0 ) {
                functionName = String.format("%s.%s", visiblePackageName, functionName);
            }

            variants.add(LookupElementBuilder.create(functionDeclaration, functionName).setTypeText(suggestionTypeText) );
        }
    }

    public Object[] references() {
        for (String builtInType : builtInFunctions) {
            variants.add(LookupElementBuilder.create(builtInType).setTypeText("builtin", true).setBold());
        }

        return variants.toArray(new Object[variants.size()]);
    }
}
