package ro.redeul.google.go;

import java.io.IOException;

import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.PsiTestCase;
import com.intellij.util.FilteringProcessor;
import com.intellij.util.Processor;
import org.junit.Ignore;
import ro.redeul.google.go.lang.psi.GoFile;

@Ignore
public abstract class GoSmokeTestCase extends PsiTestCase {

    public static String sourceRoot = "/Users/mtoader/Work/Personal/go/src/";

    public void testGoDefs() {
        VirtualFile file =
            LocalFileSystem.getInstance()
                           .findFileByPath(
                                sourceRoot + "cmd/yacc/yacc.go");
        bibi(file);
    }

    public void testParsingGoSources() throws IOException {
        LocalFileSystem.getInstance();

        final VirtualFile root =
            LocalFileSystem.getInstance().findFileByPath(sourceRoot);

        if (root == null)
            return;

        VfsUtil.processFilesRecursively(
            root,
            new FilteringProcessor<VirtualFile>(
                new Condition<VirtualFile>() {
                    @Override
                    public boolean value(VirtualFile virtualFile) {
                        return virtualFile.getName().endsWith(".go");
                    }
                },
                new Processor<VirtualFile>() {
                    @Override
                    public boolean process(VirtualFile virtualFile) {

                        bibi(virtualFile);

                        return true;
                    }
                }
            )
        );
    }

    private void bibi(VirtualFile virtualFile) {
        System.out.println("Processing: " + virtualFile.getCanonicalPath());
        try {
            myFile = createFile(myModule,
                                "test.go",
                                VfsUtil.loadText(virtualFile));

            ((GoFile)myFile).getMethods();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Done processing: " + virtualFile.getCanonicalPath());
    }
}
