package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.types.GoTypeStruct;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;

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
    public GoPsiElement[] getMembers() {
        List<GoPsiElement> members = new ArrayList<GoPsiElement>();

        GoTypeStructField fields[] = getFields();
        for (GoTypeStructField field : fields) {
            Collections.addAll(members, field.getIdentifiers());
        }

        return members.toArray(new GoPsiElement[members.size()]);
    }
}
