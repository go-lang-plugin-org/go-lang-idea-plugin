package ro.redeul.google.go;

import com.intellij.facet.FacetManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.testFramework.IdeaTestCase;
import com.intellij.testFramework.UsefulTestCase;
import com.intellij.testFramework.builders.ModuleFixtureBuilder;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.intellij.testFramework.fixtures.TestFixtureBuilder;
import org.junit.After;
import org.junit.Before;
import ro.redeul.google.go.config.facet.GoFacet;
import ro.redeul.google.go.config.facet.GoFacetType;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.config.sdk.GoSdkType;

import java.io.File;
import java.lang.reflect.Method;

public abstract class GoTestCase<FixtureType extends IdeaProjectTestFixture> extends UsefulTestCase {

    protected FixtureType fixture;
    protected Method currentRunningTestMethod;

    protected GoTestCase() {
        IdeaTestCase.initPlatformPrefix();
    }

    @Before
    public void before(Method method) throws Throwable {
        this.currentRunningTestMethod = method;

        TestFixtureBuilder<IdeaProjectTestFixture> projectBuilder =
                getProjectBuilder();

//        final JavaModuleFixtureBuilder moduleFixtureBuilder = projectBuilder.addModule(JavaModuleFixtureBuilder.class);
//        moduleFixtureBuilder.addSourceContentRoot(myFixture.getTempDirPath());
//        customizeProject(projectBuilder);
        
        this.fixture = createTestFixture(projectBuilder.getFixture());
        this.fixture.setUp();
    }

    protected TestFixtureBuilder<IdeaProjectTestFixture> getProjectBuilder() {

        IdeaTestFixtureFactory testFixtureFactory = IdeaTestFixtureFactory.getFixtureFactory();

        return testFixtureFactory.createLightFixtureBuilder(getProjectDescriptor());
    }

    protected void customizeProject(TestFixtureBuilder<IdeaProjectTestFixture> projectBuilder) {
        
    }

    private <ModuleBuilder extends ModuleFixtureBuilder> Class<ModuleFixtureBuilder> getModuleBuilderClass() {
        return ModuleFixtureBuilder.class;
    }

    @After
    public void after(Method method) throws Exception {
        this.fixture.tearDown();
        this.currentRunningTestMethod = null;
    }

    protected String getTestRootPath() {
        return "testdata";
    }

    protected abstract String getRelativeDataPath();

    protected abstract FixtureType createTestFixture(IdeaProjectTestFixture projectTestFixture) throws Exception;

    protected Module getModule() {
        return fixture != null ? fixture.getModule() : null;
    }

    protected Project getProject() {
        return fixture != null ? fixture.getProject() : null;

    }

    protected String getTestName() {
        String name = currentRunningTestMethod.getName();

        name = StringUtil.trimStart(name, "test");
        if (StringUtil.isEmpty(name)) {
            return getRelativeDataPath();
        }

        String processedName = getRelativeDataPath() + File.separator;
        boolean isFirst = true;

        for (String s : name.split("(?<=\\p{Lower})(?=\\p{Upper})")) {
            if (isFirst) {
                processedName += s.toLowerCase();
                isFirst = false;
            } else {
                processedName += File.separator + s.toLowerCase();
            }
        }

        return processedName;
    }

    @Override
    protected String getTestName(boolean lowercaseFirstLetter) {
        return getTestName();
    }

    protected DefaultLightProjectDescriptor getProjectDescriptor() {

        return new DefaultLightProjectDescriptor() {

            @Override            
            public void configureModule(Module module, ModifiableRootModel model, ContentEntry contentEntry) {
                                
                model.setSdk(getSdk());

                // add go sdk to project root
                Sdk sdk = ProjectJdkTable.getInstance().createSdk("go Sdk", GoSdkType.getInstance());

                SdkModificator modificator = sdk.getSdkModificator();

                GoSdkData goSdkData = new GoSdkData();

                modificator.setHomePath("bau");
                modificator.setVersionString("1");
                modificator.setSdkAdditionalData(goSdkData);
                modificator.commitChanges();

                // add go module facet
                FacetManager facetManager = FacetManager.getInstance(module);

                GoFacet goFacet = facetManager.addFacet(new GoFacetType(), "go facet", null);
                goFacet.getConfiguration().SDK_NAME = "go Sdk";

                ContentEntry contentEntries[] = model.getContentEntries();
                for (ContentEntry entry : contentEntries) {
                    int a = 10;
                }
            }
        };

    }

}
