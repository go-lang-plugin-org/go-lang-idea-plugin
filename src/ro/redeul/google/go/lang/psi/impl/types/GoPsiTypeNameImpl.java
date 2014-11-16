package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.packages.GoPackages;
import ro.redeul.google.go.lang.psi.GoPackage;
import ro.redeul.google.go.lang.psi.GoPackageReference;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.resolve.refs.TypeNameReference;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.string;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 30, 2010
 * Time: 7:12:16 PM
 */
public class GoPsiTypeNameImpl extends GoPsiElementBase implements GoPsiTypeName {

    public GoPsiTypeNameImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    @NotNull
    public String getName() {
        GoLiteralIdentifier identifiers[] =
                findChildrenByClass(GoLiteralIdentifier.class);

        String name = null;
        if ( identifiers.length > 0)
            name = identifiers[identifiers.length - 1].getName();

        return name != null ? name : getText();
    }

    @NotNull
    @Override
    public PsiElement getNavigationElement() {
        GoLiteralIdentifier identifiers[] =
                findChildrenByClass(GoLiteralIdentifier.class);

        GoLiteralIdentifier identifier = null;
        if (identifiers.length > 0)
            identifier = identifiers[identifiers.length - 1];

        return identifier == null ? this : identifier;
    }

    @Override
    public PsiElement setName(@NotNull @NonNls String name)
            throws IncorrectOperationException {
        return null;
    }

    public GoPackageReference getPackageReference() {
        return findChildByClass(GoPackageReference.class);
    }

    private static final ElementPattern<PsiElement> NIL_TYPE =
            psiElement()
                    .withText(string().matches("nil"));

    @Override
    protected PsiReference[] defineReferences() {
        if (NIL_TYPE.accepts(this))
            return PsiReference.EMPTY_ARRAY;

        GoPackage goPackage = null;
        if (findChildByType(GoTokenTypes.oDOT) != null) {
            GoLiteralIdentifier identifiers[] = findChildrenByClass(GoLiteralIdentifier.class);
            GoImportDeclaration importDeclaration = GoPsiUtils.resolveSafely(identifiers[0], GoImportDeclaration.class);
            goPackage = importDeclaration != null ? importDeclaration.getPackage() : null;
        }

        if ( goPackage != null && goPackage == GoPackages.C )
            return PsiReference.EMPTY_ARRAY;

        return new PsiReference[] { goPackage != null ? new TypeNameReference(this, goPackage) : new TypeNameReference(this) };
    }

    public void accept(GoElementVisitor visitor) {
        visitor.visitTypeName(this);
    }

    @NotNull
    @Override
    @SuppressWarnings("ConstantConditions")
    public GoLiteralIdentifier getIdentifier() {
        // we cannot parse a type name unless we really have an identifier so this should always return something
        return findChildByClass(GoLiteralIdentifier.class);
    }

    @Override
    public boolean isReference() {
        return findChildByType(GoTokenTypes.oMUL) != null;
    }

    @Override
    public boolean isPrimitive() {
        return GoTypes.PRIMITIVE_TYPES_PATTERN.matcher(getText()).matches();
    }

    @Override
    public String getLookupTailText() {
        return getText();
    }
}
