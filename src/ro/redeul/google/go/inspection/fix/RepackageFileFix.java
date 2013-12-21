package ro.redeul.google.go.inspection.fix;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ex.MessagesEx;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;

import java.io.IOException;

public class RepackageFileFix implements IntentionAction, LocalQuickFix {

    private final VirtualFile rootPath;
    private final String targetPackage;

    /**
     * @param rootPath the source root
     * @param targetPackage the desired target package
     */
    public RepackageFileFix(VirtualFile rootPath, String targetPackage) {
        this.rootPath = rootPath;
        this.targetPackage = targetPackage;
    }

    @Override
    @NotNull
    public String getText() {
        return GoBundle.message("repackage.file.fix", targetPackage);
    }

    @Override
    @NotNull
    public String getName() {
        return getText();
    }

    @Override
    @NotNull
    public String getFamilyName() {
        return GoBundle.message("repackage.file.fix.family");
    }

    @Override
    public void applyFix(@NotNull final Project project, @NotNull ProblemDescriptor descriptor) {
        final PsiFile file = descriptor.getPsiElement().getContainingFile();
        if (isAvailable(project, null, file)) {
            new WriteCommandAction(project) {
                @Override
                protected void run(Result result) throws Throwable {
                    invoke(project, FileEditorManager.getInstance(project).getSelectedTextEditor(), file);
                }
            }.execute();
        }
    }

    @Override
    public final boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        if (!rootPath.isValid() || !rootPath.isDirectory()) return false;

        if (!file.isValid()) return false;

        VirtualFile vFile = file.getVirtualFile();
        if (vFile == null) return false;

        final VirtualFile parent = vFile.getParent();
        return parent != null;
    }


    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) {
        VirtualFile vFile = file.getVirtualFile();
        Document document = PsiDocumentManager.getInstance(project).getDocument(file);
        if (document == null) {
            return;
        }

        FileDocumentManager.getInstance().saveDocument(document);
        try {
            VirtualFile targetFolder = VfsUtil.createDirectoryIfMissing(rootPath, targetPackage);
            if (targetFolder != null)
                vFile.move(this, targetFolder);
        }
        catch(IOException e){
            MessagesEx.error(project, e.getMessage()).showLater();
        }
        if (editor != null) {
            DaemonCodeAnalyzer.getInstance(project).restart(file);
        }
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}