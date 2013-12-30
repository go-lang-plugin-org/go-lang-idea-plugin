package ro.redeul.google.go;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

/**
 * Base class for languages test cases.
 * <p/>
 * Created on Dec-30-2013 01:08
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public abstract class GoLightCodeInsightTestCase extends LightCodeInsightFixtureTestCase {

    @Override
    final protected String getTestDataPath() {
        return FileUtil.normalize(String.format("%s/%s", "testdata", getRelativeTestDataPath()));
    }

    final protected String beforeFileName(String fileName) {
        return String.format("%s.go", fileName);
    }

    final protected String afterFileName(String fileName) {
        return String.format("%s_.go", fileName);
    }

    protected abstract String getRelativeTestDataPath();

    protected abstract void _test();
}
