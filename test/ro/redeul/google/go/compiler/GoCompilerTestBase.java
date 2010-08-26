package ro.redeul.google.go.compiler;

import com.intellij.compiler.CompilerManagerImpl;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.impl.DefaultJavaProgramRunner;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.facet.FacetManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.compiler.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.projectRoots.impl.JavaSdkImpl;
import com.intellij.openapi.roots.CompilerProjectExtension;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.testFramework.builders.JavaModuleFixtureBuilder;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture;
import com.intellij.testFramework.fixtures.TempDirTestFixture;
import com.intellij.testFramework.fixtures.impl.TempDirTestFixtureImpl;
import com.intellij.util.concurrency.Semaphore;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import ro.redeul.google.go.components.GoCompilerLoader;
import ro.redeul.google.go.config.facet.GoFacet;
import ro.redeul.google.go.config.facet.GoFacetType;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.config.sdk.GoSdkType;
import ro.redeul.google.go.runner.GoRunConfiguration;
import ro.redeul.google.go.runner.GoRunConfigurationType;
import ro.redeul.google.go.util.GoSdkUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class GoCompilerTestBase extends JavaCodeInsightFixtureTestCase {

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


    @BeforeMethod
    public void setupOutputFixture() throws Exception {
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

    @AfterMethod
    public void tearDown() throws Exception {
        myMainOutput.tearDown();
        myMainOutput = null;
        super.tearDown();
    }


    private void addGoFacetAndSdk(Module module, Project project) {
        String goSdkName = "go Sdk";

        // add go sdk to project root
        Sdk sdk = ProjectJdkTable.getInstance().createSdk(goSdkName, GoSdkType.getInstance());

        SdkModificator sdkModificator = sdk.getSdkModificator();

        GoSdkData goSdkData = new GoSdkData();

        String[] goSdkParams = GoSdkUtil.getMockGoogleSdk();
        goSdkData.BINARY_PATH = goSdkParams[1];
        goSdkData.TARGET_OS = goSdkParams[2];
        goSdkData.TARGET_ARCH = goSdkParams[3];
        goSdkData.VERSION = goSdkParams[4];

        sdkModificator.setHomePath(goSdkParams[0]);
        sdkModificator.setVersionString(goSdkData.VERSION);
        sdkModificator.setSdkAdditionalData(goSdkData);
        sdkModificator.commitChanges();

        ProjectJdkTable.getInstance().addJdk(sdk);

        // add go module facet
        FacetManager facetManager = FacetManager.getInstance(module);

        GoFacet goFacet = facetManager.addFacet(new GoFacetType(), "go facet", null);

        goFacet.getConfiguration().SDK_NAME = goSdkName;
    }

    @Override
    protected void tuneFixture(JavaModuleFixtureBuilder moduleBuilder) throws Exception {
        moduleBuilder.setMockJdkLevel(JavaModuleFixtureBuilder.MockJdkLevel.jdk15);
        moduleBuilder.addJdk(JavaSdkImpl.getMockJdk14Path().getPath());
        super.tuneFixture(moduleBuilder);
    }
//    protected void customizeProjectFixture(TestFixtureBuilder<IdeaProjectTestFixture> projectBuilder) {
//
//        JavaModuleFixtureBuilder moduleBuilder = projectBuilder.addModule(JavaTestFixtureFactoryImpl.MyJavaModuleFixtureBuilderImpl.class);
//        final JavaModuleFixtureBuilder moduleBuilder = projectBuilder.addModule(JavaModuleFixtureBuilder.class);

//        moduleBuilder.addSourceContentRoot(myFixture.getTempDirPath());
//        moduleBuilder.setMockJdkLevel(JavaModuleFixtureBuilder.MockJdkLevel.jdk15);
//        moduleBuilder.addJdk(JavaSdkImpl.getMockJdk14Path().getPath());
//    }

    //    @Override

    protected void tuneModuleFixture(JavaModuleFixtureBuilder moduleFixtureBuilder, JavaCodeInsightTestFixture fixture) {
        moduleFixtureBuilder.addSourceContentRoot(fixture.getTempDirPath());
        moduleFixtureBuilder.setMockJdkLevel(JavaModuleFixtureBuilder.MockJdkLevel.jdk15);
        moduleFixtureBuilder.addJdk(JavaSdkImpl.getMockJdk14Path().getPath());

//        super.tuneModuleFixture(moduleFixtureBuilder, fixture);
    }

    protected List<String> make() {
        final Semaphore semaphore = new Semaphore();
        semaphore.down();
        final ErrorReportingCallback callback = new ErrorReportingCallback(semaphore);
        CompilerManager.getInstance(getProject()).make(callback);
        semaphore.waitFor();
        callback.throwException();
        return callback.getMessages();

//        final Semaphore semaphore = new Semaphore();
//        semaphore.down();
//
//        List<VirtualFile> roots = Arrays.asList(ModuleRootManager.getInstance(getModule()).getSourceRoots());

//        TranslatingCompilerFilesMonitor.getInstance().scanSourceContent(getProject(), roots, roots.size(), true);

//        TranslatingCompilerFilesMonitor.ourDebugMode = true;
//        TranslatingCompilerFilesMonitor.getInstance().scanSourcesForCompilableFiles(getProject());

//        for (VirtualFile root : roots) {
//            root.refresh(false, true);
//        }
//
//        final ErrorReportingCallback callback = new ErrorReportingCallback(semaphore);
//        CompilerManager.getInstance(getProject()).make(callback);
//        semaphore.waitFor();
//        callback.throwException();
//        return callback.getMessages();
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

        final GoRunConfiguration configuration = new GoRunConfiguration("app", getProject(), GoRunConfigurationType.getInstance());

        configuration.scriptName = applicationName + ".go";
        configuration.setModule(module);

        final DefaultRunExecutor extension = Executor.EXECUTOR_EXTENSION_NAME.findExtension(DefaultRunExecutor.class);

        final ExecutionEnvironment environment = new ExecutionEnvironment(
                configuration,
                getProject(),
                new RunnerSettings<JDOMExternalizable>(null, null), null, null);

        final DefaultJavaProgramRunner runner = ProgramRunner.PROGRAM_RUNNER_EP.findExtension(DefaultJavaProgramRunner.class);
        final StringBuffer sb = new StringBuffer();
        final Semaphore semaphore = new Semaphore();

        semaphore.down();
        runner.execute(extension, environment, new ProgramRunner.Callback() {
            public void processStarted(final RunContentDescriptor descriptor) {
                Disposer.register(myFixture.getProject(), new Disposable() {
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
