package ro.redeul.google.go.inspection.fix;

import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateEditingAdapter;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.editor.TemplateUtil;
import ro.redeul.google.go.lang.psi.statements.GoStatement;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;

public class CreateLocalVariableFix extends LocalQuickFixAndIntentionActionOnPsiElement {
    private static final String VARIABLE = "____INTRODUCE_VARIABLE____";

    public CreateLocalVariableFix(@Nullable PsiElement element) {
        super(element);
    }

    @NotNull
    @Override
    public String getText() {
        return "Create local variable \"" + getStartElement().getText() + "\"";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Variable Declaration";
    }

    @Override
    public void invoke(@NotNull final Project project, @NotNull final PsiFile file,
                       @Nullable("is null when called from inspection") final Editor editor,
                       @NotNull final PsiElement startElement, @NotNull PsiElement endElement) {
        GoStatement statement = findParentOfType(startElement, GoStatement.class);
        if (statement == null || editor == null) {
            return;
        }

        final Document doc = PsiDocumentManager.getInstance(project).getDocument(file);

        if (doc == null) {
            return;
        }

        final RangeMarker rangeMarker;
        final int line = doc.getLineNumber(statement.getTextOffset());
        String variableName = startElement.getText();

        if (expressionIsTheWholeStatement(startElement, statement)) {
            doc.deleteString(doc.getLineStartOffset(line), doc.getLineEndOffset(line) + 1);
            rangeMarker = null;
        } else {
            rangeMarker = doc.createRangeMarker(startElement.getTextRange());
        }

        editor.getCaretModel().moveToOffset(doc.getLineStartOffset(line));

        TemplateImpl template = createTemplate(variableName);
        TemplateManager.getInstance(project).startTemplate(editor, template, new TemplateEditingAdapter() {
            @Override
            public void templateFinished(Template template, boolean brokenOff) {
                int offset;
                if (rangeMarker != null) {
                    offset = rangeMarker.getEndOffset();
                } else {
                    offset = doc.getLineEndOffset(line);
                }
                editor.getCaretModel().moveToOffset(offset);
            }
        });
    }

    private TemplateImpl createTemplate(String variableName) {
        String decl = String.format("%s := $%s$\n", variableName, VARIABLE);
        TemplateImpl template = TemplateUtil.createTemplate(decl);
        template.setToIndent(true);
        template.setToReformat(true);
        template.addVariable(VARIABLE, "\"value\"", "\"value\"", true);
        return template;
    }

    private static boolean expressionIsTheWholeStatement(PsiElement element, GoStatement stmt) {
        return element.getTextRange().equals(stmt.getTextRange());
    }
}
