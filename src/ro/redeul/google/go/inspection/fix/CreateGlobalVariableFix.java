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
import ro.redeul.google.go.lang.documentation.DocumentUtil;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclarations;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;
import ro.redeul.google.go.lang.psi.toplevel.GoPackageDeclaration;

public class CreateGlobalVariableFix extends LocalQuickFixAndIntentionActionOnPsiElement {
    private static final String VARIABLE = "____INTRODUCE_VARIABLE____";

    public CreateGlobalVariableFix(@Nullable PsiElement element) {
        super(element);
    }

    @NotNull
    @Override
    public String getText() {
        return "Create global variable \"" + getStartElement().getText() + "\"";
    }
    @NotNull
    @Override
    public String getFamilyName() {
        return "Variable Declaration";
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file,
                       @Nullable("is null when called from inspection") final Editor editor, @NotNull PsiElement startElement,
                       @NotNull PsiElement endElement) {
        if (!(file instanceof GoFile) || editor == null) {
            return;
        }

        Document doc = PsiDocumentManager.getInstance(project).getDocument(file);

        if (doc == null) {
            return;
        }

        final RangeMarker rangeMarker = doc.createRangeMarker(startElement.getTextRange());

        GoFile goFile = (GoFile) file;
        Template template;
        GoVarDeclarations[] globalVariables = goFile.getGlobalVariables();
        if (globalVariables.length == 0) {
            template = createNewStatementTemplate(editor, goFile, startElement);
        } else {
            GoVarDeclarations lastVar = globalVariables[globalVariables.length - 1];
            template = createAppendGlobalVariableTemplate(editor, lastVar, startElement);
        }

        if (template != null) {
            TemplateManager.getInstance(project).startTemplate(editor, template, new TemplateEditingAdapter() {
                @Override
                public void templateFinished(Template template, boolean brokenOff) {
                    editor.getCaretModel().moveToOffset(rangeMarker.getEndOffset());
                }
            });
        }
    }

    private static Template createAppendGlobalVariableTemplate(Editor editor, GoVarDeclarations lastVar,
                                                               PsiElement element) {
        PsiElement lastChild = lastVar.getLastChild();
        // If it's a parenthesised var declaration, append new variable at the end of the list
        if (isRightParenthesis(lastChild)) {
            String variableName = element.getText();
            String decl = String.format("%s = $%s$\n", variableName, VARIABLE);
            return createTemplateAtPosition(editor, lastChild.getTextOffset(), decl);
        }

        GoVarDeclaration[] declarations = lastVar.getDeclarations();
        if (declarations.length != 1) {
            // It shouldn't happen. If there is no parentheses, there should be exactly one declaration.
            return createNewStatementTemplate(editor, (GoFile) lastVar.getContainingFile(), element);
        }

        String variableName = element.getText();
        String decl = String.format("\nvar (\n%s\n%s = $%s$\n)\n", declarations[0].getText(), variableName, VARIABLE);
        int position = lastVar.getTextOffset();
        Project project = lastVar.getProject();
        PsiFile file = lastVar.getContainingFile();
        Document doc = PsiDocumentManager.getInstance(project).getDocument(file);
        DocumentUtil.replaceElementWithText(doc, lastVar, "");
        return createTemplateAtPosition(editor, position, decl);
    }

    private static boolean isRightParenthesis(PsiElement e) {
        return e != null && ")".equals(e.getText());
    }

    private static Template createNewStatementTemplate(Editor editor, GoFile goFile, PsiElement element) {
        GoImportDeclarations[] imports = goFile.getImportDeclarations();
        int position = 0;
        // If import statements exist, put new variable statement under last import statement.
        // Otherwise, put new variable statement under package statement
        if (imports.length != 0) {
            position = imports[imports.length - 1].getTextRange().getEndOffset();
        } else {
            GoPackageDeclaration packageDeclaration = goFile.getPackage();
            if (packageDeclaration != null) {
                position = packageDeclaration.getTextRange().getEndOffset();
            }
        }

        String variableName = element.getText();
        String decl = String.format("\nvar (\n%s = $%s$\n)\n", variableName, VARIABLE);
        return createTemplateAtPosition(editor, position, decl);
    }

    private static Template createTemplateAtPosition(Editor editor, int position, String decl) {
        editor.getCaretModel().moveToOffset(position);
        TemplateImpl template = TemplateUtil.createTemplate(decl);
        template.setToIndent(true);
        template.setToReformat(true);
        template.addVariable(VARIABLE, "\"value\"", "\"value\"", true);
        return template;
    }
}
