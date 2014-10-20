package ro.redeul.google.go.lang.psi.impl.toplevel;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.impl.expressions.literals.GoLiteralIdentifierImpl;
import ro.redeul.google.go.lang.psi.impl.types.GoPsiTypeSliceImpl;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/26/11
 * Time: 11:07 PM
 */
public class GoFunctionParameterImpl extends GoPsiElementBase implements GoFunctionParameter {

    public GoFunctionParameterImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitFunctionParameter(this);
    }

    @Override
    public boolean isVariadic() {
        return getTokenType() == GoElementTypes.FUNCTION_PARAMETER_VARIADIC;
    }

    @Override
    public GoLiteralIdentifier[] getIdentifiers() {
        return findChildrenByClass(GoLiteralIdentifierImpl.class);
    }

    @Override
    public GoPsiType getType() { return findChildByClass(GoPsiType.class); }

    @Override
    public GoPsiType getTypeForBody() {
        if (isVariadic()) {
            return new GoPsiTypeSliceImpl(getNode());
        }else{
            return findChildByClass(GoPsiType.class);
        }
    }

    @Override
    public String toString() {
        return isVariadic() ? "FunctionParameterVariadicImpl" : "FunctionParameterImpl";
    }

    @NotNull
    @Override
    public String getPresentationTailText() {
        StringBuilder presentationText = new StringBuilder();

        GoLiteralIdentifier[] identifiers = getIdentifiers();
        for (int i = 0; i < identifiers.length; i++) {
            GoLiteralIdentifier identifier = identifiers[i];

            presentationText.append(identifier.getName());
            if (i < identifiers.length - 1) {
                presentationText.append(",");
            }
            presentationText.append(" ");
        }

        if (isVariadic()) {
            presentationText.append("...");
        }

        if (getType() != null) {
            presentationText.append(getType().getPresentationTailText());
        }

        return presentationText.toString();
    }
}
