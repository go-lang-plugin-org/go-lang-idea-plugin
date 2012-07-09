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
import static ro.redeul.google.go.editor.TemplateUtil.getTemplateVariableExpression;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNewLineNode;

public class AddMissingExpressionFix implements LocalQuickFix {
    @NotNull
    @Override
    public String getName() {
        return "Add missing expression";
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

        GoConstDeclaration cd = (GoConstDeclaration) element;
        if (!cd.hasInitializers()) {
            addConstInitializer(project, editor, cd);
        } else {
            addMissingExpression(project, editor, cd);
        }
    }

    private void addMissingExpression(Project project, Editor editor, GoConstDeclaration cd) {
        GoLiteralIdentifier[] ids = cd.getIdentifiers();
        GoExpr[] expressions = cd.getExpressions();
        StringBuilder sb = new StringBuilder();
        for (int i = expressions.length; i < ids.length; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append("$v").append(i).append("$");
        }
        TemplateImpl template = createTemplate(sb.toString());
        for (int i = expressions.length; i < ids.length; i++) {
            template.addVariable("v" + i, "\"value\"", "\"value\"", true);
        }

        editor.getCaretModel().moveToOffset(getConstEndOffset(cd));
        TemplateManager.getInstance(project).startTemplate(editor, "", template);
    }

    private void addConstInitializer(Project project, Editor editor, GoConstDeclaration cd) {
        int length = cd.getIdentifiers().length;
        String text = " = " + getTemplateVariableExpression(length, ", ");
        TemplateImpl template = createTemplate(text);
        template.setToIndent(false);
        for (int i = 0; i < length; i++) {
            template.addVariable("v" + i, "\"value\"", "\"value\"", true);
        }
        editor.getCaretModel().moveToOffset(getConstEndOffset(cd));
        TemplateManager.getInstance(project).startTemplate(editor, "", template);
    }

    private static int getConstEndOffset(GoConstDeclaration cd) {
        PsiElement child = cd.getLastChild();
        while (child != null && isNewLineNode(child)) {
            child = child.getPrevSibling();
        }

        return child == null ? cd.getTextRange().getEndOffset() : child.getTextRange().getEndOffset();
    }
}
