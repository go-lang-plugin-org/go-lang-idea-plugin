package ro.redeul.google.go.lang.psi.impl.expressions.primary;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.impl.expressions.GoExpressionBase;
import ro.redeul.google.go.lang.psi.resolve.references.InterfaceMethodReference;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypeInterface;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/19/11
 * Time: 10:58 PM
 */
public class GoSelectorExpressionImpl extends GoExpressionBase implements GoSelectorExpression {

    public GoSelectorExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    public String toString() {
        return "SelectorExpression";
    }

    public void accept(GoElementVisitor visitor) {
        // TODO: implement this properly
        visitor.visitElement(this);
    }

    @Override
    protected GoType[] resolveTypes() {

//        PsiElement psiElement = resolve();

//        if (psiElement != null && psiElement instanceof GoType) {
//            return new GoType[]{(GoType) psiElement};
//        }
//
        return GoType.EMPTY_ARRAY;
    }

    @Override
    @Nullable
    public GoPrimaryExpression getBaseExpression() {
        return findChildByClass(GoPrimaryExpression.class);
    }

    @Override
    public GoLiteralIdentifier getIdentifier() {
        return findChildByClass(GoLiteralIdentifier.class);
    }

    private Object[] convertToPresentation(GoType type, GoPsiElement[] members) {

        Object[] presentations = new Object[members.length];

        for (int i = 0, membersLength = members.length; i < membersLength; i++) {

            GoPsiElement member = members[i];

            if (member instanceof GoLiteralIdentifier) {
                presentations[i] = getFieldPresentation(type, (GoLiteralIdentifier) member);
            } else {
                presentations[i] = member;
            }
        }

        return presentations;
    }

    private LookupElementBuilder getFieldPresentation(GoType type, GoLiteralIdentifier id) {

        String name = id.getName();

        LookupElementBuilder builder = LookupElementBuilder.create(id, name);

        GoType ownerType = null;
        if (id.getParent() != null && id.getParent() instanceof GoTypeStructField) {
            GoTypeStructField structField = (GoTypeStructField) id.getParent();
            ownerType = (GoType) structField.getParent();
        }

        if (ownerType == null) {
            return builder;
        }

        return builder
                .setBold()
                .setTailText(String.format(" (defined by: %s)", ownerType.getQualifiedName()))
                .setTypeText("<field>", ownerType != type);
    }

    @Override
    public PsiReference getReference() {
        GoPrimaryExpression baseExpression = getBaseExpression();

        if (baseExpression == null) {
            return null;
        }

        GoType []baseTypes = baseExpression.getType();
        if (baseTypes.length == 0) {
            return null;
        }

        GoType type = baseTypes[0];

        GoUnderlyingType x = type.getUnderlyingType();

        if ( x instanceof GoUnderlyingTypeInterface )
            return new InterfaceMethodReference(this);


//        if ( type instanceof GoTypeStruct) {
//            return new StructFieldReference(this);

        return null;
    }
}
