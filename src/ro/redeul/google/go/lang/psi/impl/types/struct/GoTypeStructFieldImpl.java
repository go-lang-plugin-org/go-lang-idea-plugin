package ro.redeul.google.go.lang.psi.impl.types.struct;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;

import javax.swing.*;

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
    public String getName() {
//        return getIdentifiers().getText();
        return "x";
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        return null;
    }

    @Override
    public boolean isBlank() {
        GoIdentifier identifiers[] = getIdentifiers();

        return identifiers.length == 1 && identifiers[0].isBlank();
    }

    @Override
    public GoIdentifier[] getIdentifiers() {
        return findChildrenByClass(GoIdentifier.class);
    }

    @Override
    public GoType getType() {
        return findChildByClass(GoType.class);
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
}
