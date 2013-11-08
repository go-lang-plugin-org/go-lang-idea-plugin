/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.idea.golang.jps.incremental;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.intellij.execution.process.BaseOSProcessHandler;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.CompilerMessage;
import static com.intellij.openapi.diagnostic.Logger.getInstance;

public class GoBuildOsProcessHandler extends BaseOSProcessHandler {

    private final Consumer<String> statusUpdater;
    private final StringBuffer stdErr = new StringBuffer();

    private static final Logger LOG =
        getInstance("#ro.redeul.idea.golang.jps.incremental.GoBuildOsProcessHandler");

    private final static Pattern pattern = Pattern.compile("([^:]+):(\\d+): (.+)", Pattern.UNIX_LINES);
    private final List<CompilerMessage> compilerMessages = new ArrayList<CompilerMessage>();

    public GoBuildOsProcessHandler(@NotNull Process process,
                                   @NotNull Consumer<String> statusUpdater) {
        super(process, null, null);
        this.statusUpdater = statusUpdater;
    }


    public void notifyTextAvailable(final String text, final Key outputType) {
        super.notifyTextAvailable(text, outputType);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Received from groovyc: " + text);
        }

        if (outputType == ProcessOutputTypes.SYSTEM) {
            return;
        }

        if (outputType == ProcessOutputTypes.STDERR) {
            stdErr.append(StringUtil.convertLineSeparators(text));
            return;
        }

        parseOutput(text);
    }

    private void parseOutput(String text) {
        Matcher matcher = pattern.matcher(text.trim());
        if (matcher.matches()) {
            String fileName = matcher.group(1);
            String message = matcher.group(3);
            Integer line = Integer.parseInt(matcher.group(2));

            compilerMessages.add(
                new CompilerMessage("go build", BuildMessage.Kind.ERROR, message, fileName, -1, -1, -1, line, 0));
        } else {
            compilerMessages.add(
                new CompilerMessage("go build", BuildMessage.Kind.INFO, text)
            );
        }
    }

    public List<CompilerMessage> getCompilerMessages() {
        return compilerMessages;
    }
}
