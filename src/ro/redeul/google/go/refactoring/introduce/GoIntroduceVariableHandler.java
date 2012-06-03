package ro.redeul.google.go.refactoring.introduce;

import com.intellij.codeInsight.CodeInsightUtilBase;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.AccessToken;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pass;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.IntroduceTargetChooser;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.util.Function;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.refactoring.GoRefactoringException;

import java.util.List;

public class GoIntroduceVariableHandler extends GoIntroduceHandlerBase {
    @Override
    public void doInvoke(@NotNull final Project project, final Editor editor, final PsiFile file, DataContext dataContext) throws GoRefactoringException {
        final SelectionModel sm = editor.getSelectionModel();
        if (sm.hasSelection()) {
            introduceVariable(project, editor, file, sm.getSelectionStart(), sm.getSelectionEnd());
            return;
        }

        // if nothing is selected in editor, find all potential expressions.
        int offset = editor.getCaretModel().getOffset();
        List<GoPsiElementBase> expressions = collectExpressions(file, editor, offset);
        if (expressions.isEmpty()) {
            return;
        }

        if (expressions.size() == 1) {
            TextRange range = expressions.get(0).getNode().getTextRange();
            introduceVariable(project, editor, file, range.getStartOffset(), range.getEndOffset());
            return;
        }

        // if there are multiple potential expressions, let user select one.
        IntroduceTargetChooser.showChooser(editor, expressions, new Pass<GoPsiElementBase>() {
                    @Override
                    public void pass(GoPsiElementBase expr) {
                        TextRange range = expr.getNode().getTextRange();
                        introduceVariable(project, editor, file, range.getStartOffset(), range.getEndOffset());
                    }
                }, new Function<GoPsiElementBase, String>() {
                    @Override
                    public String fun(GoPsiElementBase goExpressionBase) {
                        return goExpressionBase.getText();
                    }
                }
        );
    }

    private void introduceVariable(final Project project, final Editor editor, final PsiFile file, final int start, final int end) {
        CommandProcessor.getInstance().executeCommand(project, new Runnable() {
            public void run() {
                AccessToken accessToken = WriteAction.start();
                try {
                    doIntroduceVariable(project, editor, file, start, end);
                } catch (GoRefactoringException e) {
                    CommonRefactoringUtil.showErrorHint(project, editor, e.getMessage(), "Refactoring error!", null);
                } finally {
                    accessToken.finish();
                }
            }
        }, "Introduce variable", null);
    }

    private void doIntroduceVariable(Project project, Editor editor, PsiFile file, int start, int end) throws GoRefactoringException {
        GoPsiElementBase e = CodeInsightUtilBase.findElementInRange(file, start, end, GoPsiElementBase.class, GoFileType.GO_LANGUAGE);
        if (e == null) {
            throw new GoRefactoringException("It's not a valid expression!");
        }

        if (e.getParent() instanceof GoPsiElementBase) {
            GoPsiElementBase parent = (GoPsiElementBase) e.getParent();
            if (parent.getTokenType() == GoElementTypes.EXPRESSION_PARENTHESIZED) {
                e = parent;
                start = e.getTextOffset();
                end = start + e.getTextLength();
            }
        }

        // remove redundant parenthesis around declaration
        boolean needToRemoveParenthesis = e.getTokenType() == GoElementTypes.EXPRESSION_PARENTHESIZED;

        PsiDocumentManager manager = PsiDocumentManager.getInstance(project);
        Document document = manager.getDocument(file);
        if (document == null) {
            return;
        }

        String variable = "value";
        int lineStart = document.getLineStartOffset(document.getLineNumber(start));
        String declaration = e.getText().trim();
        if (needToRemoveParenthesis) {
            declaration = declaration.substring(1, declaration.length() - 1);
        }
        String indent = findIndent(document.getText(new TextRange(lineStart, start)));
        editor.getCaretModel().moveToOffset(end);
        // replace expression with variable
        document.replaceString(start, end, variable);

        // declare variable
        document.insertString(lineStart, indent + variable + " := " + declaration + "\n");

        // TODO: trigger an in-place variable rename.
    }

    private static String findIndent(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != ' ' && s.charAt(i) != '\t') {
                return s.substring(0, i);
            }
        }
        return s;
    }
}
