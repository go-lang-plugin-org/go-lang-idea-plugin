package ro.redeul.google.go.intentions.statements;

import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.intentions.Intention;
import ro.redeul.google.go.intentions.IntentionExecutionException;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.statements.GoForWithClausesStatement;
import ro.redeul.google.go.lang.psi.statements.GoIfStatement;
import ro.redeul.google.go.lang.psi.statements.GoSimpleStatement;
import ro.redeul.google.go.lang.psi.statements.GoStatement;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchExpressionStatement;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchTypeStatement;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

import java.util.*;

import static com.intellij.psi.util.PsiTreeUtil.findFirstParent;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.*;
import static ro.redeul.google.go.util.EditorUtil.reformatPositions;

public class MoveSimpleStatementOutIntention extends Intention {

    private static final Comparator<PsiElement> REVERSE_POSITION_COMPARATOR = new Comparator<PsiElement>() {
        @Override
        public int compare(PsiElement o1, PsiElement o2) {
            return o2.getTextOffset() - o1.getTextOffset();
        }
    };

    @Override
    protected boolean satisfiedBy(PsiElement element) {
        return isInIfStatement(element) ||
               isInSwitchStatement(element) ||
               isInForClauseStatement(element);
    }

    private static boolean isInIfStatement(PsiElement element) {
        GoIfStatement stmt = findParentOfType(element, GoIfStatement.class);
        if (stmt == null) {
            return false;
        }

        GoSimpleStatement ss = stmt.getSimpleStatement();
        return ss != null && ss.getTextRange().contains(element.getTextRange());
    }

    private static boolean isInSwitchStatement(PsiElement element) {
        GoSwitchExpressionStatement seStmt = findParentOfType(element, GoSwitchExpressionStatement.class);
        if (seStmt != null) {
            GoSimpleStatement ss = seStmt.getSimpleStatement();
            return ss != null && ss.getTextRange().contains(element.getTextRange());
        }

        GoSwitchTypeStatement stStmt = findParentOfType(element, GoSwitchTypeStatement.class);
        if (stStmt != null) {
            GoSimpleStatement ss = stStmt.getSimpleStatement();
            return ss != null && ss.getTextRange().contains(element.getTextRange());
        }
        return false;
    }

    private static boolean isInForClauseStatement(PsiElement element) {
        GoForWithClausesStatement forStatement = findParentOfType(element, GoForWithClausesStatement.class);
        if (forStatement == null) {
            return false;
        }

        GoStatement statement = forStatement.getInitialStatement();
        return statement != null && statement.getTextRange().contains(element.getTextRange());
    }

    @Override
    protected void processIntention(@NotNull PsiElement element, Editor editor)
            throws IntentionExecutionException {
        GoIfStatement ifStatement = findParentOfType(element, GoIfStatement.class);
        if (ifStatement != null) {
            moveSimpleStatementOut(editor, ifStatement);
            return;
        }

        GoSwitchExpressionStatement seStmt = findParentOfType(element, GoSwitchExpressionStatement.class);
        if (seStmt != null) {
            moveSimpleStatementOut(editor, seStmt);
            return;
        }

        GoSwitchTypeStatement stStmt = findParentOfType(element, GoSwitchTypeStatement.class);
        if (stStmt != null) {
            moveSimpleStatementOut(editor, stStmt);
            return;
        }

        GoForWithClausesStatement forStatement = findParentOfType(element, GoForWithClausesStatement.class);
        if (forStatement != null) {
            moveSimpleStatementOut(editor, forStatement);
        }
    }

    private static void moveSimpleStatementOut(Editor editor, GoForWithClausesStatement forStatement) {
        GoStatement simpleStatement = forStatement.getInitialStatement();
        PsiElement semi = findChildOfType(forStatement, GoTokenTypes.oSEMI);
        if (simpleStatement == null || semi == null) {
            return;
        }

        int start = simpleStatement.getTextOffset();
        int end = semi.getTextOffset();
        move(editor, forStatement, simpleStatement.getText(), start, end);
    }

    private static void moveSimpleStatementOut(Editor editor, GoSwitchTypeStatement stStmt) {
        GoSimpleStatement simpleStatement = stStmt.getSimpleStatement();
        PsiElement endElement = stStmt.getTypeGuard();
        endElement = endElement.getPrevSibling();

        if (simpleStatement == null || endElement == null) {
            return;
        }

        int start = simpleStatement.getTextOffset();
        int end = endElement.getTextRange().getEndOffset();
        move(editor, stStmt, simpleStatement.getText(), start, end);
    }

    private static void moveSimpleStatementOut(Editor editor, GoSwitchExpressionStatement seStmt) {
        GoSimpleStatement simpleStatement = seStmt.getSimpleStatement();
        PsiElement endElement = seStmt.getExpression();
        if (endElement == null) {
            endElement = findChildOfType(seStmt, GoTokenTypes.oSEMI);
        } else {
            endElement = endElement.getPrevSibling();
        }

        if (simpleStatement == null || endElement == null) {
            return;
        }

        int start = simpleStatement.getTextOffset();
        int end = endElement.getTextRange().getEndOffset();
        move(editor, seStmt, simpleStatement.getText(), start, end);
    }

    private static void move(Editor editor, final GoStatement statement, final String declaration, final int start, final int end) {
        final Document document = editor.getDocument();
        RangeMarker range = document.createRangeMarker(statement.getTextOffset(), end);
        WriteCommandAction writeCommandAction = new WriteCommandAction(editor.getProject()) {
            @Override
            protected void run(@NotNull Result result) throws Throwable {
                document.deleteString(start, end);
                document.insertString(statement.getTextOffset(), declaration + "\n");
            }
        };
        writeCommandAction.execute();
        reformatPositions(statement.getContainingFile(), range);
    }

    public static void moveSimpleStatementOut(Editor editor, GoIfStatement ifStatement) {
        final GoSimpleStatement simpleStatement = ifStatement.getSimpleStatement();
        final GoExpr condition = ifStatement.getExpression();
        if (simpleStatement == null || condition == null) {
            return;
        }

        moveDependentSimpleStatementsFirst(editor, ifStatement, simpleStatement);

        PsiElement outermostIf = ifStatement;
        while (outermostIf instanceof GoIfStatement && outermostIf.getParent() instanceof GoIfStatement) {
            outermostIf = outermostIf.getParent();
        }

        final Document document = editor.getDocument();
        RangeMarker range = document.createRangeMarker(outermostIf.getTextOffset(), condition.getTextOffset());
        final PsiElement finalOutermostIf = outermostIf;
        WriteCommandAction writeCommandAction = new WriteCommandAction(ifStatement.getContainingFile().getProject()) {
            @Override
            protected void run(@NotNull Result result) throws Throwable {
                document.deleteString(simpleStatement.getTextOffset(), condition.getTextOffset());
                document.insertString(finalOutermostIf.getTextOffset(), simpleStatement.getText() + "\n");
            }
        };
        writeCommandAction.execute();
        reformatPositions(ifStatement.getContainingFile(), range);
    }

    private static void moveDependentSimpleStatementsFirst(Editor editor, GoIfStatement ifStatement,
                                                           GoSimpleStatement simpleStatement) {
        List<GoIfStatement> dependentIfs = findAllDependentIfs(ifStatement, simpleStatement);
        Collections.sort(dependentIfs, REVERSE_POSITION_COMPARATOR);

        for (GoIfStatement dependent : dependentIfs) {
            moveSimpleStatementOut(editor, dependent);
        }
    }

    private static List<GoIfStatement> findAllDependentIfs(GoIfStatement ifStatement,
                                                           GoSimpleStatement simpleStatement) {
        final List<GoIfStatement> dependentIfs = new ArrayList<GoIfStatement>();
        final Set<GoIfStatement> outerIfs = findAllOuterIfs(ifStatement);

        new GoRecursiveElementVisitor() {
            @Override
            public void visitLiteralIdentifier(GoLiteralIdentifier identifier) {
                PsiElement resolve = resolveSafely(identifier, PsiElement.class);
                if (resolve == null) {
                    return;
                }

                PsiElement parent = findFirstParent(resolve, new Condition<PsiElement>() {
                    @Override
                    public boolean value(PsiElement element) {
                        return element instanceof GoIfStatement && outerIfs.contains(element);
                    }
                });

                if (parent instanceof GoIfStatement) {
                    dependentIfs.add((GoIfStatement) parent);
                }
            }
        }.visitElement(simpleStatement);
        return dependentIfs;
    }

    private static Set<GoIfStatement> findAllOuterIfs(GoIfStatement ifStatement) {
        final Set<GoIfStatement> outerIfs = new HashSet<GoIfStatement>();
        while (ifStatement != null && ifStatement.getParent() instanceof GoIfStatement) {
            ifStatement = (GoIfStatement) ifStatement.getParent();
            outerIfs.add(ifStatement);
        }
        return outerIfs;
    }
}
