package ro.redeul.google.go.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.DefaultProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunContentBuilder;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

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
        return executorId.equals(DefaultRunExecutor.EXECUTOR_ID) && profile instanceof GoApplicationConfiguration;
    }

    @Override
    protected RunContentDescriptor doExecute(Project project, Executor executor, RunProfileState state, RunContentDescriptor contentToReuse, ExecutionEnvironment env) throws ExecutionException {
        FileDocumentManager.getInstance().saveAllDocuments();

        ExecutionResult executionResult = state.execute(executor, this);
        if (executionResult == null) {
            return null;
        }

        final RunContentBuilder contentBuilder = new RunContentBuilder(project, this, executor);
        contentBuilder.setExecutionResult(executionResult);
        contentBuilder.setEnvironment(env);

        return contentBuilder.showRunContent(contentToReuse);
    }
}
