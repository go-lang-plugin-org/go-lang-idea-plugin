package ro.redeul.google.go.lang;

import com.intellij.testFramework.fixtures.*;
import com.intellij.testFramework.fixtures.impl.LightTempDirTestFixtureImpl;
import ro.redeul.google.go.GoTestCase;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 25, 2010
 * Time: 2:38:28 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class GoCodeInsightTestCase extends GoTestCase<CodeInsightTestFixture> {

    @Override
    protected CodeInsightTestFixture createTestFixture(IdeaProjectTestFixture fixture) throws Exception {

        JavaCodeInsightTestFixture codeInsightFixture =
                JavaTestFixtureFactory.getFixtureFactory().createCodeInsightFixture(fixture, new LightTempDirTestFixtureImpl(true));

        codeInsightFixture.setTestDataPath(getTestRootPath());
        return codeInsightFixture;
    }
}
