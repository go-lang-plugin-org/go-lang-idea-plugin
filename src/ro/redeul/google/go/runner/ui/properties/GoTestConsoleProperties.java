package ro.redeul.google.go.runner.ui.properties;

import com.intellij.execution.Executor;
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties;
import ro.redeul.google.go.runner.GoTestConfiguration;

public class GoTestConsoleProperties extends SMTRunnerConsoleProperties {
    private GoTestConfiguration config;

    public GoTestConsoleProperties(GoTestConfiguration config, Executor executor) {
        super(config, "GoTest", executor);
    }

    @Override
    public GoTestConfiguration getConfiguration() {
        return (GoTestConfiguration) super.getConfiguration();
    }
}
