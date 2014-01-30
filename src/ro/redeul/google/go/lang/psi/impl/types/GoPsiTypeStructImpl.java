package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import com.intellij.psi.impl.PsiElementBase;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiPackagedElementBase;
import ro.redeul.google.go.lang.psi.impl.types.struct.PromotedFieldsDiscover;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeStruct;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructPromotedFields;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypes;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import static ro.redeul.google.go.lang.psi.utils.GoTypeUtils.resolveToFinalType;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/27/11
 * Time: 12:17 AM
 */
public class GoPsiTypeStructImpl extends GoPsiPackagedElementBase implements
                                                                  GoPsiTypeStruct {

    public GoPsiTypeStructImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        return super.getParent().getFirstChild().getText();
    }

    @Override
    public GoTypeStructField[] getFields() {
        return findChildrenByClass(GoTypeStructField.class);
    }

    @NotNull
    @Override
    public GoTypeStructPromotedFields getPromotedFields() {
        return new PromotedFieldsDiscover(this).getPromotedFields();
    }


    @Override
    public GoTypeStructAnonymousField[] getAnonymousFields() {
        return findChildrenByClass(GoTypeStructAnonymousField.class);
    }

//    @Override
//    public GoPsiElement[] getMembers() {
//        List<GoPsiElement> members = new ArrayList<GoPsiElement>();
//
//        GoTypeStructField fields[] = getFields();
//        for (GoTypeStructField field : fields) {
//            Collections.addAll(members, field.getIdentifiers());
//        }
//
//        GoTypeStructAnonymousField fieldAnonymous[] = getAnonymousFields();
//        for (GoTypeStructAnonymousField field : fieldAnonymous) {
//            GoType type = field.getType();
//            Collections.addAll(members, type.getMembers());
//        }
//
//        return members.toArray(new GoPsiElement[members.size()]);
//    }

//    @Override
//    public GoType getMemberType(String name) {
//
//        GoTypeStructField fields[] = getFields();
//        for (GoTypeStructField field : fields) {
//            for (GoLiteralIdentifier identifier : field.getIdentifiers()) {
//                String identifierName = identifier.getName();
//
//                if ( identifierName != null && identifierName.equals(name) ) {
//                    return field.getType();
//                }
//            }
//        }
//
//        GoTypeStructAnonymousField fieldAnonymous[] = getAnonymousFields();
//        for (GoTypeStructAnonymousField field : fieldAnonymous) {
//            GoType type = field.getType();
//            GoType memberType = type.getMemberType(name);
//            if ( memberType != null ) {
//                return memberType;
//            }
//        }
//
//        return null;
//    }
//
    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitStructType(this);
    }

    @Override
    public GoUnderlyingType getUnderlyingType() {
        return GoUnderlyingTypes.getStruct();
    }

    @Override
    public boolean isIdentical(GoPsiType goType) {
        if (goType instanceof GoPsiTypeName) {
            goType = resolveToFinalType(goType);
        }
        if (!(goType instanceof GoPsiTypeStruct))
            return false;
        GoPsiTypeStruct otherStruct = (GoPsiTypeStruct) goType;
        GoTypeStructField[] fields = getFields();
        GoTypeStructField[] otherFields = otherStruct.getFields();
        GoTypeStructAnonymousField[] anonymousFields = getAnonymousFields();
        GoTypeStructAnonymousField[] otherAnonymousFields = otherStruct.getAnonymousFields();

        if (anonymousFields.length != otherAnonymousFields.length)
            return false;
        if (fields.length != otherFields.length)
            return false;

        for (int i = 0; i < fields.length; i++) {
            GoTypeStructField field = fields[i];
            GoTypeStructField otherField = otherFields[i];
            PsiElementBase tag = field.getTag();
            PsiElementBase otherTag = otherField.getTag();
            if ((tag == null && otherTag != null) || (otherTag == null && tag != null))
                return false;
            if (tag != null && !tag.getText().equals(otherTag.getText()))
                return false;

            GoLiteralIdentifier[] identifiers = field.getIdentifiers();
            GoLiteralIdentifier[] otherIdentifiers = otherField.getIdentifiers();
            if (identifiers.length != otherIdentifiers.length)
                return false;
            for (int j = 0; j < identifiers.length; j++) {
                GoLiteralIdentifier identifier = identifiers[j];
                GoLiteralIdentifier otherIdentifier = otherIdentifiers[j];
                if (!identifier.getUnqualifiedName().equals(otherIdentifier.getUnqualifiedName()))
                    return false;
                if (!field.getType().isIdentical(otherField.getType()))
                    return false;
            }
        }

        for (int i = 0; i < anonymousFields.length; i++) {
            GoTypeStructAnonymousField field = anonymousFields[i];
            GoTypeStructAnonymousField otherField = otherAnonymousFields[i];
            if (!field.getFieldName().equals(otherField.getFieldName()))
                return false;
            if (!field.getType().isIdentical(otherField.getType()))
                return false;
            PsiElementBase tag = field.getTag();
            PsiElementBase otherTag = otherField.getTag();
            if ((tag == null && otherTag != null) || (otherTag == null && tag != null))
                return false;
            if (tag != null && !tag.getText().equals(otherTag.getText()))
                return false;
        }

        return true;

    }


}
