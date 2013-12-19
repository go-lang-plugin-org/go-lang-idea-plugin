package ro.redeul.google.go.template.macro;

import com.intellij.codeInsight.template.Expression;
import com.intellij.codeInsight.template.ExpressionContext;
import com.intellij.codeInsight.template.Result;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;

class MacroUtil {
    public static <T extends GoPsiElement> T findElementOfType(ExpressionContext context, Class<T> clz) {
        Project project = context.getProject();
        if (context.getEditor() == null) {
            return null;
        }

        PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(context.getEditor().getDocument());
        if (file == null) {
            return null;
        }

        int templateStartOffset = context.getTemplateStartOffset();
        int offset = templateStartOffset > 0 ? context.getTemplateStartOffset() - 1 : context.getTemplateStartOffset();
        return findParentOfType(file.findElementAt(offset), clz);
    }

    public static String readFirstParamValue(ExpressionContext context, Expression[] params, String defaultValue) {
        String value = null;
        if (params.length > 0) {
            Result result = params[0].calculateResult(context);
            if (result != null) {
                value = result.toString();
            }
        }

        return value == null ? defaultValue : value;
    }
}
