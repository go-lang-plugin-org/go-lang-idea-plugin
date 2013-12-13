package ro.redeul.google.go.lang.psi.utils;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class GoIdentifierUtils {
    private static final ElementPattern FUNCTION_DECLARATION_PATTERN =
            psiElement(GoLiteralIdentifier.class)
                    .withParent(
                            psiElement(GoFunctionDeclaration.class));

    private static boolean isFunctionDeclarationIdentifier(@Nullable PsiElement identifier) {
        return FUNCTION_DECLARATION_PATTERN.accepts(identifier);
    }

    @Nullable
    public static GoFunctionDeclaration getFunctionDeclaration(@Nullable PsiElement identifier) {
        if (!(identifier instanceof GoLiteralIdentifier)) {
            return null;
        }

        // If the identifier is not definition identifier of function, try to resolve it.
        if (!isFunctionDeclarationIdentifier(identifier)) {
            identifier = GoPsiUtils.resolveSafely(identifier, PsiElement.class);
        }

        if (identifier == null || !isFunctionDeclarationIdentifier(identifier)) {
            return null;
        }

        PsiElement parent = identifier.getParent();
        return parent instanceof GoFunctionDeclaration ? (GoFunctionDeclaration) parent : null;
    }
}
