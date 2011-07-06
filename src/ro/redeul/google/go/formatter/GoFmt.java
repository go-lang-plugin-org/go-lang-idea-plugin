package ro.redeul.google.go.formatter;

import com.ansorgit.plugins.bash.editor.formatting.noOpModel.NoOpBlock;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.formatting.FormattingModel;
import com.intellij.formatting.FormattingModelBuilder;
import com.intellij.formatting.FormattingModelProvider;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.compilation.CompilationTaskWorker;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.sdk.GoSdkUtil;

import java.util.HashMap;

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

            String filePath = containingFile.getVirtualFile().getPath();
            Sdk sdk = GoSdkUtil.getGoogleGoSdkForFile(containingFile);

            GeneralCommandLine command = new GeneralCommandLine();

            String goFmtPath = ((GoSdkData) sdk.getSdkAdditionalData()).BINARY_PATH + "/gofmt";

            command.setExePath(goFmtPath);
            command.addParameter("-w");
            command.addParameter(filePath);

            command.createProcess();

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
