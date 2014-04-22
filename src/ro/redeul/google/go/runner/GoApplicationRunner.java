package ro.redeul.google.go.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.DefaultProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunContentBuilder;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugProcessStarter;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.sdk.GoSdkUtil;
import uk.co.cwspencer.gdb.Gdb;
import uk.co.cwspencer.gdb.messages.GdbEvent;
import uk.co.cwspencer.ideagdb.debug.GdbDebugProcess;
import uk.co.cwspencer.ideagdb.run.GdbExecutionResult;

import java.io.File;
import java.io.IOException;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 27, 2010
 * Time: 1:51:43 PM
 */
public class GoApplicationRunner extends DefaultProgramRunner {

    @NotNull
    public String getRunnerId() {
        return "GoApplicationRunner";
    }

    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        //If it isnt a GoApplicationConfiguration, we wont run it!
        if(!(profile instanceof GoApplicationConfiguration)) {
            return false;
        }

        //Debugging is only available, if it will be built before run
        GoApplicationConfiguration goConfig = (GoApplicationConfiguration)profile;
        if(DefaultDebugExecutor.EXECUTOR_ID.equals(executorId) && goConfig.goBuildBeforeRun) {
            return true;
        }

        //Running is always available
        return DefaultRunExecutor.EXECUTOR_ID.equals(executorId);
    }

    protected RunContentDescriptor doExecute(final Project project, RunProfileState state, RunContentDescriptor contentToReuse, ExecutionEnvironment env) throws ExecutionException {
        FileDocumentManager.getInstance().saveAllDocuments();

        final ExecutionResult executionResult = state.execute(env.getExecutor(), this);
        if (executionResult == null) {
            return null;
        }


        if(env.getExecutor().getClass().equals(DefaultRunExecutor.class)) {
            final RunContentBuilder contentBuilder = new RunContentBuilder(this, executionResult, env);
            contentBuilder.setEnvironment(env);

            return contentBuilder.showRunContent(contentToReuse);
        } else {
            GoApplicationConfiguration configuration = ((GdbExecutionResult)executionResult).m_configuration;

            String execName = configuration.goOutputDir.concat("/").concat(project.getName());

            if (GoSdkUtil.isHostOsWindows()) {
                execName = execName.concat(".exe");
                execName = execName.replaceAll("\\\\", "/");
            }

            final XDebugSession debugSession = XDebuggerManager.getInstance(project).startSession(this,
                env, contentToReuse, new XDebugProcessStarter() {
                     @NotNull
                     @Override
                     public XDebugProcess start(@NotNull XDebugSession session) throws ExecutionException {
                            session.setAutoInitBreakpoints(false);
                            return new GdbDebugProcess(project, session, (GdbExecutionResult) executionResult);
                     }
            });

            Sdk sdk = GoSdkUtil.getGoogleGoSdkForProject(project);
            if ( sdk == null ) {
                debugSession.stop();
                return null;
            }

            final GoSdkData sdkData = (GoSdkData)sdk.getSdkAdditionalData();
            if ( sdkData == null ) {
                debugSession.stop();
                return null;
            }

            GdbDebugProcess debugProcess = ((GdbDebugProcess) debugSession.getDebugProcess());

            String goRootPath;
            try {
                goRootPath = (new File(sdkData.GO_GOROOT_PATH)).getCanonicalPath();
            } catch (IOException ignored) {
                debugSession.stop();
                return null;
            }

            final Gdb gdbProcess = debugProcess.m_gdb;

            // Queue startup commands
            gdbProcess.sendCommand("-list-features", new Gdb.GdbEventCallback() {
                 @Override
                 public void onGdbCommandCompleted(GdbEvent event) {
                      gdbProcess.onGdbCapabilitiesReady(event);
                 }
            });
            gdbProcess.sendCommand("add-auto-load-safe-path " + goRootPath);

            String pythonRuntime = goRootPath + "/src/pkg/runtime/runtime-gdb.py";
            if (GoSdkUtil.checkFileExists(pythonRuntime)) {
                gdbProcess.sendCommand("source " + pythonRuntime);
            }

            gdbProcess.sendCommand("file " + execName);

            //If we got any script arguments, pass them into gdb
            if(!configuration.scriptArguments.equals("")) {
                gdbProcess.sendCommand("set args " + configuration.scriptArguments);
            }

            debugSession.initBreakpoints();

            // Send startup commands
            String[] commandsArray = configuration.STARTUP_COMMANDS.split("\\r?\\n");
            for (String command : commandsArray) {
                command = command.trim();
                if (!command.isEmpty()) {
                    gdbProcess.sendCommand(command);
                }
            }

            if (configuration.autoStartGdb) {
                gdbProcess.sendCommand("run");
            }

            return debugSession.getRunContentDescriptor();
        }
    }
}
