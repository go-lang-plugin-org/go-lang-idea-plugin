package ro.redeul.google.go.intentions.control;

import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.SmartPsiElementPointer;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.intentions.Intention;
import ro.redeul.google.go.intentions.IntentionExecutionException;
import ro.redeul.google.go.intentions.statements.MoveSimpleStatementOutIntention;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.binary.GoLogicalOrExpression;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.statements.GoIfStatement;
import ro.redeul.google.go.lang.psi.statements.GoSimpleStatement;
import ro.redeul.google.go.lang.psi.statements.GoStatement;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.*;
import static ro.redeul.google.go.util.EditorUtil.reformatPositions;

public class MergeIfAndIntention extends Intention {
    @Override
    protected boolean satisfiedBy(PsiElement element) {
        if (!isNodeOfType(element, GoTokenTypes.kIF)) {
            return false;
        }

        GoIfStatement ifStatement = findParentOfType(element, GoIfStatement.class);
        if (ifStatement == null || ifStatement.getElseBlock() != null || ifStatement.getElseIfStatement() != null) {
            return false;
        }

        if (ifStatement.getExpression() == null) {
            return false;
        }

        GoBlockStatement thenBlock = ifStatement.getThenBlock();
        if (thenBlock == null ||
            !isNodeOfType(thenBlock.getFirstChild(), GoTokenTypes.pLCURLY) ||
            !isNodeOfType(thenBlock.getLastChild(), GoTokenTypes.pRCURLY)) {
            return false;
        }

        GoStatement[] statements = thenBlock.getStatements();
        if (statements.length != 1 || !(statements[0] instanceof GoIfStatement)) {
            return false;
        }

        GoIfStatement inner = (GoIfStatement) statements[0];
        return inner.getExpression() != null;
    }

    @Override
    protected void processIntention(@NotNull PsiElement element, Editor editor)
            throws IntentionExecutionException {
        final Document document = editor.getDocument();
        GoIfStatement outer = findParentOfType(element, GoIfStatement.class);
        RangeMarker reformatRange = document.createRangeMarker(outer.getTextRange());
        GoIfStatement inner = (GoIfStatement) outer.getThenBlock().getStatements()[0];
        if (outer.getSimpleStatement() != null && inner.getSimpleStatement() != null) {
            SmartPsiElementPointer<GoIfStatement> outerPointer = createSmartElementPointer(outer);
            MoveSimpleStatementOutIntention.moveSimpleStatementOut(editor, outer);
            outer = outerPointer.getElement();
            if (outer == null) {
                return;
            }
            inner = (GoIfStatement) outer.getThenBlock().getStatements()[0];
        }

        // outer if range is text range from "if" to "{", inclusive, and surrounding whitespaces are also included.
        final RangeMarker outerIfRange = getOuterIfRange(document, outer);
        final RangeMarker rightCurlyRange = getRightCurlyRange(document, outer);
        if (outerIfRange == null || rightCurlyRange == null) {
            return;
        }

        final int innerEnd = inner.getThenBlock().getTextOffset();
        final String simpleStatementAndExpression = getSimpleStatementAndExpression(outer, inner);

        final GoIfStatement finalInner = inner;
        WriteCommandAction writeCommandAction = new WriteCommandAction(element.getContainingFile().getProject()) {
            @Override
            protected void run(@NotNull Result result) throws Throwable {
                document.replaceString(finalInner.getTextOffset(), innerEnd, simpleStatementAndExpression);

                document.deleteString(outerIfRange.getStartOffset(), outerIfRange.getEndOffset());
                document.deleteString(rightCurlyRange.getStartOffset(), rightCurlyRange.getEndOffset());
            }
        };
        writeCommandAction.execute();

        PsiFile file = outer.getContainingFile();
        if (file != null) {
            reformatPositions(file, reformatRange);
        }
    }

    private RangeMarker getRightCurlyRange(Document document, GoIfStatement outer) {
        PsiElement rightCurly = outer.getThenBlock().getLastChild();
        if (rightCurly == null) {
            return null;
        }
        PsiElement startElement = getPrevSiblingIfItsWhiteSpaceOrComment(rightCurly.getPrevSibling());
        if (startElement == null) {
            return null;
        }
        int start = startElement.getTextRange().getEndOffset();
        int end = rightCurly.getTextRange().getEndOffset();
        return document.createRangeMarker(start, end);
    }

    private RangeMarker getOuterIfRange(Document document, GoIfStatement outer) {
        PsiElement leftCurly = outer.getThenBlock().getFirstChild();
        if (leftCurly == null) {
            return null;
        }
        PsiElement endElement = getNextSiblingIfItsWhiteSpaceOrComment(leftCurly.getNextSibling());
        if (endElement == null) {
            return null;
        }
        return document.createRangeMarker(outer.getTextOffset(), endElement.getTextOffset());
    }

    private String getSimpleStatementAndExpression(GoIfStatement outer, GoIfStatement inner) {
        String simpleStatement = getSimpleStatement(outer, inner);
        String expression = getExpression(outer, inner);

        if (simpleStatement.isEmpty()) {
            return "if " + expression;
        }
        return String.format("if %s; %s", simpleStatement, expression);
    }

    private String getSimpleStatement(GoIfStatement outer, GoIfStatement inner) {
        GoSimpleStatement statement = outer.getSimpleStatement();
        if (statement != null) {
            return statement.getText();
        }

        statement = inner.getSimpleStatement();
        if (statement != null) {
            return statement.getText();
        }

        return "";
    }

    private String getExpression(GoIfStatement outer, GoIfStatement inner) {
        return getExpressionDeclaration(outer) + " && " + getExpressionDeclaration(inner);
    }

    private String getExpressionDeclaration(GoIfStatement ifStatement) {
        GoExpr expr = ifStatement.getExpression();
        return isOrExpression(expr) ? "(" + expr.getText() + ")" : expr.getText();
    }

    private static boolean isOrExpression(GoExpr expr) {
        return expr instanceof GoLogicalOrExpression;
    }
}
