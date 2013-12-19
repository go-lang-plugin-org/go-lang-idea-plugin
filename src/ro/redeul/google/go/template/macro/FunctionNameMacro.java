package ro.redeul.google.go.template.macro;

import com.intellij.codeInsight.template.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.template.GoTemplateContextType;
import ro.redeul.google.go.template.TemplateBundle;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;

public class FunctionNameMacro extends Macro {
    @Override
    public String getName() {
        return "functionName";
    }

    @Override
    public String getPresentableName() {
        return TemplateBundle.message("macro.functionName");
    }

    @Override
    public boolean isAcceptableInContext(TemplateContextType context) {
        return context instanceof GoTemplateContextType.Function;
    }

    @Override
    public Result calculateResult(@NotNull Expression[] params, ExpressionContext context) {
        Project project = context.getProject();
        if (context.getEditor() == null) {
            return new TextResult("");
        }

        PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(context.getEditor().getDocument());
        if (file == null) {
            return new TextResult("");
        }

        int templateStartOffset = context.getTemplateStartOffset();
        final int offset = templateStartOffset > 0 ? context.getTemplateStartOffset() - 1 : context.getTemplateStartOffset();

        GoFunctionDeclaration fd = findParentOfType(file.findElementAt(offset), GoFunctionDeclaration.class);
        String name = fd == null ? "" : fd.getFunctionName();
        return new TextResult(name == null ? "" : name);
    }
}
