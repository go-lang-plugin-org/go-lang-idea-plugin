package ro.redeul.google.go.formatter;

import com.ansorgit.plugins.bash.editor.formatting.noOpModel.NoOpBlock;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.formatting.CustomFormattingModelBuilder;
import com.intellij.formatting.FormattingModel;
import com.intellij.formatting.FormattingModelBuilder;
import com.intellij.formatting.FormattingModelProvider;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.sdk.GoSdkTool;
import ro.redeul.google.go.sdk.GoSdkUtil;

/**
 * User: jhonny
 * Date: 05/07/11
 */
public class GoFmt implements FormattingModelBuilder {

    @NotNull
    @Override
    public FormattingModel createModel(PsiElement element, CodeStyleSettings settings) {

        FileDocumentManager documentManager = FileDocumentManager.getInstance();

        PsiFile containingFile = element.getContainingFile();
        final VirtualFile file = containingFile.getVirtualFile();

        // try to format using go's default formatter
        try {
            if (file != null) {
                Document document = containingFile.getViewProvider().getDocument();

                if ( document != null ) {
                    PsiDocumentManager.getInstance(element.getProject()).commitDocument(document);
                }

                String filePath = file.getPath();
                Sdk sdk = GoSdkUtil.getGoogleGoSdkForFile(containingFile);

                GeneralCommandLine command = new GeneralCommandLine();
                GoSdkData sdkData = (GoSdkData) sdk.getSdkAdditionalData();

                if (sdkData != null) {
                    String goFmtPath = sdkData.BINARY_PATH + "/" + GoSdkUtil.getToolName(sdkData.TARGET_OS, sdkData.TARGET_ARCH, GoSdkTool.GoFmt);

                    command.setExePath(goFmtPath);
                    command.addParameter("-w");
                    command.addParameter(filePath);

                    Process process = command.createProcess();
                    process.waitFor();

                    if ( document != null ) {
                        file.refresh(false, true);
                        documentManager.reloadFromDisk(document);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // return a block that does nothing

        ASTNode astNode = containingFile.getNode();
        return FormattingModelProvider.createFormattingModelForPsiFile(containingFile, new NoOpBlock(astNode), settings);
    }

    @Override
    public TextRange getRangeAffectingIndent(PsiFile psiFile, int i, ASTNode astNode) {
        return null;
    }

//    @Override
//    public boolean isEngagedToFormat(PsiElement context) {
//        System.out.println(context.toString());
//        return true;
//    }

}
