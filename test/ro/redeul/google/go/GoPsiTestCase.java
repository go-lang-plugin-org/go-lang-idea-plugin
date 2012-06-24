package ro.redeul.google.go;

import java.io.File;
import java.io.IOException;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.PsiTestCase;
import com.intellij.util.FilteringProcessor;
import com.intellij.util.Processor;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.util.GoTestUtils;

public abstract class GoPsiTestCase extends PsiTestCase {

    protected String getTestDataPath() {
        return GoTestUtils.getTestDataPath() + getTestDataRelativePath();
    }

    protected String getTestDataRelativePath() {
        return "";
    }

    protected  GoFile parse(String content) throws Exception {
        return (GoFile)createFile("temp.go", content);
    }

}
