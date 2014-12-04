package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoPackageReference;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
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
        return getIdentifier().getName();
    }

    @NotNull
    @Override
    public PsiElement getNavigationElement() {
        return getIdentifier();
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
        return super.defineReferences();
    }

    public void accept(GoElementVisitor visitor) {
        visitor.visitTypeName(this);
    }

    @SuppressWarnings("ConstantConditions")
    @NotNull
    @Override
    public GoLiteralIdentifier getIdentifier() {
        return (GoLiteralIdentifier) findLastChildByType(GoElementTypes.LITERAL_IDENTIFIER);
    }

    @Override
    public boolean isPrimitive() {
        return GoTypes.PRIMITIVE_TYPES_PATTERN.matcher(getText()).matches();
    }

    @Override
    public boolean isQualified() {
        return findChildByType(GoTokenTypes.oDOT) != null;
    }

    @Nullable
    @Override
    public GoLiteralIdentifier getQualifier() {
        GoLiteralIdentifier[] childIdentifiers = findChildrenByClass(GoLiteralIdentifier.class);
        return childIdentifiers.length == 2 ? childIdentifiers[0] : null;
    }

    @Override
    public String getLookupTailText() {
        return getText();
    }
}
