package ro.redeul.google.go.inspection.fix.constDeclaration;

import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;

import static ro.redeul.google.go.editor.TemplateUtil.createTemplate;

public class AddMissingConstFix implements LocalQuickFix {
    @NotNull
    @Override
    public String getName() {
        return "Add missing constant";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Constant";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        PsiElement element = descriptor.getStartElement();
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor == null) {
            return;
        }

        if (!(element instanceof GoConstDeclaration)) {
            return;
        }

        addMissingConst(project, editor, (GoConstDeclaration) element);
    }

    private void addMissingConst(Project project, Editor editor, GoConstDeclaration cd) {
        GoLiteralIdentifier[] ids = cd.getIdentifiers();
        GoExpr[] expressions = cd.getExpressions();
        StringBuilder sb = new StringBuilder();

        if (ids.length == 0) {
            return;
        }

        for (int i = ids.length; i < expressions.length; i++) {
            sb.append(", $v").append(i).append("$");
        }
        TemplateImpl template = createTemplate(sb.toString());
        for (int i = ids.length; i < expressions.length; i++) {
            template.addVariable("v" + i, "\"C\"", "\"C\"", true);
        }

        editor.getCaretModel().moveToOffset(ids[ids.length - 1].getTextRange().getEndOffset());
        TemplateManager.getInstance(project).startTemplate(editor, "", template);
    }
}
