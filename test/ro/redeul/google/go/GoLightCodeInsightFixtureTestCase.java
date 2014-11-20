package ro.redeul.google.go;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.junit.Ignore;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.util.GoTestUtils;

import java.io.File;
import java.io.IOException;

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

    protected void addPackage(String importPath, String ... files) throws IOException {
        for (String file : files) {
            VirtualFile virtualFile = VfsUtil.findFileByIoFile(new File(getBasePath() + "/" + file), true);
            if (virtualFile == null) {
                virtualFile = VfsUtil.findFileByIoFile(new File(file), true);
            }

            if ( virtualFile == null)
                continue;

            myFixture.addFileToProject(
                    FileUtil.toCanonicalPath(importPath + "/" + virtualFile.getName()),
                    VfsUtil.loadText(virtualFile));
        }
    }

    protected void addPackageBuiltin() throws IOException {
        addPackage("builtin", testDataRoot + "/builtin/builtin.go");
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

    @Override
    public void runBare() throws Throwable {
        if ( GoTestUtils.shouldRunBare(this) )
            super.runBare();
    }
}
