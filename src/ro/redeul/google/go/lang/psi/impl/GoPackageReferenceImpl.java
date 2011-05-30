package ro.redeul.google.go.lang.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.scope.util.PsiScopesUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPackageReference;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.processors.ImportedPackagesCollectorProcessor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 4, 2010
 * Time: 10:41:21 PM
 */
public class GoPackageReferenceImpl extends GoPsiElementBase implements GoPackageReference {

    // WARN: Why is this a GoType implementation ?!

    public GoPackageReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    public boolean isBlank() {
        return getString().equals("_");
    }

    public boolean isLocal() {
        return getString().equals(".");
    }

    public String getString() {
        return getText();
    }

    public PsiElement getElement() {
        return this;
    }

    public TextRange getRangeInElement() {
        return getTextRange();
    }

    public PsiElement resolve() {
        return null;
    }

    public String getCanonicalText() {
        return getText();
    }

    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isReferenceTo(PsiElement element) {
        return this == element;
    }

    @NotNull
    public Object[] getVariants() {

        ImportedPackagesCollectorProcessor processor = new ImportedPackagesCollectorProcessor();

        PsiScopesUtil.treeWalkUp(processor, this, this.getContainingFile());

        return processor.getPackageImports();
    }

    public boolean isSoft() {
        return true;
    }

    @Override
    public String getName() {
        return getText();
    }

    public PsiElement setName(@NonNls String name) throws IncorrectOperationException {
        return null;
    }

//    @Override
//    public GoPsiElement[] getMembers() {
//        return new GoPsiElement[0];  //To change body of implemented methods use File | Settings | File Templates.
//    }
}
