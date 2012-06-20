package ro.redeul.google.go.refactoring.introduce;

import com.intellij.codeInsight.CodeInsightUtilBase;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import ro.redeul.google.go.GoLanguage;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.statements.GoStatement;
import ro.redeul.google.go.refactoring.GoRefactoringException;

import static ro.redeul.google.go.editor.TemplateUtil.runTemplate;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNodeOfType;

public class GoIntroduceVariableHandler extends GoIntroduceHandlerBase {
    private static final String VARIABLE = "____INTRODUCE_VARIABLE____";

    @Override
    protected void doIntroduce(Project project, Editor editor, GoFile file, int start, int end) throws GoRefactoringException {
        GoExpr e = CodeInsightUtilBase.findElementInRange(file, start, end, GoExpr.class, GoLanguage.INSTANCE);
        if (e == null) {
            throw new GoRefactoringException("It's not a valid expression!");
        }

        if (isNodeOfType(e.getParent(), GoElementTypes.PARENTHESISED_EXPRESSION)) {
            // If there is a pair of parenthesis enclosed the expression, include the parenthesis.
            e = (GoExpr) e.getParent();
            start = e.getTextOffset();
            end = start + e.getTextLength();
        }

        GoStatement stmt = findParentOfType(e, GoStatement.class);
        if (stmt == null) {
            return;
        }

        // Remove redundant parenthesis around declaration.
        boolean needToRemoveParenthesis = isNodeOfType(e, GoElementTypes.PARENTHESISED_EXPRESSION);

        PsiDocumentManager manager = PsiDocumentManager.getInstance(project);
        Document document = manager.getDocument(file);
        if (document == null) {
            return;
        }

        int lineStart = document.getLineStartOffset(document.getLineNumber(stmt.getTextOffset()));
        String declaration = e.getText().trim();
        if (needToRemoveParenthesis) {
            declaration = declaration.substring(1, declaration.length() - 1);
        }

        RangeMarker range = document.createRangeMarker(lineStart, end);
        editor.getCaretModel().moveToOffset(end);

        String originalText = document.getText(new TextRange(lineStart, start));
        String indent = findIndent(originalText);
        String text;
        if (new TextRange(start, end).equals(stmt.getTextRange())) {
            text = String.format(indent + "$%s$ := %s", VARIABLE, declaration);
        } else {
            text = String.format(indent + "$%s$ := %s\n%s$%s$", VARIABLE, declaration, originalText, VARIABLE);
        }
        document.replaceString(lineStart, end, text);
        runTemplate(editor, TextRange.create(range), VARIABLE, "value");
    }
}
