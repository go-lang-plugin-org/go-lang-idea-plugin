package ro.redeul.google.go.formatter;

import com.ansorgit.plugins.bash.editor.formatting.noOpModel.NoOpBlock;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.formatting.FormattingModel;
import com.intellij.formatting.FormattingModelBuilder;
import com.intellij.formatting.FormattingModelProvider;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.sdk.GoSdkTool;
import ro.redeul.google.go.sdk.GoSdkUtil;

import javax.swing.*;
import java.awt.*;

/**
 * User: jhonny
 * Date: 05/07/11
 */
public class GoFmt implements FormattingModelBuilder {

    @NotNull
    @Override
    public FormattingModel createModel(PsiElement element, CodeStyleSettings settings) {

        PsiFile containingFile = element.getContainingFile();

        String currentCommandName = CommandProcessor.getInstance().getCurrentCommandName();

        if (currentCommandName != null && currentCommandName.equals("Reformat Code")) {
            formatDocument(containingFile);
        }

        ASTNode astNode = containingFile.getNode();

        // return a block that does nothing
        return FormattingModelProvider.createFormattingModelForPsiFile(containingFile, new NoOpBlock(astNode), settings);
    }

    private void formatDocument(final PsiFile psiFile) {

        Sdk sdk = GoSdkUtil.getGoogleGoSdkForFile(psiFile);

        if (sdk == null) {
            showBalloon(psiFile.getProject(), "Error formatting code", "There is no Go SDK attached to module/project.", MessageType.ERROR);
            return;
        }

        final VirtualFile file = psiFile.getVirtualFile();

        if (file == null) {
            return;
        }
        // try to format using go's default formatter
        final Document fileDocument = psiFile.getViewProvider().getDocument();

        // commit file changes to disk
        if (fileDocument != null) {
            final FileDocumentManager documentManager = FileDocumentManager.getInstance();
            PsiDocumentManager.getInstance(psiFile.getProject()).commitDocument(fileDocument);

            documentManager.saveDocument(fileDocument);

            try {

                GeneralCommandLine command = new GeneralCommandLine();

                String goFmtPath = GoSdkUtil.getTool((GoSdkData) sdk.getSdkAdditionalData(), GoSdkTool.GoFmt);

                command.setExePath(goFmtPath);
                command.addParameter("-w");
                command.addParameter(file.getPath());

                Process process = command.createProcess();
                process.waitFor();

                file.refresh(false, true);
                documentManager.reloadFromDisk(fileDocument);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public TextRange getRangeAffectingIndent(PsiFile psiFile, int i, ASTNode astNode) {
        return null;
    }

    private static void showBalloon(Project project, String title, String message, MessageType messageType) {
        // ripped from com.intellij.openapi.vcs.changes.ui.ChangesViewBalloonProblemNotifier
        final JFrame frame = WindowManager.getInstance().getFrame(project.isDefault() ? null : project);
        if (frame == null) return;
        final JComponent component = frame.getRootPane();
        if (component == null) return;

        final Rectangle rect = component.getVisibleRect();
        final Point p = new Point(rect.x + rect.width - 10, rect.y + 10);
        final RelativePoint point = new RelativePoint(component, p);

        JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(
                String.format("<html><body><strong>%s</strong><br/><p>%s</p></body></html>", title, message),
                messageType.getDefaultIcon(), messageType.getPopupBackground(), null)
                .setShowCallout(false)
                .setFadeoutTime(2000)
                .setPreferredPosition(Balloon.Position.above)
                .setCloseButtonEnabled(true)
                .createBalloon().show(point, Balloon.Position.atLeft);
    }

}
