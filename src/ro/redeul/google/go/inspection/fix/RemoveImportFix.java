package ro.redeul.google.go.inspection.fix;

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;

import static ro.redeul.google.go.inspection.fix.FixUtil.removeWholeElement;
import static ro.redeul.google.go.lang.psi.GoPsiElementFactory.createGoFile;

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
        if (!(startElement instanceof GoImportDeclaration) || !(file instanceof GoFile)) {
            return;
        }

        GoImportDeclaration declaration = (GoImportDeclaration) startElement;
        final GoImportDeclarations declarations = (GoImportDeclarations) declaration.getParent();
        String removeImport = declaration.getText();
        if (removeImport == null) {
            return;
        }

        GoImportDeclaration[] da = declarations.getDeclarations();
        // if there are more than 2 imports, just remove current one.
        if (da.length > 2) {
            removeWholeElement(declaration);
            return;
        }

        // if there are exactly 2 imports, replace the whole import to import "theOther". i.e. remove parenthesis.
        if (da.length == 2) {
            final PsiElement newImport = getNewImport(da, startElement.getText(), (GoFile) file);
            if (newImport != null) {
                WriteCommandAction writeCommandAction = new WriteCommandAction(file.getProject()) {
                    @Override
                    protected void run(@NotNull Result result) throws Throwable {
                        declarations.replace(newImport);
                    }
                };
                writeCommandAction.execute();
                return;
            }
        }

        // remove the whole declarations, if current one is the only one left.
        removeWholeElement(declarations);
    }

    private PsiElement getNewImport(GoImportDeclaration[] da, String importToRemove, GoFile file) {
        if (da[0] == null || da[1] == null) {
            return null;
        }

        String otherImport;
        if (da[0].getText().equals(importToRemove)) {
            otherImport = da[1].getText();
        } else if (da[1].getText().equals(importToRemove)) {
            otherImport = da[0].getText();
        } else {
            return null;
        }

        String script = "package main\nimport " + otherImport;
        GoFile newFile = createGoFile(file, script);
        if (newFile == null) {
            return null;
        }
        GoImportDeclarations[] imports = newFile.getImportDeclarations();
        if (imports == null || imports.length != 1) {
            return null;
        }

        return imports[0];
    }
}
