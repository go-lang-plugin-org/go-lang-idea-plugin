package ro.redeul.google.go.lang.psi.impl.expressions.literals;

import javax.swing.*;

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
import ro.redeul.google.go.lang.psi.expressions.literals.GoIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.processors.IdentifierVariantsCollector;
import ro.redeul.google.go.lang.psi.processors.IdentifierVariantsResolver;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.utils.GoTokenSets;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 10:43:49 PM
 */
public class GoIdentifierImpl extends GoPsiElementBase implements GoIdentifier {

    public GoIdentifierImpl(@NotNull ASTNode node) {
        super(node);
    }

    public void accept(GoElementVisitor visitor) {
        visitor.visitIdentifier(this);
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

        IdentifierVariantsResolver identifierVariantsResolver = new IdentifierVariantsResolver(this);

        PsiScopesUtil.treeWalkUp(identifierVariantsResolver, this, this.getContainingFile(), GoResolveStates.initial());

        return identifierVariantsResolver.reference();

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

        if (GoPsiUtils.isNodeOfType(getParent(), GoTokenSets.NO_IDENTIFIER_COMPLETION_PARENTS)) {
            return PsiReference.EMPTY_ARRAY;
        }

        IdentifierVariantsCollector identifierVariantsCollector = new IdentifierVariantsCollector();

        PsiScopesUtil.treeWalkUp(identifierVariantsCollector, this, this.getContainingFile(), GoResolveStates.initial());

        return identifierVariantsCollector.references();
    }

    @Override
    public boolean isBlank() {
        return getText().equals("_");
    }

    @Override
    public boolean isIota() {
        return getText().equals("iota");
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
