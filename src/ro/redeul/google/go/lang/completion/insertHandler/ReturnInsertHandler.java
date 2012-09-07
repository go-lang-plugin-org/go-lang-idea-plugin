package ro.redeul.google.go.lang.completion.insertHandler;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;

import static ro.redeul.google.go.editor.TemplateUtil.createTemplate;
import static ro.redeul.google.go.editor.TemplateUtil.getTemplateVariableExpression;
import static ro.redeul.google.go.inspection.InspectionUtil.getFunctionResultCount;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;

public class ReturnInsertHandler implements InsertHandler<LookupElement> {
    @Override
    public void handleInsert(InsertionContext context, LookupElement item) {
        int offset = context.getTailOffset();
        PsiElement element = context.getFile().findElementAt(offset);
        GoFunctionDeclaration function = findParentOfType(element, GoFunctionDeclaration.class);
        if (function == null) {
            return;
        }

        int count = getFunctionResultCount(function);
        if (count == 0) {
            return;
        }

        TemplateImpl template = createTemplate(" " + getTemplateVariableExpression(count, ", "));
        for (int i = 0; i < count; i++) {
            String defaultValue = String.format("\"v%d\"", i);
            template.addVariable("v" + i, defaultValue, defaultValue, true);
        }

        Editor editor = context.getEditor();
        Project project = context.getProject();
        TemplateManager.getInstance(project).startTemplate(editor, "", template);
    }
}
