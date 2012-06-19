package ro.redeul.google.go.lang.psi.impl.types;

import java.util.regex.Pattern;

import com.intellij.lang.ASTNode;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.GoPackageReference;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiPackagedElementBase;
import ro.redeul.google.go.lang.psi.resolve.references.TypeNameReference;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.GoTypeName;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import static com.intellij.patterns.PsiJavaPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.or;
import static com.intellij.patterns.StandardPatterns.string;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 30, 2010
 * Time: 7:12:16 PM
 */
public class GoTypeNameImpl extends GoPsiPackagedElementBase
    implements GoTypeName {

    static final Pattern PRIMITIVE_TYPES_PATTERN =
        Pattern.compile("" +
                            "bool|error|byte|rune|uintptr|string|char|" +
                            "(int|uint)(8|16|32|64)?|" +
                            "float(32|64)|" +
                            "complex(64|128)");

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

    static final ElementPattern<PsiElement> NON_REFERENCES = or(
        psiElement().withParent(GoTypeSpec.class),
        psiElement().withText(
            string().matches(PRIMITIVE_TYPES_PATTERN.pattern()))
    );

    @Override
    public PsiReference getReference() {
        if (NON_REFERENCES.accepts(this))
            return null;

        return new TypeNameReference(this);
    }

    public void accept(GoElementVisitor visitor) {
        visitor.visitTypeName(this);
    }

    @Override
    public GoPsiElement[] getMembers() {
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

        return new GoPsiElement[0];
    }

    @Override
    public GoType getMemberType(String name) {
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
        return null;
    }

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
        return PRIMITIVE_TYPES_PATTERN.matcher(getText()).matches();
    }
}
