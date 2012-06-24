package ro.redeul.google.go.intentions.control;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.intentions.Intention;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.statements.GoIfStatement;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNodeOfType;
import static ro.redeul.google.go.util.EditorUtil.reformatPositions;

public class SplitIfIntention extends Intention {
    @Override
    protected boolean satisfiedBy(PsiElement element) {
        // only split logical and expression.
        PsiElement parent = element.getParent();
        if (!isNodeOfType(parent, GoElementTypes.LOG_AND_EXPRESSION)) {
            return false;
        }

        PsiElement leftExpr = parent.getFirstChild();
        PsiElement rightExpr = parent.getLastChild();

        if (!"&&".equals(element.getText()) || leftExpr == rightExpr ||
            !isNodeOfType(rightExpr, GoElementTypes.REL_EXPRESSION)) {
            return false;
        }

        PsiElement grandpa = parent.getParent();
        if (!(grandpa instanceof GoIfStatement)) {
            return false;
        }

        GoIfStatement ifStmt = (GoIfStatement) grandpa;
        return ifStmt.getThenBlock() != null &&
               ifStmt.getElseBlock() == null &&
               ifStmt.getElseIfStatement() == null;
    }

    @Override
    protected void processIntention(@NotNull PsiElement element, Project project, Editor editor)
            throws IncorrectOperationException {
        PsiElement parent = element.getParent();
        GoIfStatement ifStmt = (GoIfStatement) parent.getParent();
        PsiElement leftExpr = parent.getFirstChild();
        PsiElement rightExpr = parent.getLastChild();
        if (leftExpr == null || rightExpr == null ||
            !isNodeOfType(leftExpr, GoElementTypes.REL_EXPRESSION) ||
            !isNodeOfType(rightExpr, GoElementTypes.REL_EXPRESSION)) {
            return;
        }

        GoBlockStatement then = ifStmt.getThenBlock();
        TextRange thenRange = then.getTextRange();
        Document doc = editor.getDocument();
        RangeMarker thenRangeMarker = doc.createRangeMarker(thenRange);

        int lineStartOffset = doc.getLineStartOffset(doc.getLineNumber(thenRange.getEndOffset()));
        doc.insertString(lineStartOffset, "}\n");

        int lineEndOffset = doc.getLineEndOffset(doc.getLineNumber(thenRange.getStartOffset()));
        doc.insertString(lineEndOffset, String.format("\nif %s {", rightExpr.getText()));

        doc.deleteString(leftExpr.getTextRange().getEndOffset(), rightExpr.getTextRange().getEndOffset());

        PsiFile file = element.getContainingFile();
        if (file != null) {
            reformatPositions(file, thenRangeMarker.getStartOffset(), thenRangeMarker.getEndOffset());
        }
    }
}
