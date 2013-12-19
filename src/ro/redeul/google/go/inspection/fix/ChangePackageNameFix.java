package ro.redeul.google.go.inspection.fix;

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.psi.toplevel.GoPackageDeclaration;

public class ChangePackageNameFix extends
                                  LocalQuickFixAndIntentionActionOnPsiElement {

    private final String targetPackage;

    public ChangePackageNameFix(GoPackageDeclaration packageDeclaration, String targetPackage) {
        super(packageDeclaration);
        this.targetPackage = targetPackage;
    }

    @Override
    @NotNull
    public String getText() {
        return GoBundle.message("update.package.name.fix", targetPackage);
    }

    @Override
    @NotNull
    public String getFamilyName() {
        return GoBundle.message("repackage.file.fix.family");
    }

    @Override
    public void invoke(@NotNull final Project project, @NotNull final PsiFile file,
                       @Nullable(
                           "is null when called from inspection") final Editor editor,
                       @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        if (!(startElement instanceof GoPackageDeclaration))
            return;

        final GoPackageDeclaration packageDeclaration = (GoPackageDeclaration) startElement;


        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                Document doc =
                    PsiDocumentManager.getInstance(project).getDocument(file);

                if (doc == null) {
                    return;
                }

                TextRange textRange = packageDeclaration.getTextRange();

                doc.replaceString(textRange.getStartOffset(),
                                  textRange.getEndOffset(),
                                  "package " + targetPackage);
            }
        });
    }
}