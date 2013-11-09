package ro.redeul.google.go.runner;

import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.openapi.util.Key;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GoTestProcessListener monitors the output of the go test process
 * and reports test progress and result back to Intellij IDEA.
 *
 * The output header of a test case is always:
 * === RUN [Test Name]
 * --- [FAIL or PASS]: [Test Name]
 *
 * The output header of a benchmark is always:
 * BenchmarkXxx
 * or:
 * BenchmarkXxx-CpuNum
 * When multiple CPUs are used
 *
 * If the benchmark failed, you can always find string
 * --- FAIL: BenchmarkXxx
 *
 */
class GoTestProcessListener extends ProcessAdapter {
    private static final String TEST_CASE_START_HEADER = "=== RUN ";
    private static final String TEST_CASE_FAILED_HEADER = "--- FAIL: ";

    private static final String BENCHMARK_START_HEADER = "Benchmark";
    private static final String BENCHMARK_FAILED_STRING = "--- FAIL: ";

    private static final Pattern BENCHMARK_IDENTIFIER = Pattern.compile("^(Benchmark\\p{L}[\\p{L}\\p{Digit}]*)(-\\d+)?\t.*\n?");

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
        }
    }

    private void standardOutputAvailable(String text) {
        if (text.startsWith(TEST_CASE_START_HEADER)) {
            reporter.testCaseStarted(text.substring(TEST_CASE_START_HEADER.length()).trim());
        } else if (text.startsWith(TEST_CASE_FAILED_HEADER)) {
            reporter.testCaseFailed();
        } else if (text.startsWith(BENCHMARK_START_HEADER)) {
            Matcher matcher = BENCHMARK_IDENTIFIER.matcher(text);
            if (matcher.matches()) {
                reporter.testCaseStarted(matcher.group(1));
            }
        }

        checkWhetherBenchmarkFailed(text);
    }

    private void checkWhetherBenchmarkFailed(String text) {
        String testCaseName = reporter.getTestCaseName();
        if (testCaseName != null &&
                testCaseName.startsWith(BENCHMARK_START_HEADER) &&
                text.contains(BENCHMARK_FAILED_STRING + testCaseName)) {
            reporter.testCaseFailed();
        }
    }
}
