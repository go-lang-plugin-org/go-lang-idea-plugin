package ro.redeul.google.go.intentions.parenthesis;

import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.intentions.Intention;

import static ro.redeul.google.go.intentions.parenthesis.ParenthesisUtil.*;
import static ro.redeul.google.go.util.EditorUtil.reformatPositions;

public class AddDeclarationParenthesesIntention extends Intention {
    @Override
    protected boolean satisfiedBy(PsiElement element) {
        return getRightParenthesis(element) == null && hasOnlyOneDeclaration(element);
    }

    @Override
    protected void processIntention(@NotNull PsiElement element, Editor editor)
            throws IncorrectOperationException {
        PsiElement declaration = getDeclaration(element);
        final TextRange range = declaration.getTextRange();
        final Document document = editor.getDocument();
        final String text = "(\n" + declaration.getText() + "\n)";

        WriteCommandAction writeCommandAction = new WriteCommandAction(element.getContainingFile().getProject()) {
            @Override
            protected void run(@NotNull Result result) throws Throwable {
                document.replaceString(range.getStartOffset(), range.getEndOffset(), text);
            }
        };
        writeCommandAction.execute();

        PsiFile file = element.getContainingFile();
        if (file != null) {
            reformatPositions(file, range.getStartOffset(), range.getStartOffset() + text.length());
        }
    }
}
