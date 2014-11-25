package ro.redeul.google.go.lang.psi.resolve.refs;

import com.intellij.patterns.ElementPattern;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.resolve.ReferenceSolvingVisitor;
import ro.redeul.google.go.lang.psi.resolve.VisitingReferenceSolver;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class ShortVarSolver extends VisitingReferenceSolver<ShortVarReference, ShortVarSolver>{

    private static final ElementPattern<GoLiteralIdentifier> SHORT_VAR_IN_FUNCTION =
            psiElement(GoLiteralIdentifier.class).withParent(
                    psiElement(GoShortVarDeclaration.class).withParent(
                            psiElement(GoBlockStatement.class).withParent(GoFunctionDeclaration.class)
                    ));

    private static final ElementPattern<GoLiteralIdentifier> SHORT_VAR =
            psiElement(GoLiteralIdentifier.class).withParent(
                    psiElement(GoShortVarDeclaration.class));

    public ShortVarSolver(final ShortVarReference reference) {
        solveWithVisitor(new ReferenceSolvingVisitor(this, reference) {
            @Override
            public void visitShortVarDeclaration(GoShortVarDeclaration declaration) {
                checkIdentifiers(reference.name(), declaration.getDeclarations());
            }

            @Override
            public void visitVarDeclaration(GoVarDeclaration declaration) {
                checkIdentifiers(reference.name(), declaration.getIdentifiers());
            }

            @Override
            public void visitFunctionParameter(GoFunctionParameter parameter) {
                checkIdentifiers(reference.name(), parameter.getIdentifiers());
            }
        });
    }

    @Override
    public ShortVarSolver self() { return this; }
}
