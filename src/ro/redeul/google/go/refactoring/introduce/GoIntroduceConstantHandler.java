package ro.redeul.google.go.refactoring.introduce;

import com.intellij.codeInsight.CodeInsightUtilBase;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;
import ro.redeul.google.go.refactoring.GoRefactoringException;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isEnclosedByParenthesis;

public class GoIntroduceConstantHandler extends GoIntroduceHandlerBase {
    @Override
    protected void doIntroduce(Project project, Editor editor, GoFile file, int start, int end) throws GoRefactoringException {
        GoPsiElementBase e = CodeInsightUtilBase
                .findElementInRange(file, start, end, GoPsiElementBase.class, GoFileType.GO_LANGUAGE);
        if (e == null) {
            throw new GoRefactoringException("It's not a valid expression!");
        }

        if (e.getParent() instanceof GoPsiElementBase) {
            // If there is a pair of parenthesis enclosed the expression, include the parenthesis.
            GoPsiElementBase parent = (GoPsiElementBase) e.getParent();
            if (parent.getTokenType() == GoElementTypes.EXPRESSION_PARENTHESIZED) {
                e = parent;
                start = e.getTextOffset();
                end = start + e.getTextLength();
            }
        }

        // Remove redundant parenthesis around declaration.
        boolean needToRemoveParenthesis = e.getTokenType() == GoElementTypes.EXPRESSION_PARENTHESIZED;

        PsiDocumentManager manager = PsiDocumentManager.getInstance(project);
        Document document = manager.getDocument(file);
        if (document == null) {
            return;
        }

        String variable = "VALUE";
        String declaration = e.getText().trim();
        if (needToRemoveParenthesis) {
            declaration = declaration.substring(1, declaration.length() - 1);
        }

        editor.getCaretModel().moveToOffset(end);
        // replace expression with const
        document.replaceString(start, end, variable);

        GoConstDeclarations[] allConstDeclarations = file.getConsts();
        if (allConstDeclarations.length > 0) {
            GoConstDeclarations declarations = allConstDeclarations[allConstDeclarations.length - 1];
            appendConstToLastDeclaration(document, start, variable, declaration, declarations);
        } else {
            appendConstToLastImportOrPackage(document, file, variable, declaration);
        }

        // TODO: trigger an in-place constant rename.
    }

    private void appendConstToLastImportOrPackage(Document document, GoFile file, String variable, String declaration) {
        GoPsiElement lastElement;
        GoImportDeclarations[] imports = file.getImportDeclarations();
        if (imports.length != 0) {
            lastElement = imports[imports.length - 1];
        } else {
            lastElement = file.getPackage();
        }

        String sb = "\n\nconst " + variable + " = " + declaration;
        document.insertString(lastElement.getTextOffset() + lastElement.getTextLength(), sb);
    }

    private void appendConstToLastDeclaration(Document document, int start, String variable, String declaration, GoConstDeclarations declarations) {
        GoConstDeclaration[] consts = declarations.getDeclarations();
        if (consts.length == 1 && !isEnclosedByParenthesis(consts[0])) {
            GoConstDeclaration lastConst = consts[0];
            int offset = lastConst.getTextOffset();
            StringBuilder sb = new StringBuilder("(\n");
            sb.append("    ").append(lastConst.getText()).append("\n");
            sb.append("    ").append(variable).append(" = ").append(declaration).append("\n");
            sb.append(")");
            document.replaceString(offset, offset + lastConst.getTextLength(), sb.toString());
            return;
        }

        GoConstDeclaration lastConst = consts[consts.length - 1];
        int lineStart = document.getLineStartOffset(document.getLineNumber(start));
        String indent = findIndent(document.getText(new TextRange(lineStart, start)));
        int offset = lastConst.getTextOffset() + lastConst.getTextLength();
        document.insertString(offset, "\n" + indent + variable + " = " + declaration);
    }
}
