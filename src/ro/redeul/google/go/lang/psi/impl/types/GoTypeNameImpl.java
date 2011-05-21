package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.scope.util.PsiScopesUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPackageReference;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementImpl;
import ro.redeul.google.go.lang.psi.processors.NamedTypesProcessor;
import ro.redeul.google.go.lang.psi.processors.TypesReferencesScopeProcessor;
import ro.redeul.google.go.lang.psi.types.GoTypeName;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 30, 2010
 * Time: 7:12:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoTypeNameImpl extends GoPsiElementImpl implements GoTypeName {

    public GoTypeNameImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return "TypeName";
    }

    @Override
    public String getName() {

        GoIdentifier identifier = findChildByClass(GoIdentifier.class);

        return identifier != null ? identifier.getText() : getText();
    }

    public PsiElement setName(@NonNls String name) throws IncorrectOperationException {
        return null;
    }

    public PsiElement getElement() {
        return this;
    }

    public TextRange getRangeInElement() {
        return new TextRange(0, getTextLength());
    }

    public GoPackageReference getPackageReference() {
        return findChildByClass(GoPackageReference.class);
    }

    public PsiElement resolve() {

        TypesReferencesScopeProcessor namedTypesProcessor = new TypesReferencesScopeProcessor(this);

        if (!PsiScopesUtil.treeWalkUp(namedTypesProcessor, this, this.getContainingFile())) {
            return namedTypesProcessor.getFoundType();
        }

        return null;
    }

    @NotNull
    public String getCanonicalText() {
        return getText();
    }

    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        return this;
    }

    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        if (isReferenceTo(element))
            return this;

        throw new IncorrectOperationException("Cannot bind to:" + element + " of class " + element.getClass());
    }

    @Override
    public PsiReference getReference() {
        return this;
    }

    public boolean isReferenceTo(PsiElement element) {
        return true;
    }

    @NotNull
    public Object[] getVariants() {

        NamedTypesProcessor namedTypesProcessor = new NamedTypesProcessor();

        PsiScopesUtil.treeWalkUp(namedTypesProcessor, this, this.getContainingFile());

        return namedTypesProcessor.references();
    }

    public boolean isSoft() {
        return false;
    }

    public void accept(GoElementVisitor visitor) {
        visitor.visitTypeName(this);
    }

}
