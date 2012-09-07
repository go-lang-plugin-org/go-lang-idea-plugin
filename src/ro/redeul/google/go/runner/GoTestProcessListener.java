package ro.redeul.google.go.runner;

import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.openapi.util.Key;

/**
 * GoTestProcessListener monitors the output of the go test process
 * and reports test progress and result back to Intellij IDEA.
 *
 * The output header of a test case is always:
 * === RUN [Test Name]
 * --- [FAIL or PASS]: [Test Name]
 *
 */
class GoTestProcessListener extends ProcessAdapter {
    private static final String TEST_CASE_START_HEADER = "=== RUN ";
    private static final String TEST_CASE_FAILED_HEADER = "--- FAIL: ";

    private final GoTestReporter reporter;

    public GoTestProcessListener(ProcessHandler processHandler, String packageDir) {
        this.reporter = new GoTestReporter(processHandler, packageDir);
    }

    @Override
    public void startNotified(ProcessEvent event) {
        reporter.testRunStarted();
    }

    @Override
    public void processWillTerminate(ProcessEvent event, boolean willBeDestroyed) {
        reporter.testRunEnded();
    }

    @Override
    public void onTextAvailable(ProcessEvent event, Key outputType) {
        String text = event.getText();
        if (text == null) {
            return;
        }

        if (outputType == ProcessOutputTypes.STDOUT) {
            standardOutputAvailable(text);
        } else if (outputType == ProcessOutputTypes.STDERR) {
            standardErrorAvailable(text);
        }
    }

    private void standardOutputAvailable(String text) {
        if (text.startsWith(TEST_CASE_START_HEADER)) {
            reporter.testCaseStarted(text.substring(TEST_CASE_START_HEADER.length()).trim());
        } else if (text.startsWith(TEST_CASE_FAILED_HEADER)) {
            reporter.testCaseFailed();
        }
    }

    private void standardErrorAvailable(String text) {
    }
}
