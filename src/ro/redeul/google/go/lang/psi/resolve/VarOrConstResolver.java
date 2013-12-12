package ro.redeul.google.go.lang.psi.resolve;

import com.intellij.psi.PsiElement;
import ro.redeul.google.go.lang.completion.GoCompletionContributor;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.processors.GoNamesUtil;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.resolve.references.VarOrConstReference;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchTypeGuard;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodReceiver;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/22/11
 * Time: 8:35 PM
 */
public class VarOrConstResolver extends
                                GoPsiReferenceResolver<VarOrConstReference> {

    public VarOrConstResolver(VarOrConstReference reference) {
        super(reference);
    }

    @Override
    public void visitMethodReceiver(GoMethodReceiver receiver) {
        checkIdentifiers(receiver.getIdentifier());
    }

    @Override
    public void visitFunctionParameter(GoFunctionParameter parameter) {
        checkIdentifiers(parameter.getIdentifiers());
    }

    @Override
    public void visitVarDeclaration(GoVarDeclaration declaration) {
        checkIdentifiers(declaration.getIdentifiers());
    }

    @Override
    public void visitShortVarDeclaration(GoShortVarDeclaration declaration) {
        checkIdentifiers(declaration.getDeclarations());
    }

    @Override
    public void visitLiteralIdentifier(GoLiteralIdentifier identifier) {
        checkIdentifiers(identifier);
    }

    @Override
    public void visitConstDeclaration(GoConstDeclaration declaration) {
        checkIdentifiers(declaration.getIdentifiers());
    }

    @Override
    public void visitFunctionDeclaration(GoFunctionDeclaration declaration) {
        checkIdentifiers(declaration.getNameIdentifier());
    }

    @Override
    public void visitSwitchTypeGuard(GoSwitchTypeGuard typeGuard) {
        checkIdentifiers(typeGuard.getIdentifier());
    }

    void checkIdentifiers(PsiElement... identifiers) {
        String refName = getReference().getElement().getText();
        String currentPackageName = getState().get(GoResolveStates.VisiblePackageName);
        if (currentPackageName == null) {
            currentPackageName = "";
        }
        boolean isOriginalPackage = getState().get(GoResolveStates.IsOriginalPackage);
        boolean incomplete = refName.contains(GoCompletionContributor.DUMMY_IDENTIFIER);
        if (incomplete) {
            int completionPosition = refName.indexOf(GoCompletionContributor.DUMMY_IDENTIFIER);
            refName = refName.substring(0, completionPosition);
        }
        for (PsiElement id : identifiers) {
            if (id == null) {
                continue;
            }
            String name = id.getText();
            if (isOriginalPackage || GoNamesUtil.isExportedName(name)) {
                if (refName.contains(".")) {
                    name = currentPackageName + "." + name;
                }
                if (incomplete && name.startsWith(refName)) {
                    addDeclaration(id);
                    return;
                }else if (refName.equals(name)) {
                    addDeclaration(id);
                    return;
                }
            }
        }
    }
}