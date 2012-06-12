package ro.redeul.google.go;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import ro.redeul.google.go.lang.psi.GoFile;

public abstract class GoLightCodeInsightFixtureTestCase
    extends LightCodeInsightFixtureTestCase {

    @Override
    protected String getBasePath() {
        return "testdata/" + getTestDataRelativePath();
    }

    protected abstract String getTestDataRelativePath();

    protected GoFile parse(String fileText) {
        return (GoFile) myFixture.configureByText(GoFileType.INSTANCE, fileText);
    }
}
