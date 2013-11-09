package ro.redeul.google.go.refactoring.introduce;

import com.intellij.openapi.actionSystem.DataContext;
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
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.refactoring.GoRefactoringException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.getPrevSiblingIfItsWhiteSpaceOrComment;

abstract class GoIntroduceHandlerBase implements RefactoringActionHandler {
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
            PsiElement element = getPrevSiblingIfItsWhiteSpaceOrComment(file.findElementAt(offset));
            if (element != null) {
                offset = Math.min(offset, element.getTextRange().getEndOffset() - 1);
            }
            List<GoExpr> expressions = collectExpressions(file, offset);
            if (expressions.isEmpty()) {
                return;
            }

            if (expressions.size() == 1) {
                TextRange range = expressions.get(0).getNode().getTextRange();
                introduce(project, editor, file, range.getStartOffset(), range.getEndOffset());
                return;
            }

            // If there are multiple potential expressions, let user select one.
            IntroduceTargetChooser.showChooser(editor, expressions, new Pass<GoExpr>() {
                        @Override
                        public void pass(GoExpr expr) {
                            TextRange range = expr.getNode().getTextRange();
                            introduce(project, editor, file, range.getStartOffset(), range.getEndOffset());
                        }
                    }, new Function<GoExpr, String>() {
                        @Override
                        public String fun(GoExpr expr) {
                            return expr.getText();
                        }
                    }
            );
        } catch (GoRefactoringException e) {
            CommonRefactoringUtil.showErrorHint(project, editor, e.getMessage(), "Refactoring error!", null);
        }
    }

    private void introduce(final Project project, final Editor editor, final GoFile file, final int start, final int end) {
        try {
            doIntroduce(project, editor, file, start, end);
        } catch (GoRefactoringException e) {
            CommonRefactoringUtil.showErrorHint(project, editor, e.getMessage(), "Refactoring error!", null);
        }
    }

    protected abstract void doIntroduce(Project project, Editor editor, GoFile file, int start, int end) throws GoRefactoringException;

    boolean isExpressionValid(GoExpr expression) {
        return expression != null;
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiElement[] elements, DataContext dataContext) {
        // invoked from elsewhere (other from editor). do nothing.
    }

    List<GoExpr> collectExpressions(PsiFile file, int offset) {
        Set<TextRange> expressionRanges = new HashSet<>();
        List<GoExpr> expressions = new ArrayList<>();
        for (GoExpr expression = PsiTreeUtil.findElementOfClassAtOffset(file, offset, GoExpr.class, false);
             expression != null;
             expression = PsiTreeUtil.getParentOfType(expression, GoExpr.class)) {
            IElementType tt = expression.getNode().getElementType();
            if (tt == GoElementTypes.PARENTHESISED_EXPRESSION ||
                expressionRanges.contains(expression.getTextRange())) {
                continue;
            }

            if (!isExpressionValid(expression)) {
                break;
            }

            expressions.add(expression);
            expressionRanges.add(expression.getTextRange());
        }
        return expressions;
    }
}
