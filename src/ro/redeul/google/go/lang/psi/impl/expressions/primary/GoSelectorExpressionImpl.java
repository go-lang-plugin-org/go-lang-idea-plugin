package ro.redeul.google.go.lang.psi.impl.expressions.primary;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.impl.expressions.GoExpressionBase;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeName;
import ro.redeul.google.go.lang.psi.typing.GoTypePointer;
import ro.redeul.google.go.lang.psi.typing.GoTypeStruct;
import ro.redeul.google.go.lang.psi.utils.GoIdentifierUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.resolveSafely;

public class GoSelectorExpressionImpl extends GoExpressionBase implements GoSelectorExpression {

    public GoSelectorExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    public void accept(GoElementVisitor visitor) {
        visitor.visitSelectorExpression(this);
    }

    @NotNull
    @Override
    protected GoType[] resolveTypes() {
        PsiElement target = resolveSafely(getIdentifier(), PsiElement.class);

        if (target instanceof GoTypeSpec)
            return new GoType[]{
                    types().fromPsiType(((GoTypeSpec) target).getTypeNameDeclaration())};

        if (target instanceof GoFunctionDeclaration) {
            GoFunctionDeclaration functionDeclaration = (GoFunctionDeclaration) target;
            return new GoType[]{
                    types().fromPsiType(functionDeclaration)
            };
        }

        if (target != null && target.getParent() instanceof GoTypeStructField) {

            GoTypeStructField structField = (GoTypeStructField) target.getParent();

            return new GoType[]{types().fromPsiType(structField.getType())};
        }

        if (target instanceof GoTypeStructAnonymousField) {
            GoTypeStructAnonymousField structField =
                    (GoTypeStructAnonymousField) target;

            return new GoType[]{
                    types().fromPsiType(structField.getType())
            };
        }

        if (target instanceof GoLiteralIdentifier) {
            GoLiteralIdentifier targetIdentifier = (GoLiteralIdentifier) target;

            GoFunctionDeclaration functionDeclaration = GoIdentifierUtils.getFunctionDeclaration(target);
            if (functionDeclaration != null) {
                return new GoType[]{types().fromPsiType(functionDeclaration)};
            }
            if (target.getParent() instanceof GoVarDeclaration) {
                GoVarDeclaration declaration = (GoVarDeclaration) target.getParent();

                GoType output = declaration.getIdentifierType(targetIdentifier);
                return output == null ? GoType.EMPTY_ARRAY : new GoType[]{output};
            }

            if (target.getParent() instanceof GoConstDeclaration) {
                GoConstDeclaration constDeclaration = (GoConstDeclaration) target.getParent();
                GoExpr expression = constDeclaration.getExpression(targetIdentifier);
                return expression != null ? expression.getType() : GoType.EMPTY_ARRAY;
            }
        }

        return GoType.EMPTY_ARRAY;
    }

    @Override
    public GoPrimaryExpression getBaseExpression() {
        return findChildByClass(GoPrimaryExpression.class);
    }

    @Override
    @Nullable
    public GoLiteralIdentifier getIdentifier() {
        return findChildByClass(GoLiteralIdentifier.class);
    }

    private Object[] convertToPresentation(GoPsiType type, GoPsiElement[] members) {

        Object[] presentations = new Object[members.length];

        for (int i = 0, numMembers = members.length; i < numMembers; i++) {
            GoPsiElement member = members[i];

            if (member instanceof GoLiteralIdentifier) {
                LookupElementBuilder presentation =
                        getFieldPresentation(type, (GoLiteralIdentifier) member);

                if (presentation != null)
                    presentations[i] = presentation;

            } else {
                presentations[i] = member;
            }
        }

        return presentations;
    }

    @Nullable
    private LookupElementBuilder getFieldPresentation(GoPsiType type, GoLiteralIdentifier id) {

        String name = id.getName();
        LookupElementBuilder builder = LookupElementBuilder.create(id, name);

        GoPsiType ownerType = null;
        if (id.getParent() != null && id.getParent() instanceof GoTypeStructField) {
            GoTypeStructField structField = (GoTypeStructField) id.getParent();
            ownerType = (GoPsiType) structField.getParent();
        }

        if (ownerType == null)
            return builder;

        return builder
                .bold()
                .withTailText(String.format(" (defined by: %s)", ownerType.getName()))
                .withTypeText("<field>", ownerType != type);
    }

    @NotNull
    @Override
    public PsiReference[] defineReferences() {
        return super.defineReferences();
    }
}

