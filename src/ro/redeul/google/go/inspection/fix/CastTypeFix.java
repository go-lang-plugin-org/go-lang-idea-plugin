package ro.redeul.google.go.inspection.fix;

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.util.GoUtil;

import static ro.redeul.google.go.util.EditorUtil.reformatLines;

public class CastTypeFix extends LocalQuickFixAndIntentionActionOnPsiElement {

    @Nullable
    private PsiElement element;
    private final GoPsiType type;

    public CastTypeFix(@Nullable PsiElement element, GoPsiType type) {
        super(element);
        this.element = element;
        this.type = type;
    }


    @NotNull
    @Override
    public String getText() {
        return "Cast {" + getStartElement().getText() + "} to " + type.getText();
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Function Calling";
    }


    @Override
    public void invoke(@NotNull final Project project,
                       @NotNull PsiFile file,
                       @Nullable("is null when called from inspection") Editor editor,
                       @NotNull final PsiElement startElement, @NotNull PsiElement endElement) {
        final PsiElement e;

        Document doc = PsiDocumentManager.getInstance(project).getDocument(file);

        if (doc == null) {
            return;
        }

        TextRange textRange = element.getTextRange();
        GoFile currentFile = (GoFile) element.getContainingFile();
        String qualifiedName = GoUtil.getNameLocalOrGlobal(type, currentFile);

        String castString = String.format("(%s)(%s)", qualifiedName, element.getText());
        doc.replaceString(textRange.getStartOffset(), textRange.getEndOffset(), castString);
        if (editor != null) {
            int line = doc.getLineNumber(textRange.getStartOffset());
            reformatLines(file, editor, line, line);
            //reformatPositions(element);
        }
    }
}
