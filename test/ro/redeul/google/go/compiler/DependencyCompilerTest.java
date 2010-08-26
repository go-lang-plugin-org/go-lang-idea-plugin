package ro.redeul.google.go.compiler;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.PathUtil;
import org.testng.annotations.Test;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 25, 2010
 * Time: 1:50:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class DependencyCompilerTest extends GoCompilerTestBase {

//    @Override
//    protected String getRelativeDataPath() {
//        return super.getRelativeDataPath() + File.separator + "dependency";
//    }

    @Test
    public void testHannibal() throws Exception {
//        List<VirtualFile> roots = Arrays.asList(ModuleRootManager.getInstance(myModule).getSourceRoots());
//        for (VirtualFile root : roots) {
//            root.refresh(false, true);
//        }

        myFixture.addFileToProject("main.go",
                "package main\n" +
                "import \"fmt\"\n" +
                "func main() {\n" +
                "   fmt.Printf(\"239\")\n" +
                "}\n");

        assertEmpty(make());
        assertOutput("main", "239");
    }

    private void addSourceFileToProject(final String fileName, final String fileContent) {
        VirtualFile virtualFile[] = ModuleRootManager.getInstance(myModule).getSourceRoots();


        final VirtualFile sourceRoot = virtualFile[0];

        final String parentPathParts[] = PathUtil.getParentPath(fileName).split("/");

        VirtualFile file = ApplicationManager.getApplication().runWriteAction(new Computable<VirtualFile>() {
            public VirtualFile compute() {
                VirtualFile root = sourceRoot;
                try {
                    for (String parentPathPart : parentPathParts) {
                        if ( parentPathPart.length() != 0 ) {
                            VirtualFile child = root.findChild(parentPathPart);
                            if (child != null) {
                                root = child;
                            } else {
                                root = root.createChildDirectory(this, parentPathPart);
                            }
                        }
                    }

                    VirtualFile file = root.findChild(PathUtil.getFileName(fileName));
                    if (file == null) {
                        file = root.createChildData(null, PathUtil.getFileName(fileName));
                    }

                    VfsUtil.saveText(file, fileContent);
                    return file;

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
