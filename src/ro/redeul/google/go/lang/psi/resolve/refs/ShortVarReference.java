package ro.redeul.google.go.lang.psi.resolve.refs;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.ResolveState;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.processors.ResolveStates;
import ro.redeul.google.go.lang.psi.resolve.ReferenceWithSolver;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.getAs;

/**
 * ShortVarDeclarationReference is a reference of identifier defined in short variable declaration.
 *
 * According to spec, short variable declaration may redeclare variables.
 * If the variable is redeclared in current short variable declaration, method {@link #resolve}
 * returns the identifier where it's declared. Otherwise, null is returned.
 */
public class ShortVarReference extends ReferenceWithSolver<GoLiteralIdentifier, ShortVarSolver, ShortVarReference> {

    public static final ElementPattern<GoLiteralIdentifier> MATCHER =
            psiElement(GoLiteralIdentifier.class)
                    .withParent(
                            psiElement(GoShortVarDeclaration.class)
                    );

    public ShortVarReference(@NotNull GoLiteralIdentifier element) {
        super(element);
    }

    @Override
    protected ShortVarReference self() { return this; }

    @Override
    protected ShortVarSolver newSolver() { return new ShortVarSolver(this); }

    @Override
    protected void walkSolver(ShortVarSolver solver) {
        GoShortVarDeclaration declaration = getAs(GoShortVarDeclaration.class, getElement().getParent());

        GoBlockStatement blockStatement = getAs(GoBlockStatement.class, declaration.getParent());

        if ( blockStatement != null ) {
            if ( !blockStatement.processDeclarations(solver, ResolveStates.initial(), declaration, this.getElement()))
                return;

            if ( blockStatement.getParent() instanceof GoFunctionDeclaration ) {
                GoFunctionDeclaration functionDeclaration = (GoFunctionDeclaration) blockStatement.getParent();

                functionDeclaration.processDeclarations(solver, ResolveState.initial(), blockStatement, this.getElement());
            }
        }
    }

    @Override
    public boolean isSoft() {
        return true;
    }
}
