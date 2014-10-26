package ro.redeul.google.go.lang.psi.resolve.refs;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.resolve.ReferenceSolvingVisitor;
import ro.redeul.google.go.lang.psi.resolve.VisitingReferenceSolver;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchTypeGuard;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodReceiver;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;

public class VarOrConstSolver extends VisitingReferenceSolver<VarOrConstReference, VarOrConstSolver> {

    @Override
    public VarOrConstSolver self() { return this; }

    public VarOrConstSolver(final VarOrConstReference reference) {
        solveWithVisitor(new ReferenceSolvingVisitor(this, reference) {
            @Override
            public void visitMethodReceiver(GoMethodReceiver receiver) {
                if ( receiver.getIdentifier() != null )
                    checkIdentifiers(reference.name(), receiver.getIdentifier());
            }

            @Override
            public void visitFunctionParameter(GoFunctionParameter parameter) {
                checkIdentifiers(reference.name(), parameter.getIdentifiers());
            }

            @Override
            public void visitVarDeclaration(GoVarDeclaration declaration) {
                checkIdentifiers(reference.name(), declaration.getIdentifiers());
            }

            @Override
            public void visitShortVarDeclaration(GoShortVarDeclaration declaration) {
                checkIdentifiers(reference.name(), declaration.getDeclarations());
            }

            @Override
            public void visitLiteralIdentifier(GoLiteralIdentifier identifier) {
                checkIdentifiers(reference.name(), identifier);
            }

            @Override
            public void visitConstDeclaration(GoConstDeclaration declaration) {
                checkIdentifiers(reference.name(), declaration.getIdentifiers());
            }

            @Override
            public void visitFunctionDeclaration(GoFunctionDeclaration declaration) {
                if (matchNames(reference.name(), declaration.getFunctionName()))
                    addTarget(declaration);
            }

            @Override
            public void visitMethodDeclaration(GoMethodDeclaration declaration) {
//                visitFunctionDeclaration(declaration);
            }

            @Override
            public void visitSwitchTypeGuard(GoSwitchTypeGuard typeGuard) {
                checkIdentifiers(reference.name(), typeGuard.getIdentifier());
            }
        });
    }

    @Override
    public PsiElement resolve(@NotNull VarOrConstReference reference, boolean incompleteCode) {
        PsiElement solution = super.resolve(reference, incompleteCode);

        if ( solution != null ) {

            System.out.println();
            System.out.println(reference.getElement().getParent().getParent().getText());
            System.out.println(solution.getParent().getParent().getText());
        }


        // PsiElement secondSolution = GoPsiUtils.resolveSafely(solution);
//
//        return secondSolution != null ? secondSolution : solution;
        return solution;
    }
}
