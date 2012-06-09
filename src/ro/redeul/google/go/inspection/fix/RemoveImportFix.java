package ro.redeul.google.go.inspection.fix;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNodeOfType;

public class RemoveImportFix extends LocalQuickFixAndIntentionActionOnPsiElement {
    public RemoveImportFix(@Nullable PsiElement element) {
        super(element);
    }

    @NotNull
    @Override
    public String getText() {
        return "Remove unused import";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Import";
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file,
                       @Nullable("is null when called from inspection") Editor editor,
                       @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        if (!(startElement instanceof GoImportDeclaration)) {
            return;
        }

        GoImportDeclaration declaration = (GoImportDeclaration) startElement;
        GoImportDeclarations declarations = (GoImportDeclarations) declaration.getParent();

        PsiElement elementToDelete;
        if (declarations.getDeclarations().length == 1) {
            elementToDelete = declarations;
        } else {
            elementToDelete = declaration;
        }

        PsiElement prev = elementToDelete.getPrevSibling();
        if (prev instanceof PsiWhiteSpace) {
            prev.delete();
        }

        PsiElement next = elementToDelete.getNextSibling();
        if (next != null && isNodeOfType(next, GoTokenTypes.wsNLS)) {
            next.delete();
        }

        elementToDelete.delete();
    }
}
