package ro.redeul.google.go.intentions.conversions;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.intentions.Intention;
import ro.redeul.google.go.intentions.IntentionExecutionException;
import ro.redeul.google.go.lang.documentation.DocumentUtil;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralString;

public class ConvertToInterpretedStringIntention extends Intention {
    @Override
    protected boolean satisfiedBy(PsiElement element) {
        return element instanceof GoLiteralString &&
               ((GoLiteralString) element).getType() == GoLiteral.Type.RawString;
    }

    @Override
    protected void processIntention(@NotNull PsiElement element, Project project, Editor editor)
            throws IntentionExecutionException {
        String text = convertToInterpretedString(element.getText());
        DocumentUtil.replaceElementWithText(editor.getDocument(), element, text);
    }

    private String convertToInterpretedString(String text) throws IntentionExecutionException {
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 1; i < text.length() - 1; i++) {
            char c = text.charAt(i);
            switch (c) {
                case '\n':
                    sb.append("\\n");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.append('"').toString();
    }
}
