package ro.redeul.google.go.lang.psi.resolve;

import com.intellij.patterns.ElementPattern;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.resolve.references.GoReturnVariableReference;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameterList;
import static com.intellij.patterns.PlatformPatterns.psiElement;

public class GoReturnVariableResolver
    extends GoPsiReferenceResolver<GoReturnVariableReference> {

    public static final ElementPattern<GoFunctionParameter> FUNCTION_RESULT =
        psiElement(GoFunctionParameter.class)
            .withParent(
                psiElement(GoFunctionParameterList.class)
                    .withParent(psiElement(GoElementTypes.FUNCTION_RESULT)));

    public GoReturnVariableResolver(GoReturnVariableReference reference) {
        super(reference);
    }

    @Override
    public void visitFunctionParameter(GoFunctionParameter parameter) {
        if (!FUNCTION_RESULT.accepts(parameter))
            return;

        for (GoLiteralIdentifier identifier : parameter.getIdentifiers()) {
            if (getReference().isReferenceTo(identifier))
                addDeclaration(identifier);
        }
    }
}
