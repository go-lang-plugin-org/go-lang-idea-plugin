package ro.redeul.google.go.formatter;

import com.ansorgit.plugins.bash.editor.formatting.noOpModel.NoOpBlock;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.formatting.FormattingModel;
import com.intellij.formatting.FormattingModelBuilder;
import com.intellij.formatting.FormattingModelProvider;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.sdk.GoSdkUtil;

/**
 * User: jhonny
 * Date: 05/07/11
 */
public class GoFmt implements FormattingModelBuilder {
    @NotNull
    @Override
    public FormattingModel createModel(PsiElement element, CodeStyleSettings settings) {
        PsiFile containingFile = element.getContainingFile();

        try {
            // try to format using go's default formatter
            if (CommandProcessor.getInstance().getCurrentCommandName().equals("Reformat Code")) {

                String filePath = containingFile.getVirtualFile().getPath();
                Sdk sdk = GoSdkUtil.getGoogleGoSdkForFile(containingFile);

                if (sdk == null) {
                    Messages.showMessageDialog(
                            "Please configure the GO SDK!",
                            "Error formatting code",
                            Messages.getInformationIcon()
                    );
                } else {

                    GeneralCommandLine command = new GeneralCommandLine();

                    String goFmtPath = ((GoSdkData) sdk.getSdkAdditionalData()).BINARY_PATH + "/gofmt";

                    command.setExePath(goFmtPath);
                    command.addParameter("-w");
                    command.addParameter(filePath);

                    command.createProcess();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        ASTNode astNode = containingFile.getNode();
        // return a block that does nothing
        return FormattingModelProvider.createFormattingModelForPsiFile(containingFile,
                new NoOpBlock(astNode), settings);
    }

    @Override
    public TextRange getRangeAffectingIndent(PsiFile psiFile, int i, ASTNode astNode) {
        return null;
    }
}
