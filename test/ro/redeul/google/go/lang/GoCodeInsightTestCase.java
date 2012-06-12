package ro.redeul.google.go.lang;

import com.intellij.testFramework.fixtures.*;
import com.intellij.testFramework.fixtures.impl.LightTempDirTestFixtureImpl;
import ro.redeul.google.go.GoTestCase;

public abstract class GoCodeInsightTestCase extends GoTestCase<CodeInsightTestFixture> {

    @Override
    protected CodeInsightTestFixture createTestFixture(IdeaProjectTestFixture fixture) throws Exception {

        JavaCodeInsightTestFixture codeInsightFixture =
                JavaTestFixtureFactory.getFixtureFactory()
                                      .createCodeInsightFixture(fixture, new LightTempDirTestFixtureImpl(true));

        codeInsightFixture.setTestDataPath(getTestRootPath());

        return codeInsightFixture;
    }
}
