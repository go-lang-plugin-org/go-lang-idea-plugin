package ro.redeul.google.go.intentions.statements;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.intentions.IntentionExecutionException;

import static ro.redeul.google.go.util.EditorUtil.reformatPositions;


public class ConvertStatementToIfIntention extends BaseBoolStatement {


    @Override
    protected void processIntention(@NotNull PsiElement element, Editor editor)
            throws IntentionExecutionException {
        Document document = editor.getDocument();
        TextRange textRange = statement.getTextRange();
        RangeMarker range = document.createRangeMarker(textRange.getStartOffset(), textRange.getEndOffset());
        document.deleteString(textRange.getStartOffset(), textRange.getEndOffset());
        document.insertString(textRange.getStartOffset(), "if " + expr.getText() + "{" + "}");
        if (editor != null) editor.getCaretModel().moveToOffset(textRange.getStartOffset() + 4 + expr.getTextLength());
        reformatPositions(statement.getContainingFile(), range);
    }

}
