package ro.redeul.google.go.compiler;

import com.intellij.compiler.CompilerManagerImpl;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.execution.runners.DefaultProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.compiler.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.roots.CompilerProjectExtension;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.builders.JavaModuleFixtureBuilder;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import com.intellij.testFramework.fixtures.TempDirTestFixture;
import com.intellij.testFramework.fixtures.impl.TempDirTestFixtureImpl;
import com.intellij.util.concurrency.Semaphore;
import com.intellij.util.ui.UIUtil;
import ro.redeul.google.go.components.GoCompilerLoader;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.config.sdk.GoSdkType;
import ro.redeul.google.go.runner.GoApplicationConfiguration;
import ro.redeul.google.go.runner.GoRunConfigurationType;
import ro.redeul.google.go.sdk.GoSdkUtil;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class GoCompilerTestCase extends JavaCodeInsightFixtureTestCase {

    private TempDirTestFixture myMainOutput;

    private Module module;

    private static File ourTempDir;

//    @Override
//    protected String getRelativeDataPath() {
//        return "compiler" + File.separator + "go";
//    }

//    @BeforeClass
//    public static void setupTempDir() throws Exception {
//        ensureTempDir();
//    }

//    @AfterClass
//    public static void removeTempDir() throws Exception {
//        if (!FileUtil.delete(ourTempDir)) {
//            System.out.println("Cannot delete " + ourTempDir);
//            ourTempDir.deleteOnExit();
//        }
//    }


    @Override
    public void setUp() throws Exception {
        myMainOutput = new TempDirTestFixtureImpl();
        myMainOutput.setUp();
        super.setUp();
        getProject().getComponent(GoCompilerLoader.class).projectOpened();

        CompilerManagerImpl.testSetup();

        new WriteCommandAction(getProject()) {
            protected void run(Result result) throws Throwable {
                addGoFacetAndSdk(myModule, getProject());
                CompilerProjectExtension.getInstance(getProject()).setCompilerOutputUrl(myMainOutput.findOrCreateDir("out").getUrl());
            }
        }.execute();
    }

    @Override
    public void tearDown() throws Exception {
        UIUtil.invokeAndWaitIfNeeded(new Runnable() {
            @Override
            public void run() {
                try {
                    myMainOutput.tearDown();
                    myMainOutput = null;
                    GoCompilerTestCase.super.tearDown();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


    private void addGoFacetAndSdk(Module module, Project project) {
        String goSdkName = "go Sdk";

        // add go sdk to project root
        Sdk sdk = ProjectJdkTable.getInstance().createSdk(goSdkName, GoSdkType.getInstance());

        SdkModificator sdkModificator = sdk.getSdkModificator();

        GoSdkData goSdkData= GoSdkUtil.getMockGoogleSdk();

        assertTrue("Test go sdk not available to run tests, check that the system property [go.test.sdk.home] or your GOROOT environment variable are set correctly.",
                goSdkData != null);

        sdkModificator.setHomePath(goSdkData.GO_HOME_PATH);
        sdkModificator.setVersionString(goSdkData.VERSION_MAJOR);
        sdkModificator.setSdkAdditionalData(goSdkData);
        sdkModificator.commitChanges();

        ProjectJdkTable.getInstance().addJdk(sdk);

        ProjectRootManager.getInstance(project).setProjectSdk(sdk);

        ModifiableRootModel modifiableModel = ModuleRootManager.getInstance(module).getModifiableModel();
        modifiableModel.inheritSdk();
        modifiableModel.commit();
        // add go module facet
    //        FacetManager facetManager = FacetManager.getInstance(module);
    //
    //        GoFacet goFacet = facetManager.addFacet(new GoFacetType(), "go facet", null);
    //
    //        goFacet.getConfiguration().SDK_NAME = goSdkName;
    }

    @Override
    protected void tuneFixture(JavaModuleFixtureBuilder moduleBuilder) throws Exception {
//        moduleBuilder.setMockJdkLevel(JavaModuleFixtureBuilder.MockJdkLevel.jdk15);
//        moduleBuilder.addJdk(GoSdkUtil.testGoogleGoSdk());
        super.tuneFixture(moduleBuilder);
    }

    protected void touch(VirtualFile file) throws IOException {
        touch(file, file.contentsToByteArray());
    }

    protected void touch(VirtualFile file, String data) throws IOException {
        touch(file, data.getBytes());
    }

    protected void touch(VirtualFile file, byte[] content) throws IOException {
        file.setBinaryContent(content, file.getModificationStamp() + 1, file.getTimeStamp() + 1);
    }

/*
    protected void tuneModuleFixture(JavaModuleFixtureBuilder moduleFixtureBuilder, JavaCodeInsightTestFixture fixture) {
        moduleFixtureBuilder.addSourceContentRoot(fixture.getTempDirPath());
        moduleFixtureBuilder.setMockJdkLevel(JavaModuleFixtureBuilder.MockJdkLevel.jdk15);
        moduleFixtureBuilder.addJdk(JavaSdkImpl.getMockJdkCE().getHomePath());

//        super.tuneModuleFixture(moduleFixtureBuilder, fixture);
    }
*/

    protected List<String> make() {
        final Semaphore semaphore = new Semaphore();
        semaphore.down();
        final ErrorReportingCallback callback = new ErrorReportingCallback(semaphore);

        UIUtil.invokeAndWaitIfNeeded(new Runnable() {
            @Override
            public void run() {
                try {
                    CompilerManager.getInstance(getProject()).make(callback);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        while (!semaphore.waitFor(100)) {
            if (SwingUtilities.isEventDispatchThread()) {
                UIUtil.dispatchAllInvocationEvents();
            }
        }
        callback.throwException();
        return callback.getMessages();
    }


    private static class ErrorReportingCallback implements CompileStatusNotification {
        private final Semaphore mySemaphore;
        private Throwable myError;
        private final List<String> myMessages = new ArrayList<String>();

        public ErrorReportingCallback(Semaphore semaphore) {
            mySemaphore = semaphore;
        }

        public void finished(boolean aborted, int errors, int warnings, final CompileContext compileContext) {
            try {
                assertFalse("Code did not compile!", aborted);
                for (CompilerMessageCategory category : CompilerMessageCategory.values()) {
                    for (CompilerMessage message : compileContext.getMessages(category)) {
                        final String msg = message.getMessage();
                        if (category != CompilerMessageCategory.INFORMATION || !msg.startsWith("Compilation completed successfully")) {
                            myMessages.add(category + ": " + msg);
                        }
                    }
                }
                if (errors > 0) {
                    fail("Compiler errors occurred! " + StringUtil.join(myMessages, "\n"));
                }
            } catch (Throwable t) {
                myError = t;
            } finally {
                mySemaphore.up();
            }
        }

        void throwException() {
            if (myError != null) {
                throw new RuntimeException(myError);
            }
        }

        public List<String> getMessages() {
            return myMessages;
        }
    }

    protected void assertOutput(String application, String output) throws ExecutionException {
        assertOutput(application, output, myModule);
    }

    protected void assertOutput(String applicationName, String output, final Module module) throws ExecutionException {

        final GoApplicationConfiguration configuration = new GoApplicationConfiguration("app", getProject(), GoRunConfigurationType.getInstance());

        configuration.scriptName = applicationName + ".go";
        configuration.setModule(module);

        final DefaultRunExecutor extension = Executor.EXECUTOR_EXTENSION_NAME.findExtension(DefaultRunExecutor.class);

        final ExecutionEnvironment environment = new ExecutionEnvironment(
            configuration,
                extension,
                getProject(),
                null);

        final DefaultProgramRunner runner = ProgramRunner.PROGRAM_RUNNER_EP.findExtension(DefaultProgramRunner.class);
        final StringBuffer sb = new StringBuffer();
        final Semaphore semaphore = new Semaphore();

        semaphore.down();
        runner.execute(environment, new ProgramRunner.Callback() {
            public void processStarted(final RunContentDescriptor descriptor) {
                Disposer.register(getProject(), new Disposable() {
                    public void dispose() {
                        descriptor.dispose();
                    }
                });
                final ProcessHandler handler = descriptor.getProcessHandler();
                assert handler != null;
                handler.addProcessListener(new ProcessAdapter() {
                    public void onTextAvailable(ProcessEvent event, Key outputType) {
                        if (ProcessOutputTypes.SYSTEM != outputType) {
                            sb.append(event.getText());
                        }
                    }

                    @Override
                    public void processTerminated(ProcessEvent event) {
                        semaphore.up();
                    }
                });
            }
        });
        semaphore.waitFor();
        assertEquals(output.trim(), StringUtil.convertLineSeparators(sb.toString().trim()));
    }
}
