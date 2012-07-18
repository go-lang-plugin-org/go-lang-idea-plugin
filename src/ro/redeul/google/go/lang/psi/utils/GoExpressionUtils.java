package ro.redeul.google.go.lang.psi.utils;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;

import static ro.redeul.google.go.lang.psi.utils.GoIdentifierUtils.getFunctionDeclaration;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findChildOfType;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;

public class GoExpressionUtils {
    @Nullable
    public static GoLiteralIdentifier getCallFunctionIdentifier(@Nullable GoCallOrConvExpression call) {
        if (call == null) {
            return null;
        }

        GoPrimaryExpression baseExpression = call.getBaseExpression();
        if (baseExpression instanceof GoLiteralExpression) {
            GoLiteralExpression literal = (GoLiteralExpression) baseExpression;
            PsiElement child = literal.getLiteral();
            return child instanceof GoLiteralIdentifier ? (GoLiteralIdentifier) child : null;
        }

        if (baseExpression instanceof GoSelectorExpression) {
            return ((GoSelectorExpression) baseExpression).getIdentifier();
        }
        return null;

    }

    /**
     * Find corresponding function declaration of a function call.
     * @param element should be a GoCallOrConvExpression or child of GoCallOrConvExpression
     * @return null if declaration can't be found
     */
    @Nullable
    public static GoFunctionDeclaration resolveToFunctionDeclaration(@Nullable PsiElement element) {
        GoCallOrConvExpression callExpr = findParentOfType(element, GoCallOrConvExpression.class);
        return getFunctionDeclaration(getCallFunctionIdentifier(callExpr));
    }

    @NotNull
    public static TextRange getCallParenthesesTextRange(@NotNull GoCallOrConvExpression call) {
        PsiElement lp = findChildOfType(call, GoTokenTypes.pLPAREN);
        PsiElement rp = findChildOfType(call, GoTokenTypes.pRPAREN);
        if (lp == null || rp == null) {
            return TextRange.EMPTY_RANGE;
        }

        return new TextRange(lp.getTextOffset(), rp.getTextRange().getEndOffset());
    }
}
