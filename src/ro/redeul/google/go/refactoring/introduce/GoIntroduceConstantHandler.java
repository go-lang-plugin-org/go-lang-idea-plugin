package ro.redeul.google.go.refactoring.introduce;

import com.intellij.codeInsight.CodeInsightUtilBase;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateEditingAdapter;
import com.intellij.codeInsight.template.TemplateEditingListener;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import ro.redeul.google.go.GoLanguage;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;
import ro.redeul.google.go.refactoring.GoRefactoringException;

import static ro.redeul.google.go.editor.TemplateUtil.runTemplate;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isEnclosedByParenthesis;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNodeOfType;

public class GoIntroduceConstantHandler extends GoIntroduceHandlerBase {
    private static final String VARIABLE = "____INTRODUCE_CONSTANT____";

    @Override
    protected void doIntroduce(Project project, Editor editor, GoFile file, int start, int end) throws GoRefactoringException {
        GoExpr e = CodeInsightUtilBase.findElementInRange(file, start, end, GoExpr.class, GoLanguage.INSTANCE);
        if (e == null) {
            throw new GoRefactoringException("It's not a valid expression!");
        }

        if (isNodeOfType(e.getParent(), GoElementTypes.EXPRESSION_PARENTHESIZED)) {
            // If there is a pair of parenthesis enclosed the expression, include the parenthesis.
            e = (GoExpr) e.getParent();
            start = e.getTextOffset();
            end = start + e.getTextLength();
        }

        // Remove redundant parenthesis around declaration.
        boolean needToRemoveParenthesis = isNodeOfType(e, GoElementTypes.EXPRESSION_PARENTHESIZED);

        PsiDocumentManager manager = PsiDocumentManager.getInstance(project);
        Document document = manager.getDocument(file);
        if (document == null) {
            return;
        }

        String declaration = e.getText().trim();
        if (needToRemoveParenthesis) {
            declaration = declaration.substring(1, declaration.length() - 1);
        }

        RangeMarker exprMarker = document.createRangeMarker(start, end);
        editor.getCaretModel().moveToOffset(end);

        GoConstDeclarations[] allConstDeclarations = file.getConsts();
        if (allConstDeclarations.length > 0) {
            GoConstDeclarations declarations = allConstDeclarations[allConstDeclarations.length - 1];
            appendConstToLastDeclaration(editor, exprMarker, declaration, declarations);
        } else {
            appendConstToLastImportOrPackage(editor, exprMarker, file, declaration);
        }
    }

    private void appendConstToLastImportOrPackage(Editor editor, RangeMarker exprMarker, GoFile file, String declaration) {
        GoPsiElement lastElement;
        GoImportDeclarations[] imports = file.getImportDeclarations();
        if (imports.length != 0) {
            lastElement = imports[imports.length - 1];
        } else {
            lastElement = file.getPackage();
        }

        int offset = lastElement.getTextRange().getEndOffset();
        String stmt = "\n\nconst $" + VARIABLE + "$ = " + declaration;
        startRenaming(editor, exprMarker, offset, 0, stmt);
    }

    private void appendConstToLastDeclaration(Editor editor, RangeMarker exprMarker, String declaration, GoConstDeclarations declarations) {
        int offset;
        int originalLength;
        String stmt;
        GoConstDeclaration[] consts = declarations.getDeclarations();
        GoConstDeclaration lastConst = consts[consts.length - 1];
        if (consts.length == 1 && !isEnclosedByParenthesis(consts[0])) {
            offset = lastConst.getTextOffset();
            originalLength = lastConst.getTextLength();
            StringBuilder sb = new StringBuilder("(\n");
            sb.append("    ").append(lastConst.getText()).append("\n");
            sb.append("    $").append(VARIABLE).append("$ = ").append(declaration).append("\n");
            sb.append(")");
            stmt = sb.toString();
        } else {
            offset = lastConst.getTextOffset() + lastConst.getTextLength();
            originalLength = 0;
            stmt = "\n    $" + VARIABLE + "$ = " + declaration;
        }

        startRenaming(editor, exprMarker, offset, originalLength, stmt);
    }

    private void startRenaming(Editor editor, RangeMarker exprMarker, int offset, int originalLength, String stmt) {
        Document document = editor.getDocument();
        document.replaceString(offset, offset + originalLength, stmt);
        RangeMarker defMarker = document.createRangeMarker(offset, offset + stmt.length());
        // replace expression with const
        document.replaceString(exprMarker.getStartOffset(), exprMarker.getEndOffset(), '$' + VARIABLE + '$');

        int start = Math.min(defMarker.getStartOffset(), exprMarker.getStartOffset());
        int end = Math.max(defMarker.getEndOffset(), exprMarker.getEndOffset());
        runTemplate(editor, new TextRange(start, end), VARIABLE, "VALUE");
    }
}
