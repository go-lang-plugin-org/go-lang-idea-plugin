package ro.redeul.google.go.refactoring;

import com.intellij.lang.refactoring.InlineActionHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.RefactoringActionHandler;
import ro.redeul.google.go.GoEditorAwareTestCase;
import ro.redeul.google.go.intentions.Intention;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.refactoring.introduce.GoIntroduceHandlerBase;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;

public abstract class GoRefactoringTestCase extends GoEditorAwareTestCase {

    protected Object createHandler() {
        try {
            return Class.forName(getClass().getName().replaceAll("Test$", "")).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String getTestDataRelativePath() {
        String name = getClass().getSimpleName().replaceAll("Test$", "");
        return "refactoring/" + name.substring(0, 1).toLowerCase() + name.substring(1) + "/";
    }

    @Override
    protected void invoke(Project project, Editor editor, GoFile file) {
        Object handler = createHandler();
        if (handler instanceof RefactoringActionHandler) {
            ((RefactoringActionHandler) handler).invoke(getProject(), editor, file, null);
        } else if (handler instanceof InlineActionHandler) {
            PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
            element = findParentOfType(element, GoLiteralIdentifier.class);
            if (element == null) {
                throw new RuntimeException("Caret should on literal identifier");
            }
            ((InlineActionHandler) handler).inlineElement(project, editor, element);
        } else {
            throw new RuntimeException("Unknown handler");
        }
    }
}
