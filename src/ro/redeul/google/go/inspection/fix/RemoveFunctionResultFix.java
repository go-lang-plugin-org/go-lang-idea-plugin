package ro.redeul.google.go.inspection.fix;

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findChildOfType;

public class RemoveFunctionResultFix extends LocalQuickFixAndIntentionActionOnPsiElement {
    public RemoveFunctionResultFix(@Nullable PsiElement element) {
        super(element);
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file,
                       @Nullable("is null when called from inspection") Editor editor,
                       @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        if (!(file instanceof GoFile)) {
            return;
        }

        final PsiElement result = findChildOfType(startElement, GoElementTypes.FUNCTION_RESULT);
        if (result == null) {
            return;
        }

        WriteCommandAction writeCommandAction = new WriteCommandAction(project) {
            @Override
            protected void run(@NotNull Result res) throws Throwable {
                result.delete();
            }
        };
        writeCommandAction.execute();
    }

    @NotNull
    @Override
    public String getText() {
        return "Remove function result";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Function Declaration";
    }
}
