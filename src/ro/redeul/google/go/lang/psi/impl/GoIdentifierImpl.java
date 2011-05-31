package ro.redeul.google.go.lang.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.scope.util.PsiScopesUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.impl.expressions.GoExpressionBase;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.processors.IdentifierVariantsCollector;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import javax.swing.*;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 10:43:49 PM
 */
public class GoIdentifierImpl extends GoExpressionBase implements GoIdentifier {

    public GoIdentifierImpl(@NotNull ASTNode node) {
        super(node);
    }

    public void accept(GoElementVisitor visitor) {
        visitor.visitIdentifier(this);
    }

    @Override
    protected GoType resolveType() {
        return null;
    }

    @Override
    public PsiElement getElement() {
        return this;
    }

    @Override
    public TextRange getRangeInElement() {
        return new TextRange(0, getTextLength());
    }

    @Override
    public PsiElement resolve() {
        return this;
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return getText();
    }

    @Override
    public String getName() {
        return getText();
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        return null;
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        if (isReferenceTo(element))
            return this;

        throw new IncorrectOperationException("Cannot bind to:" + element + " of class " + element.getClass());
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return true;
    }

    @Override
    public PsiReference getReference() {
        return this;
    }

    @Override
    public boolean isSoft() {
        return true;
    }

    @NotNull
    @Override
    public Object[] getVariants() {

        IdentifierVariantsCollector identifierVariantsCollector = new IdentifierVariantsCollector();

        PsiScopesUtil.treeWalkUp(identifierVariantsCollector, this, this.getContainingFile(), GoResolveStates.initial());

        return identifierVariantsCollector.references();
    }

    @Override
    public boolean isBlank() {
        return getText().equals("_");
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
