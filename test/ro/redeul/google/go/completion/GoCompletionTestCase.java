package ro.redeul.google.go.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import ro.redeul.google.go.GoLightCodeInsightFixtureTestCase;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class GoCompletionTestCase
    extends GoLightCodeInsightFixtureTestCase {

    protected String getTestDataRelativePath() {
        return "psi/completion/";
    }

    private boolean testDataFileExists(String fileName) {
        String absName = getTestDataPath() + File.separator + fileName;
        return LocalFileSystem.getInstance().findFileByPath(absName) != null;
    }

    protected void doTestVariants(String... additionalFiles) {
        LocalFileSystem fileSystem = LocalFileSystem.getInstance();
        final VirtualFile testRoot =
            fileSystem.findFileByPath(
                getTestDataPath() + File.separator + getTestName(false));

        List<String> files = new LinkedList<String>();

        for (String file : additionalFiles) {
            if (testDataFileExists(file)) {
                files.add(file);
            }
        }

        if (testDataFileExists("builtin.go")) {
            files.add("builtin.go");
        }

        if (testRoot != null && testRoot.isDirectory()) {
            String path = getTestName(false);
            myFixture.copyDirectoryToProject(path, "");
        }

        files.add(getTestName(false) + ".go");

        Collections.reverse(files);
        myFixture.configureByFiles(files.toArray(new String[files.size()]));
        LookupElement[] lookupElements = myFixture.completeBasic();
        System.out.println("lookupElements = " + Arrays.toString(lookupElements));
        String fileText = myFixture.getFile().getText();

        List<String> expected = new ArrayList<String>(10);
        int dataPos = fileText.indexOf("/**---");
        if (dataPos != -1) {
            String[] parts = fileText.substring(dataPos + 6).split("[\r\n]+");
            for (String part : parts) {
                part = part.trim();
                if (!part.isEmpty()) {
                    expected.add(part);
                }
            }
        }

        assertOrderedEquals(myFixture.getLookupElementStrings(), expected);
    }

    protected void doTest(String... additionalFiles) {
        List<String> files = new ArrayList<String>();
        for (String file : additionalFiles) {
            if (testDataFileExists(file)) {
                files.add(file);
            }
        }
        files.add(getTestName(false) + ".go");
        Collections.reverse(files);

        myFixture.configureByFiles(files.toArray(new String[files.size()]));
        myFixture.complete(CompletionType.BASIC);
        myFixture.checkResultByFile(getTestName(false) + "_after.go", true);
    }
}
