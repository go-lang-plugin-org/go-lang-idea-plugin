package ro.redeul.google.go.intentions.conversions;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.intentions.GoIntentionsBundle;
import ro.redeul.google.go.intentions.Intention;
import ro.redeul.google.go.intentions.IntentionExecutionException;
import ro.redeul.google.go.lang.documentation.DocumentUtil;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.statements.GoSimpleStatement;
import ro.redeul.google.go.lang.psi.statements.GoStatement;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchExpressionClause;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchExpressionStatement;
import ro.redeul.google.go.util.expression.FlipBooleanExpression;

import java.util.ArrayList;
import java.util.List;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.*;
import static ro.redeul.google.go.util.EditorUtil.reformatPositions;

public class ConvertSwitchToIfIntention extends Intention {
    @Override
    protected boolean satisfiedBy(PsiElement element) {
        return isNodeOfType(element, GoTokenTypes.kSWITCH) &&
               isNodeOfType(element.getParent(), GoElementTypes.SWITCH_EXPR_STATEMENT);
    }

    @Override
    protected void processIntention(@NotNull PsiElement element, Editor editor)
            throws IntentionExecutionException {
        GoSwitchExpressionStatement se = findParentOfType(element, GoSwitchExpressionStatement.class);
        if (se == null) {
            return;
        }

        checkFallthrough(se);

        Document document = editor.getDocument();
        RangeMarker range = document.createRangeMarker(se.getTextRange());
        SwitchElements switchElements = SwitchElements.create(document, se);
        DocumentUtil.replaceElementWithText(document, se, switchElements.toIf());
        reformatPositions(se.getContainingFile(), range);
    }

    private void checkFallthrough(GoSwitchExpressionStatement se) {
        for (GoSwitchExpressionClause clause : se.getClauses()) {
            GoStatement[] statements = clause.getStatements();
            if (statements.length == 0) {
                continue;
            }

            GoStatement lastStatement = statements[statements.length - 1];
            if (isNodeOfType(lastStatement, GoElementTypes.FALLTHROUGH_STATEMENT)) {
                int start = lastStatement.getTextOffset() - se.getTextOffset();
                String msg = GoIntentionsBundle.message("error.refuse.to.convert.fallthrough");
                throw new IntentionExecutionException(msg, start, lastStatement.getTextLength());
            }
        }
    }

    private static class SwitchElements {
        public final String simpleStatement;
        public final String expression;
        public final List<CaseElement> cases;
        public final CaseElement defaultElement;

        private SwitchElements(String simpleStatement, String expression, List<CaseElement> cases,
                               CaseElement defaultElement) {
            this.simpleStatement = simpleStatement;
            this.expression = expression;
            this.cases = cases;
            this.defaultElement = defaultElement;
        }

        private String toIf() {
            StringBuilder sb = new StringBuilder("if ");
            if (!simpleStatement.isEmpty()) {
                sb.append(simpleStatement).append("; ");
            }

            for (int i = 0; i < cases.size(); i++) {
                if (i > 0) {
                    sb.append(" else if ");
                }
                CaseElement c = cases.get(i);
                sb.append(c.getCondition(expression)).append(" {\n").append(c.statements).append("\n}");
            }

            if (cases.isEmpty()) {
                sb.append("false {\n}");
            }

            if (defaultElement != null) {
                sb.append(" else {\n").append(defaultElement.statements).append("\n}");
            }
            return sb.toString();
        }

        private static SwitchElements create(Document document, GoSwitchExpressionStatement se) {
            GoSimpleStatement simpleStatement = se.getSimpleStatement();
            String ss = simpleStatement == null ? "" : simpleStatement.getText();
            GoExpr expression = se.getExpression();
            boolean expressionIsTrue = expression == null || "true".equals(expression.getText());
            boolean expressionIsFalse = expression != null && "false".equals(expression.getText());
            String es = expressionIsTrue || expressionIsFalse ? "" : expression.getText();
            CaseElement defaultCase = null;
            List<CaseElement> cases = new ArrayList<CaseElement>();
            for (GoSwitchExpressionClause clause : se.getClauses()) {
                CaseElement c = CaseElement.create(document, clause, expressionIsFalse);
                if (clause.isDefault()) {
                    defaultCase = c;
                } else if (c != null) {
                    cases.add(c);
                }
            }
            return new SwitchElements(ss, es, cases, defaultCase);
        }
    }

    private static class CaseElement {
        public final List<String> expressions;
        public final String statements;

        private CaseElement(List<String> expressions, String statements) {
            this.expressions = expressions;
            this.statements = statements;
        }

        private String getCondition(String e) {
            StringBuilder sb = new StringBuilder();
            for (String expr : expressions) {
                if (sb.length() > 0) {
                    sb.append(" || ");
                }
                if (e.isEmpty()) {
                    sb.append(expr);
                } else {
                    sb.append(e).append(" == ").append(expr);
                }
            }
            return sb.toString();
        }

        private static CaseElement create(Document document, GoSwitchExpressionClause clause, boolean flipExpression) {
            List<String> expressions = new ArrayList<String>();
            for (GoExpr expr : clause.getExpressions()) {
                expressions.add(flipExpression ? FlipBooleanExpression.flip(expr) : expr.getText());
            }
            PsiElement colon = findChildOfType(clause, GoTokenTypes.oCOLON);
            if (colon == null) {
                return null;
            }

            int start = colon.getTextOffset() + 1;
            int end = clause.getTextRange().getEndOffset();
            String statements = document.getText(TextRange.create(start, end)).trim();
            return new CaseElement(expressions, statements);
        }
    }
}
