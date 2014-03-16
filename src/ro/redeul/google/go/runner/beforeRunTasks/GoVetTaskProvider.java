package ro.redeul.google.go.runner.beforeRunTasks;

import com.intellij.execution.BeforeRunTaskProvider;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.runner.GaeLocalConfiguration;
import ro.redeul.google.go.runner.GoApplicationConfiguration;

import javax.swing.*;

/**
 * Created by d3xter on 13.03.14.
 */
public class GoVetTaskProvider extends BeforeRunTaskProvider<GoVetTask> {
    private static final Logger LOG = Logger.getInstance(GoVetTaskProvider.class);
    private final Key<GoVetTask> TaskID = new Key<GoVetTask>("GoVet");

    @Override
    public Key<GoVetTask> getId() {
        return TaskID;
    }

    @Override
    public String getName() {
        return "Go Vet";
    }

    @Nullable
    @Override
    public Icon getTaskIcon(GoVetTask task) {
        if(task.getConfiguration() instanceof GaeLocalConfiguration) {
            return GoIcons.GAE_ICON_13x13;
        } else {
            return GoIcons.GO_ICON_13x13;
        }
    }

    @Override
    public String getDescription(GoVetTask task) {
        return "Run Go Vet";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return GoIcons.GO_ICON_13x13;
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Nullable
    @Override
    public GoVetTask createTask(RunConfiguration runConfiguration) {
        return new GoVetTask(TaskID, runConfiguration);
    }

    @Override
    public boolean configureTask(RunConfiguration runConfiguration, GoVetTask task) {
        return false;
    }

    @Override
    public boolean canExecuteTask(RunConfiguration configuration, GoVetTask task) {
        //Only run Go apps at the moment
        return configuration instanceof GoApplicationConfiguration;
    }

    @Override
    public boolean executeTask(DataContext context, final RunConfiguration configuration, final ExecutionEnvironment env, GoVetTask task) {
        ApplicationManager.getApplication().invokeAndWait(new Runnable() {
            @Override
            public void run() {

                FileDocumentManager.getInstance().saveAllDocuments();

                new GoVetRunner(env.getProject(), "Go Vet", (GoApplicationConfiguration)configuration).queue();
            }
        }, ModalityState.NON_MODAL);

        return true;
    }
}
