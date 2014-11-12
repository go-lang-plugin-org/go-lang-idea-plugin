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
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeName;
import ro.redeul.google.go.lang.psi.typing.GoTypeInterface;
import ro.redeul.google.go.lang.psi.typing.GoTypes;

import static ro.redeul.google.go.util.EditorUtil.reformatLines;

public class CastTypeFix extends LocalQuickFixAndIntentionActionOnPsiElement {

    private final GoType type;
    private final String typeString;

    public CastTypeFix(@NotNull GoExpr element, GoType type) {
        super(element);
        this.type = type;
        this.typeString = GoTypes.getRepresentation(type, (GoFile) element.getContainingFile());
    }

    @Override
    public GoExpr getStartElement() {
        return (GoExpr) super.getStartElement();
    }

    @NotNull
    @Override
    public String getText() {
        return "Cast (" + getStartElement().getText() + ") to " + typeString;
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Function Calling";
    }

    @Override
    public void invoke(@NotNull final Project project,
                       @NotNull PsiFile psiFile,
                       @Nullable("is null when called from inspection") Editor editor,
                       @NotNull final PsiElement startElement, @NotNull PsiElement endElement) {

        Document doc = PsiDocumentManager.getInstance(project).getDocument(psiFile);

        if (doc == null || !(startElement instanceof GoExpr) || !(psiFile instanceof GoFile))
            return;

        TextRange textRange = startElement.getTextRange();

        GoFile file = (GoFile) psiFile;

        GoType[] expressionType = getStartElement().getType();

        String typeDescription = GoTypes.getRepresentation(type, file);

        if (!(type instanceof GoTypeName))
            typeDescription = "(" + typeDescription + ")";

        String castString = null;
        if (expressionType.length != 0) {
            if (expressionType[0] instanceof GoTypeInterface) {
                castString = String.format("%s.(%s)", startElement.getText(), typeDescription);
            }
        }

        if (castString == null)
            castString = String.format("%s(%s)", typeDescription, startElement.getText());

        doc.replaceString(textRange.getStartOffset(), textRange.getEndOffset(), castString);
        if (editor != null) {
            int line = doc.getLineNumber(textRange.getStartOffset());
            reformatLines(file, editor, line, line);
            //reformatPositions(element);
        }
    }
}
