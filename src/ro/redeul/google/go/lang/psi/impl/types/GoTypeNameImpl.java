package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.GoPackageReference;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiPackagedElementBase;
import ro.redeul.google.go.lang.psi.resolve.references.BuiltinTypeNameReference;
import ro.redeul.google.go.lang.psi.resolve.references.TypeNameReference;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.GoTypeName;
import ro.redeul.google.go.lang.psi.types.GoTypes;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypePredeclared;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import static com.intellij.patterns.PsiJavaPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.string;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 30, 2010
 * Time: 7:12:16 PM
 */
public class GoTypeNameImpl extends GoPsiPackagedElementBase
    implements GoTypeName {

    public GoTypeNameImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    @NotNull
    public String getName() {
        GoLiteralIdentifier identifier = findChildByClass(
            GoLiteralIdentifier.class);

        return identifier != null ? identifier.getText() : getText();
    }

    public PsiElement setName(@NonNls String name)
        throws IncorrectOperationException {
        return null;
    }

    public GoPackageReference getPackageReference() {
        return findChildByClass(GoPackageReference.class);
    }

    static final ElementPattern<PsiElement> NON_REFERENCES =
        psiElement()
            .withText(
                string().matches(GoTypes.PRIMITIVE_TYPES_PATTERN.pattern()));

    @Override
    public PsiReference getReference() {
        if (NON_REFERENCES.accepts(this))
            return new BuiltinTypeNameReference(this);

        return new TypeNameReference(this);
    }

    public void accept(GoElementVisitor visitor) {
        visitor.visitTypeName(this);
    }

//    @Override
//    public GoPsiElement[] getMembers() {
//        GoTypeNameDeclaration declaration = resolve();
//
//        if (declaration != null && declaration.getTypeSpec() != null) {
//
//            GoType declarationType = declaration.getTypeSpec().getType();
//
//            if (declarationType != null) {
//                return declarationType.getMembers();
//            }
//        }

//        return new GoPsiElement[0];
//    }

//    @Override
//    public GoType getMemberType(String name) {
//        GoTypeNameDeclaration declaration = resolve();
//
//        if (declaration != null && declaration.getTypeSpec() != null) {
//
//            GoType declarationType = declaration.getTypeSpec().getType();
//
//            if (declarationType != null) {
//                return declarationType.getMemberType(name);
//            }
//        }
//
//        return null;
//    }

    @Override
    public GoUnderlyingType getUnderlyingType() {

        if (psiElement()
            .withText(string().matches(GoTypes.PRIMITIVE_TYPES_PATTERN.pattern()))
            .accepts(this))
        {
            return GoUnderlyingTypePredeclared.getForName(getText());
        }

        PsiReference reference = getReference();
        if (reference == null ){
            return GoUnderlyingType.Undefined;
        }

        PsiElement resolved = reference.resolve();
        if (resolved == null) {
            return GoUnderlyingType.Undefined;
        }

        if (resolved instanceof GoTypeSpec) {
            GoTypeSpec spec = (GoTypeSpec)resolved;
            return spec.getType().getUnderlyingType();
        }

        return GoUnderlyingType.Undefined;
    }

    @Override
    public boolean isIdentical(GoType goType) {
        if ( goType instanceof GoTypeName ) {
            return getName().equals(goType.getName());
        }

        return false;
    }

    @NotNull
    @Override
    public GoLiteralIdentifier getIdentifier() {
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
}
