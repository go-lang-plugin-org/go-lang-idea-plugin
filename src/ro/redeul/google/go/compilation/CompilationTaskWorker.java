package ro.redeul.google.go.compilation;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.CapturingProcessHandler;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.EnvironmentUtil;
import com.intellij.util.StringBuilderSpinAllocator;
import org.jetbrains.annotations.NonNls;
import ro.redeul.google.go.util.ProcessUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Go Makefile compiler implementation.
 * <p/>
 * Author: Alexandre Normand
 * Date: 11-05-28
 * Time: 8:45 PM
 */
class CompilationTaskWorker {

    private static final Logger LOG = Logger.getInstance("#ro.redeul.google.go.compilation.CompilationTaskWorker");

    private final ProcessUtil.StreamParser<List<CompilerMessage>> outputParser;

    public CompilationTaskWorker(ProcessUtil.StreamParser<List<CompilerMessage>> outputParser) {
        this.outputParser = outputParser;
    }

    ProcessOutput executeTask(GeneralCommandLine command, String path, CompileContext context) {

        if (LOG.isDebugEnabled()) {
            @NonNls final StringBuilder buf = StringBuilderSpinAllocator.alloc();
            try {
                buf.append("\n===== Environment:===========================\n");
                for (String pair : EnvironmentUtil.getEnvironment()) {
                    buf.append("\t").append(pair).append("\n");
                }

                Map<String, String> map = command.getEnvParams();
                if (map != null) {
                    buf.append("===== Custom environment:").append("\n");
                    for (String key : map.keySet()) {
                        buf.append("\t").append(key).append("=").append(map.get(key)).append("\n");
                    }
                }
                buf.append("===== Working folder:===========================\n");
                buf.append("\t").append(path).append("\n");
                buf.append("===== Command: ").append("\n");
                buf.append("\t").append(command.getCommandLineString()).append("\n");
                buf.append("=============================================================================\n");
                LOG.debug(buf.toString());
            } finally {
                StringBuilderSpinAllocator.dispose(buf);
            }
        }

        command.setWorkDirectory(path);

        try {
            ProcessOutput output = new CapturingProcessHandler(
                    command.createProcess(),
                    Charset.defaultCharset(),
                    command.getCommandLineString()).runProcess();

            if (output.getExitCode() != 0) {

                processErrors(context, output.getStderrLines(), outputParser);
                processErrors(context, output.getStdoutLines(), outputParser);

                context.addMessage(CompilerMessageCategory.WARNING, "process exited with code: " + output.getExitCode(), null, -1, -1);
            }

            return output;
        } catch (ExecutionException ex) {
            context.addMessage(CompilerMessageCategory.WARNING, ex.getMessage(), null, -1, -1);
            return null;
        }
    }

    private void processErrors(CompileContext context, List<String> outputLines, ProcessUtil.StreamParser<List<CompilerMessage>> outputStreamParser) {
        if (!outputLines.isEmpty()) {
            for (String line : outputLines) {
                List<CompilerMessage> compilerMessages = outputStreamParser.parseStream(line);

                for (CompilerMessage compilerMessage : compilerMessages) {
                    context.addMessage(compilerMessage.getCategory(), compilerMessage.getMessage(),
                            compilerMessage.getFileName(), compilerMessage.getRow(), compilerMessage.getColumn());
                }
            }
        }
    }

    static String generateFileUrl(String workingDirectory, String filename) {
        // Using this method instead of File.toURI().toUrl() because it doesn't use the two slashes after ':'
        // and IDEA doesn't recognises this as a valid URL (even though it seems to be spec compliant)

        File sourceFile = new File(workingDirectory, filename);
        String url = null;
        try {
            url = "file://" + sourceFile.getCanonicalPath();
        } catch (IOException e) {
            LOG.error("Cannot create url for compiler message: " + filename, e);
        }
        return url;
    }


}
