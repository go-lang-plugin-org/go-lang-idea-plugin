package ro.redeul.google.go.compilation.handlers;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessTerminatedListener;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 23, 2010
 * Time: 8:47:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class GoCompilerProcessHandler extends OSProcessHandler {

    private GoCompilerProcessHandler(Process process, String commandLine) {
        super(process, commandLine);
    }

    public static GoCompilerProcessHandler runCompiler(final GeneralCommandLine commandLine) throws ExecutionException {
      final GoCompilerProcessHandler goCompiler = new GoCompilerProcessHandler(
              commandLine.createProcess(),
              commandLine.getCommandLineString());

      ProcessTerminatedListener.attach(goCompiler);
      return goCompiler;
    }

}
