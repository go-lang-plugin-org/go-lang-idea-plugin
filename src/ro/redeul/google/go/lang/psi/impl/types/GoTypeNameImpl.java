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
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiPackagedElementBase;
import ro.redeul.google.go.lang.psi.processors.NamedTypeVariantsCollector;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.processors.NamedTypeResolver;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.GoTypeName;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 30, 2010
 * Time: 7:12:16 PM
 */
public class GoTypeNameImpl extends GoPsiPackagedElementBase implements GoTypeName {

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

    public GoTypeNameDeclaration resolve() {

        NamedTypeResolver namedTypesProcessor = new NamedTypeResolver(this);

        if (!PsiScopesUtil.treeWalkUp(namedTypesProcessor, this, this.getContainingFile(), GoResolveStates.initial()))
        {
            return (GoTypeNameDeclaration) namedTypesProcessor.getResolvedTypeName();
        }

        return null;
    }

    @NotNull
    public Object[] getVariants() {

        NamedTypeVariantsCollector namedTypesProcessor = new NamedTypeVariantsCollector();

        PsiScopesUtil.treeWalkUp(namedTypesProcessor, this, this.getContainingFile(), GoResolveStates.initial());

        return namedTypesProcessor.references();
    }

    public boolean isSoft() {
        return false;
    }

    public void accept(GoElementVisitor visitor) {
        visitor.visitTypeName(this);
    }

    @Override
    public GoPsiElement[] getMembers() {
        GoTypeNameDeclaration declaration = resolve();

        if ( declaration != null && declaration.getTypeSpec() != null ) {

            GoType declarationType = declaration.getTypeSpec().getType();

            if (declarationType != null) {
                return declarationType.getMembers();
            }
        }

        return new GoPsiElement[0];
    }

    @Override
    public GoType getMemberType(String name) {
        GoTypeNameDeclaration declaration = resolve();

        if ( declaration != null && declaration.getTypeSpec() != null ) {

            GoType declarationType = declaration.getTypeSpec().getType();

            if (declarationType != null) {
                return declarationType.getMemberType(name);
            }
        }

        return null;
    }
}
