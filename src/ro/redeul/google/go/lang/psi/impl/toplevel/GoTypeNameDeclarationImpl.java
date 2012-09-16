package ro.redeul.google.go.lang.psi.impl.toplevel;

import javax.swing.*;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.impl.GoStubPsiElementBase;
import ro.redeul.google.go.lang.psi.resolve.references.TypeNameDeclarationReference;
import ro.redeul.google.go.lang.psi.stubs.GoTypeNameDeclarationStub;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.util.LookupElementUtil;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 2, 2010
 * Time: 1:43:40 PM
 */
public class GoTypeNameDeclarationImpl
        extends GoStubPsiElementBase<GoTypeNameDeclarationStub>
        implements GoTypeNameDeclaration, StubBasedPsiElement<GoTypeNameDeclarationStub>
{

    public GoTypeNameDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    public GoTypeNameDeclarationImpl(GoTypeNameDeclarationStub stub, IStubElementType nodeType) {
        super(stub, nodeType);
    }

    @Override
    @NotNull
    public String getName() {
        return getText();
    }

    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitTypeNameDeclaration(this);
    }

    @Override
    public LookupElementBuilder getCompletionPresentation() {
        return LookupElementUtil.createLookupElement(this);
    }

    @Override
    public LookupElementBuilder getCompletionPresentation(GoPsiElement child) {
        return LookupElementUtil.createLookupElement(this, child);
    }

    @Override
    public String toString() {
        return "TypeNameDeclaration(" + getName() + ")";
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
            return String.format(" %s (%s)", ((GoFile)getContainingFile()).getPackage().getPackageName(), getContainingFile().getVirtualFile().getPath());
          }

          public Icon getIcon(boolean open) {
            return GoIcons.GO_ICON_16x16;
          }
        };
    }

    @Override
    public String getPackageName() {
        return ((GoFile)getContainingFile()).getPackage().getPackageName();
    }

    @Override
    public GoTypeSpec getTypeSpec() {
        return (GoTypeSpec) getParent();
    }

    @Override
    public String getPresentationText() {
        return "";
    }

    @Override
    public String getPresentationTailText() {
        return "";
    }

    @Override
    public String getPresentationTypeText() {
        return "";
    }

    @NotNull
    @Override
    public PsiReference[] getReferences() {
        return new PsiReference[]{(new TypeNameDeclarationReference(this))};
    }
}
