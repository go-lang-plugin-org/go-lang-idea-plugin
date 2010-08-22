package ro.redeul.google.go;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.testFramework.IdeaTestCase;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.UsefulTestCase;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.intellij.testFramework.fixtures.TestFixtureBuilder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 22, 2010
 * Time: 8:18:45 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class GoTestCase<FixtureType extends IdeaProjectTestFixture> extends UsefulTestCase {

    protected FixtureType fixture;

    protected Method currentRunningTestMethod;

    protected GoTestCase() {
        IdeaTestCase.initPlatformPrefix();
    }

    @BeforeMethod
    public void before(Method method) throws Exception {
        this.currentRunningTestMethod = method;

        IdeaTestFixtureFactory fixtureFactory = IdeaTestFixtureFactory.getFixtureFactory();
        TestFixtureBuilder<IdeaProjectTestFixture> fixtureBuilder = fixtureFactory.createLightFixtureBuilder(getProjectDescriptor());

        final IdeaProjectTestFixture fixture = fixtureBuilder.getFixture();

        this.fixture = createFixture(fixture);
        this.fixture.setUp();
    }

    @AfterMethod
    public void after(Method method) throws Exception {
        this.fixture.tearDown();
        this.currentRunningTestMethod = null;
    }

    protected String getTestRootPath() {
        return "testdata";
    }

    protected abstract String getRelativeDataPath();

    protected abstract FixtureType createFixture(IdeaProjectTestFixture fixture);

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

        for ( String s : name.split("(?<=\\p{Lower})(?=\\p{Upper})") )
        {
            if ( isFirst ) {
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

    public static final LightProjectDescriptor JAVA_1_5 = new DefaultLightProjectDescriptor();

    protected LightProjectDescriptor getProjectDescriptor() {
        return JAVA_1_5;
    }

}
