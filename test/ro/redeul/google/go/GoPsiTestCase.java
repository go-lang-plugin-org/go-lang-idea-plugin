package ro.redeul.google.go;

import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.PsiTestCase;
import org.junit.Ignore;
import ro.redeul.google.go.lang.psi.GoFile;

import java.io.IOException;

@Ignore
public abstract class GoPsiTestCase extends PsiTestCase {

    protected void addBuiltinPackage(VirtualFile contentRoot) throws IOException {
        VirtualFile builtin = LocalFileSystem.getInstance().findFileByPath("testdata/builtin/builtin.go");

        if ( builtin != null ) {
            createFile(myModule, createChildDirectory(contentRoot, "builtin"), "builtin.go", VfsUtil.loadText(builtin));
        }
    }

    protected String getTestDataPath() {
        return "testdata/" + getTestDataRelativePath();
    }

    protected String getTestDataRelativePath() {
        return "";
    }

    protected  GoFile parse(String content) throws Exception {
        return (GoFile)createFile("temp.go", content);
    }
}
