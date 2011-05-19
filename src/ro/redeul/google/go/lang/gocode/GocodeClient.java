package ro.redeul.google.go.lang.gocode;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.TempFiles;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.intellij.openapi.diagnostic.Logger;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.sdk.GoSdkUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Alexandre Normand
 * Date: 11-05-18
 * Time: 8:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class GocodeClient {

    private static final Logger Log = Logger.getInstance("#ro.redeul.google.go.lang.gocode.GocodeClient");

    private static final String TEMPFILE_DIR_PREFIX = "google-go-lang-for-idea-plugin";

    public List<LookupElement> getGocodeCompletions(@NotNull CompletionParameters parameters,
                                                           @NotNull ProcessingContext context) {
        List<LookupElement> lookupElements = new ArrayList<LookupElement>();

        if (Log.isDebugEnabled()) {
            Log.debug("Autocompleting with parameters: " + parameters);
            Log.debug("Working directory: " + System.getenv("GOBIN"));
        }

        try {
            File tempFile = createTemporaryFileFor(parameters.getPosition().getContainingFile());
            ProcessBuilder processBuilder = new ProcessBuilder();

            File goBinaryPath = getGoBinaryPath(parameters);
            processBuilder.directory(goBinaryPath);
            processBuilder.command(goBinaryPath.getAbsolutePath() + File.separator + "gocode", "-f=nice", "-in=" + tempFile.getAbsolutePath(), "autocomplete",
                    String.valueOf(parameters.getPosition().getTextOffset()));
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            while (line != null)
            {
                lookupElements.add(LookupElementBuilder.create(line));
                line = reader.readLine();
            }
        } catch (IOException e) {
            Log.error("Error while writing temp file to try and autocomplete, skipping this time.", e);
        }

        return lookupElements;
    }

    private File getGoBinaryPath(CompletionParameters parameters) {
        Sdk goSdk = GoSdkUtil.getGoogleGoSdkForFile(parameters.getOriginalFile());
        GoSdkData goSdkData = (GoSdkData) goSdk.getSdkAdditionalData();
        return new File(goSdkData.BINARY_PATH + File.separator);
    }

    private File createTemporaryFileFor(@NotNull final PsiFile psiFile) throws IOException {
        final File tmpDir = new File(System.getProperty("java.io.tmpdir"),
                TEMPFILE_DIR_PREFIX + UUID.randomUUID().toString());
        tmpDir.mkdirs();
        tmpDir.deleteOnExit();

        final File temporaryFile = new File(tmpDir, psiFile.getName());
        temporaryFile.deleteOnExit();

        writeContentsToFile(psiFile, temporaryFile);

        return temporaryFile;
    }

    private void writeContentsToFile(final PsiFile psiFile,
                                     final File outFile)
            throws IOException {

        final BufferedWriter tempFileOut = new BufferedWriter(
                new FileWriter(outFile));

        tempFileOut.write(psiFile.getText());
        tempFileOut.flush();
        tempFileOut.close();
    }

}
