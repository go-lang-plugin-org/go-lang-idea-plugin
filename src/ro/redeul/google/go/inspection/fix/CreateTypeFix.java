package ro.redeul.google.go.inspection.fix;

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.psi.GoFile;

public class CreateTypeFix extends LocalQuickFixAndIntentionActionOnPsiElement {
    public CreateTypeFix(@Nullable PsiElement element) {
        super(element);
    }

    @Override
    public void invoke(@NotNull Project project,
                       @NotNull PsiFile file,
                       @Nullable("is null when called from inspection") Editor editor,
                       @NotNull PsiElement startElement,
                       @NotNull PsiElement endElement) {
        PsiElement insertPoint = startElement;
        while (insertPoint != null && !(insertPoint.getParent() instanceof GoFile)) {
            insertPoint = insertPoint.getParent();
        }
        if (insertPoint == null) {
            return;
        }

        Document doc = PsiDocumentManager.getInstance(project).getDocument(file);
        if (doc == null) {
            return;
        }

        int offset = insertPoint.getTextOffset();
        int line = doc.getLineNumber(offset);
        doc.insertString(offset, "type " + startElement.getText() + " \n\n");
        if (editor != null) {
            editor.getCaretModel().moveToOffset(doc.getLineEndOffset(line));
        }
    }

    @NotNull
    @Override
    public String getText() {
        return GoBundle.message("create.type", getStartElement().getText());
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return GoBundle.message("type.declaration");
    }
}
