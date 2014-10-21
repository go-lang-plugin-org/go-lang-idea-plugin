package ro.redeul.google.go.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess;
import ro.redeul.google.go.GoLightCodeInsightFixtureTestCase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

    protected void doTestVariants(String... additionalFiles) throws IOException {
        // @TODO Fix this. Hack way to allow test to run
        try {
            String testDataRootDir = new File(GoCompletionTestCase.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "../../../" + testDataRoot).getCanonicalPath();
            VfsRootAccess.allowRootAccess(testDataRootDir);
        } catch (IOException ignored) {}

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
        addBuiltinPackage();

        // find the expected outcome
        String fileText = myFixture.getFile().getText();
        List<String> expected = new ArrayList<String>();
        int dataPos = fileText.indexOf("/**---");
        if (dataPos != -1) {
            String[] parts = fileText.substring(dataPos + 6).trim().split("[\r\n]+");
            for (String part : parts) {
                part = part.trim();
                if (!part.isEmpty()) {
                    expected.add(part);
                }
            }
        }

        // do the completion
        myFixture.completeBasic();


        // validate assertions
        List<String> lookupElementStrings = myFixture.getLookupElementStrings();
        if (lookupElementStrings.get(0).equals(""))
            lookupElementStrings = lookupElementStrings.subList(1, lookupElementStrings.size());
        assertOrderedEquals(lookupElementStrings, expected);
    }

    protected void doTest(String... additionalFiles) throws IOException {
        List<String> files = new ArrayList<String>();
        for (String file : additionalFiles) {
            if (testDataFileExists(file)) {
                files.add(file);
            }
        }
        files.add(getTestName(false) + ".go");
        Collections.reverse(files);


        myFixture.configureByFiles(files.toArray(new String[files.size()]));
        addBuiltinPackage();
        myFixture.complete(CompletionType.BASIC);
        myFixture.checkResultByFile(getTestName(false) + "_after.go", true);
    }
}
