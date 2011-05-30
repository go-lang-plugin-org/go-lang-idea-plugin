package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.GoTypeStruct;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/27/11
 * Time: 12:17 AM
 */
public class GoTypeStructImpl extends GoPsiElementBase implements GoTypeStruct {

    public GoTypeStructImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoTypeStructField[] getFields() {
        return findChildrenByClass(GoTypeStructField.class);
    }

    @Override
    public GoTypeStructAnonymousField[] getAnonymousFields() {
        return findChildrenByClass(GoTypeStructAnonymousField.class);
    }

    @Override
    public GoPsiElement[] getMembers() {
        List<GoPsiElement> members = new ArrayList<GoPsiElement>();

        GoTypeStructField fields[] = getFields();
        for (GoTypeStructField field : fields) {
            Collections.addAll(members, field.getIdentifiers());
        }

        GoTypeStructAnonymousField fieldAnonymous[] = getAnonymousFields();
        for (GoTypeStructAnonymousField field : fieldAnonymous) {
            GoType type = field.getType();
            Collections.addAll(members, type.getMembers());
        }

        return members.toArray(new GoPsiElement[members.size()]);
    }

    @Override
    public GoType getMemberType(String name) {

        GoTypeStructField fields[] = getFields();
        for (GoTypeStructField field : fields) {
            for (GoIdentifier identifier : field.getIdentifiers()) {
                String identifierName = identifier.getName();

                if ( identifierName != null && identifierName.equals(name) ) {
                    return field.getType();
                }
            }
        }

        GoTypeStructAnonymousField fieldAnonymous[] = getAnonymousFields();
        for (GoTypeStructAnonymousField field : fieldAnonymous) {
            GoType type = field.getType();
            GoType memberType = type.getMemberType(name);
            if ( memberType != null ) {
                return memberType;
            }
        }

        return null;
    }
}
