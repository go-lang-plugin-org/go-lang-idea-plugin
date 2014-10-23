package ro.redeul.google.go.lang.psi.utils;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.getAs;

public class GoIdentifierUtils {
    private static final ElementPattern FUNCTION_DECLARATION_PATTERN = psiElement(GoFunctionDeclaration.class);

    private static boolean isFunctionDeclarationIdentifier(@Nullable PsiElement identifier) {
        return FUNCTION_DECLARATION_PATTERN.accepts(identifier);
    }

    @Nullable
    public static GoFunctionDeclaration getFunctionDeclaration(@Nullable PsiElement identifier) {

        if (!(identifier instanceof GoPsiElement))
            return null;

        GoPsiElement goPsiElement =  (GoPsiElement) identifier;

        // If the identifier is not definition identifier of function, try to resolve it.
        if (!isFunctionDeclarationIdentifier(goPsiElement)) {
            goPsiElement = GoPsiUtils.resolveSafely(goPsiElement, GoPsiElement.class);
        }

        return getAs(GoFunctionDeclaration.class, goPsiElement);
    }
}
