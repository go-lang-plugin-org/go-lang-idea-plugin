package ro.redeul.google.go.compilation;

import com.intellij.openapi.compiler.CompilerMessageCategory;
import ro.redeul.google.go.util.ProcessUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* Author: Toader Mihai Claudiu <mtoader@gmail.com>
* <p/>
* Date: 8/21/11
* Time: 1:27 PM
*/
class GoCompilerOutputStreamParser implements ProcessUtil.StreamParser<List<CompilerMessage>> {

    private final static Pattern pattern = Pattern.compile("([^:]+):(\\d+): ((?:(?:.)|(?:\\n(?!/)))+)", Pattern.UNIX_LINES);
    private final String basePath;

    public GoCompilerOutputStreamParser(String basePath) {
        this.basePath = basePath;
    }

    public List<CompilerMessage> parseStream(String data) {

        List<CompilerMessage> messages = new ArrayList<>();

        Matcher matcher = pattern.matcher(data);

        if (matcher.find()) {
            String filename = matcher.group(1);
            String url = CompilationTaskWorker.generateFileUrl(basePath, filename);
            messages.add(new CompilerMessage(CompilerMessageCategory.ERROR, matcher.group(3), url, Integer.parseInt(matcher.group(2)), -1));
        } else {
            messages.add(new CompilerMessage(CompilerMessageCategory.WARNING, data, null, -1, -1));
        }

        return messages;
    }
}
