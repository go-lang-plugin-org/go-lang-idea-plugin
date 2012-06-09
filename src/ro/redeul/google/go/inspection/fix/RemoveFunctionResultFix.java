package ro.redeul.google.go.inspection.fix;

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
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

        PsiElement result = findChildOfType(startElement, GoElementTypes.FUNCTION_RESULT);
        if (result != null) {
            result.delete();
        }
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
