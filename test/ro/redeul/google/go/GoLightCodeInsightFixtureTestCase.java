package ro.redeul.google.go;

import java.io.File;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.junit.Ignore;
import ro.redeul.google.go.lang.psi.GoFile;

@Ignore
public abstract class GoLightCodeInsightFixtureTestCase extends LightCodeInsightFixtureTestCase {

    protected static String testDataRoot = "testdata/";
    @Override
    protected String getBasePath() {
        return testDataRoot + getTestDataRelativePath();
    }

    protected abstract String getTestDataRelativePath();

    protected GoFile parse(String fileText) {
        return (GoFile) myFixture.configureByText(GoFileType.INSTANCE, fileText);
    }


    @Override
    protected String getTestDataPath() {
        return testDataRoot + getTestDataRelativePath();
    }

    protected String getTestFileName() {
        String baseName = getTestDataPath() + getTestName(true);
        if (new File(baseName + ".test").exists()) {
            return baseName + ".test";
        } else {
            return baseName + ".go";
        }
    }

    protected void removeContentRoots() {
        new WriteCommandAction.Simple(myModule.getProject()) {
            @Override
            protected void run() throws Throwable {
                ModuleRootManager instance =
                    ModuleRootManager.getInstance(myModule);

                ModifiableRootModel modifiableModel = instance.getModifiableModel();

                ContentEntry[] entries = instance.getContentEntries();
                for (ContentEntry entry : entries) {
                    modifiableModel.removeContentEntry(entry);
                }
                modifiableModel.commit();
            }
        }.execute().throwException();
    }
}
