package ro.redeul.google.go.ide.exception;

import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.execution.filters.OpenFileHyperlinkInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoExceptionFilter implements Filter {
    private static final Pattern GOROUTINE_START = Pattern.compile("^goroutine \\d+ \\[\\w+\\]:\\s*$");
    private static final Pattern GOROUTINE_END = Pattern.compile("^\\s*$");
    private static final Pattern GOROUTINE_LINE = Pattern.compile("^\t(.*):(\\d+) \\+0x\\p{XDigit}+\\s*$");

    private final Project project;
    private boolean goroutineFound = false;

    public GoExceptionFilter(Project project) {
        this.project = project;
    }

    @Override
    public Result applyFilter(String line, int entireLength) {
        if (!goroutineFound) {
            if (GOROUTINE_START.matcher(line).matches()) {
                goroutineFound = true;
            }
            return null;
        }

        if (GOROUTINE_END.matcher(line).matches()) {
            goroutineFound = false;
            return null;
        }

        Matcher matcher = GOROUTINE_LINE.matcher(line);
        if (!matcher.matches()) {
            return null;
        }

        String fileName = matcher.group(1);
        int fileLine;
        try {
            fileLine = Integer.parseInt(matcher.group(2)) - 1;
        } catch (NumberFormatException e) {
            return null;
        }

        VirtualFile vf = project.getBaseDir().getFileSystem().findFileByPath(fileName);
        if (vf == null) {
            return null;
        }
        HyperlinkInfo hyperlinkInfo = new OpenFileHyperlinkInfo(project, vf, fileLine);
        int outputStart = entireLength - line.length();
        return new Result(outputStart + matcher.start(1), outputStart + matcher.end(2), hyperlinkInfo);
    }
}
