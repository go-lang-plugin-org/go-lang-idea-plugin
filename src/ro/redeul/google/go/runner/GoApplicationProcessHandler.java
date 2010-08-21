package ro.redeul.google.go.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessTerminatedListener;

import java.nio.charset.Charset;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 19, 2010
 * Time: 4:56:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoApplicationProcessHandler extends OSProcessHandler {

    public GoApplicationProcessHandler(Process process, String commandLineString, Charset charset) {
        super(process, commandLineString);
    }

    public static GoApplicationProcessHandler runCommandLine(final GeneralCommandLine commandLine) throws ExecutionException {
      final GoApplicationProcessHandler goAppProcess = new GoApplicationProcessHandler(
              commandLine.createProcess(),
              commandLine.getCommandLineString(),
              commandLine.getCharset());

      ProcessTerminatedListener.attach(goAppProcess);
      return goAppProcess;
    }

}
