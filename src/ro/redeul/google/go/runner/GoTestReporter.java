package ro.redeul.google.go.runner;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.execution.testframework.sm.ServiceMessageBuilder;

import static jetbrains.buildServer.messages.serviceMessages.ServiceMessageTypes.TEST_SUITE_FINISHED;
import static jetbrains.buildServer.messages.serviceMessages.ServiceMessageTypes.TEST_SUITE_STARTED;
import static jetbrains.buildServer.messages.serviceMessages.ServiceMessageTypes.TEST_STARTED;
import static jetbrains.buildServer.messages.serviceMessages.ServiceMessageTypes.TEST_FINISHED;
import static jetbrains.buildServer.messages.serviceMessages.ServiceMessageTypes.TEST_FAILED;

/**
 * GoTestReporter reports test progress to Intellij IDEA via ServiceMessage.
 */
class GoTestReporter {
    private static final String TEST_REPORTER_ATTACHED = "enteredTheMatrix";
    private static final String NAME = "name";
    private static final String DURATION = "duration";
    public static final String LOCATION_HINT = "locationHint";

    private final ProcessHandler handler;
    private final String packageDir;

    private long testCaseStartingTime;
    private long testSuiteStartingTime;
    private String testSuiteName = null;
    private String testCaseName = null;

    public GoTestReporter(ProcessHandler handler, String packageDir) {
        this.handler = handler;
        this.packageDir = packageDir;
    }

    public void testRunStarted() {
        report(TEST_REPORTER_ATTACHED);
    }

    public void testRunEnded() {
        if (testCaseName != null) {
            testCaseFinished();
        }

        if (testSuiteName != null) {
            testSuiteFinished();
        }

        handler.destroyProcess();
    }

    public void testSuiteStarted(String name) {
        if (testSuiteName != null) {
            testSuiteFinished();
        }
        report(TEST_SUITE_STARTED, name);
        testSuiteName = name;
        testSuiteStartingTime = System.currentTimeMillis();
    }

    public void testSuiteFinished() {
        if (testSuiteName != null) {
            report(TEST_SUITE_FINISHED, testSuiteName, System.currentTimeMillis() - testSuiteStartingTime);
            testSuiteName = null;
        }
    }

    public void testCaseStarted(String name) {
        if (testCaseName != null) {
            testCaseFinished();
        }

        String location = String.format("%s://%s:%s", GoTestLocationProvider.GO_TEST_CASE, packageDir, name);
        report(TEST_STARTED, name, location);
        testCaseName = name;
        testCaseStartingTime = System.currentTimeMillis();
    }

    public void testCaseFailed() {
        if (testCaseName != null) {
            report(new ServiceMessageBuilder(TEST_FAILED)
                    .addAttribute(NAME, testCaseName)
                    .addAttribute("message", "")
                    .addAttribute("details", "")
            );
        }
    }

    public void testCaseFinished() {
        if (testCaseName != null) {
            report(TEST_FINISHED, testCaseName, System.currentTimeMillis() - testCaseStartingTime);
            testCaseName = null;
        }
    }

    public String getTestCaseName() {
        return testCaseName;
    }

    private void report(ServiceMessageBuilder builder) {
        handler.notifyTextAvailable(builder.toString() + '\n', ProcessOutputTypes.STDOUT);
    }

    private void report(String title) {
        report(new ServiceMessageBuilder(title));
    }

    private void report(String title, String name) {
        report(new ServiceMessageBuilder(title).addAttribute(NAME, name));
    }

    private void report(String title, String name, String location) {
        report(new ServiceMessageBuilder(title).addAttribute(NAME, name).addAttribute(LOCATION_HINT, location));
    }

    private void report(String title, String name, long duration) {
        report(new ServiceMessageBuilder(title)
                .addAttribute(NAME, name)
                .addAttribute(DURATION, Long.toString(duration)));
    }
}
