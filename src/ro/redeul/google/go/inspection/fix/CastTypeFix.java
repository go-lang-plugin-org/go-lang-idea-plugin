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
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeInterface;
import ro.redeul.google.go.util.GoUtil;

import static ro.redeul.google.go.util.EditorUtil.reformatLines;

public class CastTypeFix extends LocalQuickFixAndIntentionActionOnPsiElement {

    private final GoPsiType type;
    @Nullable
    private PsiElement element;

    public CastTypeFix(@Nullable PsiElement element, GoPsiType type) {
        super(element);
        this.element = element;
        this.type = type;
    }


    @NotNull
    @Override
    public String getText() {
        return "Cast (" + getStartElement().getText() + ") to " + type.getText();
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


        Document doc = PsiDocumentManager.getInstance(project).getDocument(file);

        if (doc == null || element == null) {
            return;
        }

        TextRange textRange = element.getTextRange();
        GoFile currentFile = (GoFile) element.getContainingFile();

        String castString = null;
        GoType[] type1 = ((GoExpr) element).getType();
        if (type1.length != 0) {
            if (type1[0] instanceof GoTypeInterface) {
                castString = String.format("%s.(%s)", element.getText(), GoUtil.getNameLocalOrGlobal(this.type, currentFile));
            }
        }
        if (castString == null)
            castString = String.format("(%s)(%s)", GoUtil.getNameLocalOrGlobal(this.type, currentFile), element.getText());
        doc.replaceString(textRange.getStartOffset(), textRange.getEndOffset(), castString);
        if (editor != null) {
            int line = doc.getLineNumber(textRange.getStartOffset());
            reformatLines(file, editor, line, line);
            //reformatPositions(element);
        }
    }
}
