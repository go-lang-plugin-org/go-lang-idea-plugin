package ro.redeul.google.go.intentions.control;

import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.intentions.GoIntentionsBundle;
import ro.redeul.google.go.intentions.Intention;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.statements.GoIfStatement;
import ro.redeul.google.go.lang.psi.statements.GoReturnStatement;
import ro.redeul.google.go.lang.psi.statements.GoSimpleStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ro.redeul.google.go.lang.documentation.DocumentUtil.replaceElementWithText;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.*;
import static ro.redeul.google.go.util.EditorUtil.reformatPositions;
import static ro.redeul.google.go.util.expression.FlipBooleanExpression.flip;

public class InvertIfIntention extends Intention {
    @Override
    protected boolean satisfiedBy(PsiElement element) {
        if (element instanceof GoIfStatement) {
            return false;
        }

        GoIfStatement stmt = findParentOfType(element, GoIfStatement.class);
        if (stmt == null) {
            return false;
        }

        GoExpr condition = stmt.getExpression();
        GoBlockStatement thenBlock = stmt.getThenBlock();
        return !(condition == null || thenBlock == null || element.getTextOffset() >= thenBlock.getTextOffset()) && stmt.getElseIfStatement() == null;

    }

    @Override
    protected void processIntention(@NotNull PsiElement element, Editor editor)
            throws IncorrectOperationException {
        GoIfStatement stmt = findParentOfType(element, GoIfStatement.class);
        if (stmt == null) {
            throw new IncorrectOperationException(GoIntentionsBundle.message("error.if.statement.not.found"));
        }

        PsiFile file = stmt.getContainingFile();
        if (file == null) {
            return;
        }

        final Document document = editor.getDocument();
        RangeMarker stmtRange = document.createRangeMarker(stmt.getTextRange());

        GoExpr condition = stmt.getExpression();
        boolean parentIsFunctionDeclaration = isFunctionBlock(stmt.getParent());
        GoBlockStatement thenBlock = stmt.getThenBlock();

        final int rightCurlyPosition = blockRightCurlyPosition(thenBlock);
        final int leftCurlyPosition = blockLeftCurlyPosition(thenBlock);
        if (rightCurlyPosition < 0 || leftCurlyPosition < 0) {
            return;
        }

        GoBlockStatement elseBlock = stmt.getElseBlock();
        boolean hasElseBlock = elseBlock != null;

        List<PsiElement> siblings = getSiblings(stmt);
        if (hasElseBlock) {
            swapThenAndElseBlock(document, condition, thenBlock, elseBlock);
        } else if (parentIsFunctionDeclaration &&
                   !containsNonSpaceCommentOrReturnElements(siblings)) {
            String returnDeclaration = "return";
            if (onlyOneReturnStatement(siblings)) {
                returnDeclaration = concatenateElements(siblings);
            }

            final String finalReturnDeclaration = returnDeclaration;
            WriteCommandAction writeCommandAction = new WriteCommandAction(element.getContainingFile().getProject()) {
                @Override
                protected void run(@NotNull Result result) throws Throwable {
                    document.deleteString(rightCurlyPosition, rightCurlyPosition + 1);
                    document.insertString(leftCurlyPosition + 1, "\n" + finalReturnDeclaration + "\n}");
                }
            };
            writeCommandAction.execute();


            flipCondition(document, condition);

            GoSimpleStatement simpleStatement = stmt.getSimpleStatement();
            if (simpleStatement != null) {
                extractSimpleStatement(stmt, document, condition, simpleStatement);
            }
        } else {
            WriteCommandAction writeCommandAction = new WriteCommandAction(element.getContainingFile().getProject()) {
                @Override
                protected void run(@NotNull Result result) throws Throwable {
                    document.insertString(leftCurlyPosition, "{\n} else ");
                }
            };
            writeCommandAction.execute();

            flipCondition(document, condition);
        }

        reformatPositions(file, stmtRange);
    }

    private String concatenateElements(List<PsiElement> elements) {
        StringBuilder sb = new StringBuilder();
        for (PsiElement element : elements) {
            sb.append(element.getText());
        }
        return sb.toString();
    }

    private void swapThenAndElseBlock(Document document, GoExpr condition, GoBlockStatement thenBlock,
                                      GoBlockStatement elseBlock) {
        String elseBlockDeclaration = elseBlock.getText();
        replaceElementWithText(document, elseBlock, thenBlock.getText());
        replaceElementWithText(document, thenBlock, elseBlockDeclaration);
        flipCondition(document, condition);
    }

    private void extractSimpleStatement(final GoIfStatement stmt, final Document document, final GoExpr condition,
                                        final GoSimpleStatement simpleStatement) {
        final String simpleStatementDecl = simpleStatement.getText() + "\n";

        WriteCommandAction writeCommandAction = new WriteCommandAction(condition.getProject()) {
            @Override
            protected void run(@NotNull Result result) throws Throwable {
                document.deleteString(simpleStatement.getTextOffset(), condition.getTextOffset());
                document.insertString(stmt.getTextOffset(), simpleStatementDecl);
            }
        };
        writeCommandAction.execute();
    }

    private void flipCondition(Document document, GoExpr condition) {
        replaceElementWithText(document, condition, flip(condition));
    }

    private int blockRightCurlyPosition(GoBlockStatement block) {
        PsiElement child = block.getLastChild();
        while (child != null) {
            if (isNodeOfType(child, GoElementTypes.pRCURLY)) {
                return child.getTextOffset();
            }
            child = child.getPrevSibling();
        }
        return -1;
    }

    private int blockLeftCurlyPosition(GoBlockStatement block) {
        PsiElement child = block.getFirstChild();
        while (child != null) {
            if (isNodeOfType(child, GoElementTypes.pLCURLY)) {
                return child.getTextOffset();
            }
            child = child.getPrevSibling();
        }
        return -1;
    }

    private static boolean isFunctionBlock(PsiElement element) {
        return !(element == null || !(element instanceof GoBlockStatement)) && element.getParent() instanceof GoFunctionDeclaration;
    }

    private static List<PsiElement> getSiblings(PsiElement element) {
        if (element == null) {
            return Collections.emptyList();
        }

        List<PsiElement> siblings = new ArrayList<PsiElement>();
        while ((element = element.getNextSibling()) != null) {
            siblings.add(element);
        }

        if (!siblings.isEmpty() &&
            isNodeOfType(siblings.get(siblings.size() - 1), GoElementTypes.pRCURLY)) {
            siblings.remove(siblings.size() - 1);
        }

        // remove trailing spaces
        while (!siblings.isEmpty() &&
            isWhiteSpaceNode(siblings.get(siblings.size() - 1))) {
            siblings.remove(siblings.size() - 1);
        }

        // remove heading spaces
        while (!siblings.isEmpty() &&
               isWhiteSpaceNode(siblings.get(0))) {
            siblings.remove(0);
        }
        return siblings;
    }

    private static boolean containsNonSpaceCommentOrReturnElements(List<PsiElement> elements) {
        for (PsiElement element : elements) {
            if (!isWhiteSpaceOrComment(element) && !(element instanceof GoReturnStatement)) {
                return true;
            }
        }
        return false;
    }

    private static boolean onlyOneReturnStatement(List<PsiElement> elements) {
        int returnCount = 0;
        for (PsiElement element : elements) {
            if (element instanceof GoReturnStatement) {
                returnCount++;
            } else if (!isWhiteSpaceOrComment(element)) {
                return false;
            }
        }
        return returnCount == 1;
    }
}
