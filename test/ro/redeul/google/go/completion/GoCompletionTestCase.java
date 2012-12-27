package ro.redeul.google.go.completion;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.AdapterProcessor;
import com.intellij.util.CommonProcessors;
import com.intellij.util.FilteringProcessor;
import com.intellij.util.Function;
import ro.redeul.google.go.GoLightCodeInsightFixtureTestCase;

public abstract class GoCompletionTestCase
    extends GoLightCodeInsightFixtureTestCase {

    protected String getTestDataRelativePath() {
        return "psi/completion/";
    }

    protected void doTestVariants() {

        LocalFileSystem fileSystem = LocalFileSystem.getInstance();
        final VirtualFile testRoot =
            fileSystem.findFileByPath(
                getTestDataPath() + File.separator + getTestName(false));

        List<String> files = new LinkedList<String>();

        if (fileSystem.findFileByPath(getTestDataPath() + File.separator + "builtin.go") != null) {
            files.add("builtin.go");
        }

        if (testRoot != null && testRoot.isDirectory()) {
            VfsUtil.processFilesRecursively(
                testRoot,
                new FilteringProcessor<VirtualFile>(
                    new Condition<VirtualFile>() {
                        @Override
                        public boolean value(VirtualFile file) {
                            return !file.isDirectory() &&
                                !file.getName().equals(
                                    getTestName(false) + ".go");
                        }
                    },
                    new AdapterProcessor<VirtualFile, String>(
                        new CommonProcessors.CollectProcessor<String>(files),
                        new Function<VirtualFile, String>() {
                            @Override
                            public String fun(VirtualFile virtualFile) {
                                return VfsUtil.getRelativePath(virtualFile,
                                                               testRoot.getParent(),
                                                               File.separatorChar);
                            }
                        }
                    )
                ));

            files.add(getTestName(false) + File.separator + getTestName(false) + ".go");
        } else {
            files.add(getTestName(false) + ".go");
        }

        Collections.reverse(files);
        myFixture.configureByFiles(files.toArray(new String[files.size()]));
        myFixture.completeBasic();
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

    protected void doTest() {
        myFixture.configureByFile(getTestName(false) + ".go");
        myFixture.complete(CompletionType.BASIC);
        myFixture.checkResultByFile(getTestName(false) + "_after.go", true);
    }
}
