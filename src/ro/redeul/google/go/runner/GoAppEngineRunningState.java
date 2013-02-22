package ro.redeul.google.go.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.filters.*;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: jhonny
 * Date: 28/07/11
 */
public class GoAppEngineRunningState extends CommandLineState {

    private static final String ID = "Go App Engine Console";
    private static final String TITLE = "Go App Engine Console Output";

    private String sdkDirectory;
    private String scriptArguments;
    private String workDir;

    private ExecutionEnvironment env;
    //private OSProcessHandler processHandler;

    public GoAppEngineRunningState(ExecutionEnvironment env, String sdkDirectory, String scriptArguments, String workDir) {
        super(env);
        this.sdkDirectory = sdkDirectory;
        this.scriptArguments = scriptArguments;
        this.workDir = workDir;
        this.env = env;
    }

    @Override
    protected OSProcessHandler startProcess() throws ExecutionException {

        GeneralCommandLine commandLine = new GeneralCommandLine();

        if (scriptArguments != null && scriptArguments.trim().length() > 0) {
            commandLine.addParameter(scriptArguments);
        }
        if (workDir.contains("\\")) { // we are on Windows...
            commandLine.setExePath("python.exe");
            commandLine.addParameter(sdkDirectory + "/dev_appserver.py");
        } else {
            commandLine.setExePath(sdkDirectory + "/dev_appserver.py");
        }
        commandLine.addParameter(".");
        commandLine.setWorkDirectory(workDir);

        OSProcessHandler processHandler = GoApplicationProcessHandler.runCommandLine(commandLine);

        final Pattern pattern = Pattern.compile("(.+\\.go):(\\d+):.+");

        final TextConsoleBuilder consoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(env.getProject());

        final ConsoleView consoleView = consoleBuilder.getConsole();
        setConsoleBuilder(consoleBuilder);
        consoleBuilder.addFilter( new Filter(){
            @Override
            public Result applyFilter(String s, int i) {

                Matcher matcher = pattern.matcher(s);
                while (matcher.find()) {
                    String filePath = matcher.group(1);
                    String line = matcher.group(2);
                    VirtualFile file = VirtualFileManager.getInstance().findFileByUrl("file://" + filePath);
                    int outputStart = i - s.length();
                    if (file != null) {
                        HyperlinkInfo hyperlink = new OpenFileHyperlinkInfo(env.getProject(), file, Integer.parseInt(line) - 1);
                        return new Result(outputStart ,outputStart + filePath.length() + line.length() +1,hyperlink);
                    }
                }
                return null;
            }
        });
        return processHandler;
    }
}
