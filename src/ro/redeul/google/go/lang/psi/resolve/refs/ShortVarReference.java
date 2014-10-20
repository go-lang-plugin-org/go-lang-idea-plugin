package ro.redeul.google.go.lang.psi.resolve.refs;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.processors.ResolveStates;
import ro.redeul.google.go.lang.psi.resolve.ReferenceWithSolver;
import ro.redeul.google.go.lang.psi.resolve.ResolvingCache;
import ro.redeul.google.go.lang.psi.resolve.references.Reference;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.getAs;

/**
 * ShortVarDeclarationReference is a reference of identifier defined in short variable declaration.
 *
 * According to spec, short variable declaration may redeclare variables.
 * If the variable is redeclared in current short variable declaration, method {@link #resolve}
 * returns the identifier where it's declared. Otherwise, null is returned.
 */
public class ShortVarReference
        extends ReferenceWithSolver<GoLiteralIdentifier, ShortVarSolver, ShortVarReference> {

    public static final ElementPattern<GoLiteralIdentifier> MATCHER =
            psiElement(GoLiteralIdentifier.class)
                    .withParent(
                            psiElement(GoShortVarDeclaration.class)
                    );

//    private static final ResolveCache.AbstractResolver<ShortVarReference, ResolvingCache.Result> RESOLVER =
//            new ResolveCache.AbstractResolver<ShortVarReference, ResolvingCache.Result>() {
//                @Override
//                public ResolvingCache.Result resolve(@NotNull ShortVarReference reference, boolean incompleteCode) {
//                    GoLiteralIdentifier element = reference.getElement();
//                    PsiElement parent = element.getParent();
//                    if (!(parent instanceof GoShortVarDeclaration)) {
//                        return ResolvingCache.Result.NULL;
//                    }
//
//                    GoLiteralIdentifier identifier = reference.getElement();
//                    PsiElement resolve = ShortVarSolver.resolve(identifier);
//                    if (resolve == null) {
//                        return ResolvingCache.Result.NULL;
//                    }
//                    return ResolvingCache.Result.fromElement(resolve);
//                }
//            };
//

    public ShortVarReference(@NotNull GoLiteralIdentifier element) {
        super(element);
    }

    @Override
    protected ShortVarReference self() { return this; }

    @Override
    protected ShortVarSolver newSolver() { return new ShortVarSolver(this); }

    @Override
    protected void walkSolver(ShortVarSolver solver) {
        GoShortVarDeclaration varDeclaration = getAs(GoShortVarDeclaration.class, getElement().getParent());

        GoBlockStatement blockStatement = getAs(GoBlockStatement.class, varDeclaration.getParent());

        if ( blockStatement != null ){
            blockStatement.processDeclarations(solver, ResolveStates.initial(), blockStatement, this.getElement());
        }
        int a = 10;
    }
}
