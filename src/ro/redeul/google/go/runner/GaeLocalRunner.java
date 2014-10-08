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
import org.jetbrains.annotations.NotNull;

/**
 * Author: Johnny Everson
 * <p/>
 * Date: Aug 27, 2010
 * Time: 1:51:43 PM
 */
public class GaeLocalRunner extends DefaultProgramRunner {

    @NotNull
    public String getRunnerId() {
        return "GAEApplicationRunConfiguration";
    }

    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        if (DefaultDebugExecutor.EXECUTOR_ID.equals(executorId)) {
            return false;
        }

        return executorId.equals(DefaultRunExecutor.EXECUTOR_ID) && profile instanceof GaeLocalConfiguration;
    }

    // TODO: check why this exists here. It shouldn't be here.

    protected RunContentDescriptor doExecute(@NotNull RunProfileState state,
                                             @NotNull ExecutionEnvironment env) throws ExecutionException {
        FileDocumentManager.getInstance().saveAllDocuments();

        ExecutionResult executionResult = state.execute(env.getExecutor(), this);
        if (executionResult == null) {
            return null;
        }

        env = RunContentBuilder.fix(env, this);
        return new RunContentBuilder(executionResult, env).showRunContent(env.getContentToReuse());
    }
}
