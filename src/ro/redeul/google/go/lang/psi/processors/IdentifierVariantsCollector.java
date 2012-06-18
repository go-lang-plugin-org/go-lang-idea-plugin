package ro.redeul.google.go.lang.psi.processors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.BaseScopeProcessor;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;

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
    Set<String> names = new HashSet<String>();

    @Override
    public boolean execute(PsiElement element, ResolveState state) {

        if ( element instanceof GoFunctionDeclaration && ! (element instanceof GoMethodDeclaration) ) {
            collectFunctionName((GoFunctionDeclaration) element, state);
            return true;
        }

        if ( element instanceof GoVarDeclaration) {
            collectVariableDeclaration((GoVarDeclaration) element, state);
            return true;
        }

        if ( element instanceof GoConstDeclaration) {
            collectVariableDeclaration((GoConstDeclaration) element, state);
            return true;
        }

        return true;
    }

    private void collectVariableDeclaration(GoVarDeclaration declaration, ResolveState state) {

        GoLiteralIdentifier identifiers[] = declaration.getIdentifiers();

        boolean isImported = isImported(declaration, state);

        for (GoLiteralIdentifier identifier : identifiers) {
            if ( ! isImported || GoNamesUtil.isPublicType(identifier.getName()) ) {
                addVariant(identifier, identifier.getName(), state);
            }
        }
    }

    private void collectVariableDeclaration(GoConstDeclaration declaration, ResolveState state) {

        GoLiteralIdentifier identifiers[] = declaration.getIdentifiers();

        boolean isImported = isImported(declaration, state);

        for (GoLiteralIdentifier identifier : identifiers) {
            if ( ! isImported || GoNamesUtil.isPublicType(identifier.getName()) ) {
                addVariant(identifier, identifier.getName(), state);
            }
        }
    }

    private void collectFunctionName(GoFunctionDeclaration function, ResolveState state) {

        // include this if:
        //   - inside the same package
        //   - is a public name

        if ( function.isMain() ) {
            return;
        }

        if ( ! isImported(function, state) || GoNamesUtil.isPublicType(function.getFunctionName()) ) {
            addVariant(function, function.getName(), state);
        }
    }

    private boolean isImported(PsiElement target, ResolveState state) {
        return !(state.get(GoResolveStates.IsOriginalFile) || state.get(GoResolveStates.IsOriginalPackage));
    }

    private void addVariant(PsiElement target, String name, ResolveState state) {

        boolean isImported = isImported(target, state);

        String displayName = name;
        String visiblePackageName = state.get(GoResolveStates.VisiblePackageName);

        if ( isImported && visiblePackageName != null && visiblePackageName.length() > 0 ) {
            displayName = String.format("%s.%s", visiblePackageName, name);
        }

        if ( displayName != null && ! names.contains(displayName)) {
            variants.add(LookupElementBuilder.create(target, displayName).setTypeText(isImported ? state.get(GoResolveStates.PackageName) : "<current>"));
            names.add(displayName);
        }
    }

    public Object[] references() {

        for (String builtInType : builtInFunctions) {
            variants.add(LookupElementBuilder.create(builtInType).setTypeText("builtin", true).setBold());
        }

        return variants.toArray(new Object[variants.size()]);
    }
}
