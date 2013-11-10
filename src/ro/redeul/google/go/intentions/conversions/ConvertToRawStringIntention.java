package ro.redeul.google.go.intentions.conversions;

import com.google.common.base.Charsets;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.intentions.GoIntentionsBundle;
import ro.redeul.google.go.intentions.Intention;
import ro.redeul.google.go.intentions.IntentionExecutionException;
import ro.redeul.google.go.lang.documentation.DocumentUtil;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ConvertToRawStringIntention extends Intention {

    @Override
    protected boolean satisfiedBy(PsiElement element) {
        return element instanceof GoLiteralString &&
               ((GoLiteralString) element).getType() == GoLiteral.Type.InterpretedString;
    }

    @Override
    protected void processIntention(@NotNull PsiElement element, Editor editor)
            throws IntentionExecutionException {
        String text = convertToRawString(element.getText());
        DocumentUtil.replaceElementWithText(editor.getDocument(), element, text);
    }

    private static String convertToRawString(String text) throws IntentionExecutionException {
        byte[] textBytes = text.getBytes(Charsets.UTF_8);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            doConvert(textBytes, baos);
            return new String(baos.toByteArray(), Charsets.UTF_8);
        } catch (IOException e) {
            throw new IntentionExecutionException(GoIntentionsBundle.message("error.convert.error"));
        }
    }

    // unescape all data in "textBytes", and write to "out"
    private static void doConvert(byte[] textBytes, OutputStream out) throws IOException {
        out.write('`');
        for (int i = 1; i < textBytes.length - 1; i++) {
            byte b = textBytes[i];
            if (b != '\\') {
                out.write(b);
                continue;
            }

            b = textBytes[i + 1];
            switch (b) {
                case '\\':
                    out.write('\\');
                    break;
                case 't':
                    out.write('\t');
                    break;
                case 'r':
                    out.write('\n');
                    if (textBytes.length >= i + 4 && textBytes[i + 2] == '\\' && textBytes[i + 3] == 'n') {
                        i += 2;
                    }
                    break;
                case 'n':
                    out.write('\n');
                    break;
                case '"':
                    out.write('"');
                    break;
                case 'x':
                    try {
                        out.write(Integer.parseInt(new String(textBytes, i + 2, 2), 16));
                        i += 2;
                    } catch (Exception e) {
                        String msg = GoIntentionsBundle.message("error.unknown.escape.sequence", (char) b);
                        throw new IntentionExecutionException(msg, i, 2);
                    }
                    break;
                case 'u':
                case 'U':
                    int len = b == 'u' ? 4 : 8;
                    try {
                        int codePoint = Integer.parseInt(new String(textBytes, i + 2, len), 16);
                        out.write(new String(Character.toChars(codePoint)).getBytes(Charsets.UTF_8));
                        i += len;
                    } catch (Exception e) {
                        String msg = GoIntentionsBundle.message("error.unknown.escape.sequence", (char) b);
                        throw new IntentionExecutionException(msg, i, 2);
                    }
                    break;
                default:
                    String msg = GoIntentionsBundle.message("error.unknown.escape.sequence", (char) b);
                    throw new IntentionExecutionException(msg, i, 2);
            }
            i++;
        }
        out.write('`');
    }
}
