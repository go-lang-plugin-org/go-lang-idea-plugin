package ro.redeul.google.go.lang.psi.impl.types.struct;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/29/11
 * Time: 12:28 PM
 */
public class GoTypeStructFieldImpl extends GoPsiElementBase implements GoTypeStructField {

    public GoTypeStructFieldImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean isBlank() {
        GoLiteralIdentifier identifiers[] = getIdentifiers();

        return identifiers.length == 1 && identifiers[0].isBlank();
    }

    @Override
    public GoLiteralIdentifier[] getIdentifiers() {
        return findChildrenByClass(GoLiteralIdentifier.class);
    }

    @Override
    public GoPsiType getType() {
        return findChildByClass(GoPsiType.class);
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            public String getPresentableText() {
                return getName();
            }

            public TextAttributesKey getTextAttributesKey() {
                return null;
            }

            public String getLocationString() {
                return String.format(" %s (%s)", ((GoFile) getContainingFile()).getPackage().getPackageName(), getContainingFile().getVirtualFile().getPath());
            }

            public Icon getIcon(boolean open) {
                return GoIcons.GO_ICON_16x16;
            }
        };
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitTypeStructField(this);
    }

    @Override
    public String getPresentationTypeText() {
        return getType().getText();
    }
}
