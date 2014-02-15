package ro.redeul.google.go.inspection.fix;

import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.editor.TemplateUtil;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclarations;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;

import java.util.ArrayList;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findChildOfClass;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;

public class CreateClosureFunctionFix extends LocalQuickFixAndIntentionActionOnPsiElement {
    public CreateClosureFunctionFix(@Nullable PsiElement element) {
        super(element);
    }

    @NotNull
    @Override
    public String getText() {
        return "Create closure function \"" + getStartElement().getText() + "\"";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Variable Declaration";
    }

    @Override
    public void invoke(@NotNull Project project,
                       @NotNull final PsiFile file,
                       @Nullable("is null when called from inspection") final Editor editor,
                       @NotNull PsiElement startElement, @NotNull PsiElement endElement) {

        final PsiElement e = startElement;
        final PsiElement p = findChildOfClass(findParentOfType(e, GoFunctionDeclaration.class), GoBlockStatement.class);

        GoPsiElement childOfClass = findChildOfClass(p, GoVarDeclarations.class);
        if (childOfClass == null)
            childOfClass = findChildOfClass(p, GoVarDeclaration.class);
        if (childOfClass == null)
            childOfClass = findChildOfClass(p, GoShortVarDeclaration.class);

        ArrayList<String> arguments = new ArrayList<String>();
        final String fnArguments = CreateFunctionFix.InspectionGenFuncArgs(e, arguments);

        final int insertPoint;

        if (childOfClass == null) {
            insertPoint = p.getTextOffset() + 2;
        } else {
            insertPoint = childOfClass.getTextRange().getEndOffset();
        }


        Document doc = PsiDocumentManager.getInstance(e.getProject()).getDocument(file);

        if (doc == null) {
            return;
        }

        TemplateImpl template = TemplateUtil.createTemplate(String.format("\n\n%s := func (%s) { \n$v%d$$END$\n}", e.getText(), fnArguments, arguments.size()));
        arguments.add("//TODO: implements " + e.getText());
        TemplateUtil.runTemplate(editor, insertPoint, arguments, template);
    }
}
