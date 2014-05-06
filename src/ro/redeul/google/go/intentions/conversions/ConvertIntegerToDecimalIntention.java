package ro.redeul.google.go.intentions.conversions;

import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.intentions.Intention;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;

import static ro.redeul.google.go.intentions.conversions.ConversionUtil.isHexInteger;
import static ro.redeul.google.go.intentions.conversions.ConversionUtil.isOctalInteger;

/**
 * Convert hexadecimal or octal integer to decimal
 */
public class ConvertIntegerToDecimalIntention extends Intention {
    @Override
    protected boolean satisfiedBy(PsiElement element) {
        if (!(element instanceof GoLiteralExpression)) {
            return false;
        }

        String text = element.getText();
        return isHexInteger(text) || isOctalInteger(text);
    }

    @Override
    protected void processIntention(@NotNull PsiElement element, final Editor editor)
            throws IncorrectOperationException {
        int value;
        try {
            String text = element.getText();
            if (isHexInteger(text)) {
                value = Integer.parseInt(text.substring(2), 16);
            } else {
                value = Integer.parseInt(text, 8);
            }
        } catch (NumberFormatException e) {
            throw new IncorrectOperationException("Invalid integer");
        }

        final String result = Integer.toString(value);
        final int start = element.getTextOffset();
        final int end = start + element.getTextLength();

        WriteCommandAction writeCommandAction = new WriteCommandAction(element.getContainingFile().getProject()) {
            @Override
            protected void run(@NotNull Result res) throws Throwable {
                editor.getDocument().replaceString(start, end, result);
            }
        };
        writeCommandAction.execute();
    }
}
