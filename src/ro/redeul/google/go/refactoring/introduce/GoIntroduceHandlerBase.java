package ro.redeul.google.go.refactoring.introduce;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.refactoring.GoRefactoringException;

import java.util.ArrayList;
import java.util.List;

public abstract class GoIntroduceHandlerBase implements RefactoringActionHandler {
    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file, DataContext dataContext) {
        try {
            if (!CommonRefactoringUtil.checkReadOnlyStatus(project, file)) {
                throw new GoRefactoringException("It's a readonly file!");
            }

            PsiDocumentManager.getInstance(project).commitAllDocuments();

            doInvoke(project, editor, file, dataContext);
        } catch (GoRefactoringException e) {
            CommonRefactoringUtil.showErrorHint(project, editor, e.getMessage(), "Refactoring error!", null);
        }
    }

    protected abstract void doInvoke(Project project, Editor editor, PsiFile file, DataContext dataContext) throws GoRefactoringException;

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiElement[] elements, DataContext dataContext) {
        // invoked from elsewhere (other from editor). do nothing.
    }

    protected static List<GoPsiElementBase> collectExpressions(PsiFile file, Editor editor, int offset) {
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
            if (tt == GoElementTypes.CALL_OR_CONVERSION_EXPRESSION ||
                    tt == GoElementTypes.BUILTIN_CALL_EXPRESSION ||
                    tt == GoElementTypes.BLOCK_STATEMENT) {
                break;
            }

            expressions.add(expression);
        }
        return expressions;
    }
}
