package ro.redeul.google.go.lang.psi.utils;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;

import static com.intellij.patterns.PsiJavaPatterns.psiElement;

public class GoIdentifierUtils {
    private static final ElementPattern FUNCTION_DECLARATION_PATTERN =
            psiElement(GoLiteralIdentifier.class)
                    .withParent(
                            psiElement(GoFunctionDeclaration.class));

    public static boolean isFunctionDeclarationIdentifier(@Nullable PsiElement identifier) {
        return FUNCTION_DECLARATION_PATTERN.accepts(identifier);
    }

    @Nullable
    public static GoFunctionDeclaration getFunctionDeclaration(@Nullable PsiElement identifier) {
        if (!(identifier instanceof GoLiteralIdentifier)) {
            return null;
        }

        // If the identifier is not definition identifier of function, try to resolve it.
        if (!isFunctionDeclarationIdentifier(identifier)) {
            identifier = resolveIdentifier((GoLiteralIdentifier) identifier);
        }

        if (identifier == null || !isFunctionDeclarationIdentifier(identifier)) {
            return null;
        }

        PsiElement parent = identifier.getParent();
        return parent instanceof GoFunctionDeclaration ? (GoFunctionDeclaration) parent : null;
    }

    @Nullable
    public static PsiElement resolveIdentifier(@Nullable GoLiteralIdentifier id) {
        if (id == null) {
            return null;
        }

        PsiReference reference = id.getReference();
        return reference == null ? null : reference.resolve();
    }
}
