package ro.redeul.google.go.refactoring.introduce;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.AccessToken;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pass;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.IntroduceTargetChooser;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.util.Function;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.refactoring.GoRefactoringException;

import java.util.ArrayList;
import java.util.List;

public abstract class GoIntroduceHandlerBase implements RefactoringActionHandler {
    @Override
    public void invoke(@NotNull final Project project, final Editor editor, PsiFile psiFile, DataContext dataContext) {
        try {
            if (!CommonRefactoringUtil.checkReadOnlyStatus(project, psiFile)) {
                throw new GoRefactoringException("It's a readonly file!");
            }

            if (!(psiFile instanceof GoFile)) {
                throw new GoRefactoringException("Only go file could be handled.");
            }

            final GoFile file = (GoFile) psiFile;

            PsiDocumentManager.getInstance(project).commitAllDocuments();

            final SelectionModel sm = editor.getSelectionModel();
            if (sm.hasSelection()) {
                introduce(project, editor, file, sm.getSelectionStart(), sm.getSelectionEnd());
                return;
            }

            // If nothing is selected in editor, find all potential expressions.
            int offset = editor.getCaretModel().getOffset();
            List<GoPsiElementBase> expressions = collectExpressions(file, editor, offset);
            if (expressions.isEmpty()) {
                return;
            }

            if (expressions.size() == 1) {
                TextRange range = expressions.get(0).getNode().getTextRange();
                introduce(project, editor, file, range.getStartOffset(), range.getEndOffset());
                return;
            }

            // If there are multiple potential expressions, let user select one.
            IntroduceTargetChooser.showChooser(editor, expressions, new Pass<GoPsiElementBase>() {
                        @Override
                        public void pass(GoPsiElementBase expr) {
                            TextRange range = expr.getNode().getTextRange();
                            introduce(project, editor, file, range.getStartOffset(), range.getEndOffset());
                        }
                    }, new Function<GoPsiElementBase, String>() {
                        @Override
                        public String fun(GoPsiElementBase goExpressionBase) {
                            return goExpressionBase.getText();
                        }
                    }
            );
        } catch (GoRefactoringException e) {
            CommonRefactoringUtil.showErrorHint(project, editor, e.getMessage(), "Refactoring error!", null);
        }
    }

    private void introduce(final Project project, final Editor editor, final GoFile file, final int start, final int end) {
        CommandProcessor.getInstance().executeCommand(project, new Runnable() {
            public void run() {
                AccessToken accessToken = WriteAction.start();
                try {
                    doIntroduce(project, editor, file, start, end);
                } catch (GoRefactoringException e) {
                    CommonRefactoringUtil.showErrorHint(project, editor, e.getMessage(), "Refactoring error!", null);
                } finally {
                    accessToken.finish();
                }
            }
        }, "Introduce", null);
    }

    protected abstract void doIntroduce(Project project, Editor editor, GoFile file, int start, int end) throws GoRefactoringException;

    protected boolean isExpressionValid(GoPsiElementBase expression) {
        IElementType tt = expression.getTokenType();
        return tt != GoElementTypes.CALL_OR_CONVERSION_EXPRESSION &&
               tt != GoElementTypes.BUILTIN_CALL_EXPRESSION &&
               tt != GoElementTypes.BLOCK_STATEMENT;
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiElement[] elements, DataContext dataContext) {
        // invoked from elsewhere (other from editor). do nothing.
    }

    protected List<GoPsiElementBase> collectExpressions(PsiFile file, Editor editor, int offset) {
        final PsiElement elementAtCaret = file.findElementAt(offset);
        if (elementAtCaret == null) {
            return new ArrayList<GoPsiElementBase>();
        }

        List<GoPsiElementBase> expressions = new ArrayList<GoPsiElementBase>();
        for (GoPsiElementBase expression = PsiTreeUtil.getParentOfType(elementAtCaret, GoPsiElementBase.class);
             expression != null;
             expression = PsiTreeUtil.getParentOfType(expression, GoPsiElementBase.class)) {
            IElementType tt = expression.getTokenType();
            if (tt == GoElementTypes.EXPRESSION_PARENTHESIZED) {
                continue;
            }

            if (!isExpressionValid(expression)) {
                break;
            }

            expressions.add(expression);
        }
        return expressions;
    }

    protected static String findIndent(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != ' ' && s.charAt(i) != '\t') {
                return s.substring(0, i);
            }
        }
        return s;
    }
}
