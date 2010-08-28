package ro.redeul.google.go.compiler;

import com.intellij.psi.PsiFile;
import org.testng.annotations.Test;

public class IncrementalCompilationTest extends GoCompilerTestCase {

    @Test
    public void testSimpleMainWithLocalLibrary() throws Exception {
        PsiFile file = myFixture.addFileToProject("tools.go",
                "package tools\n" +
                "func F() int {\n" +
                "   return 10\n" +
                "}\n");

        myFixture.addFileToProject("main.go",
                "package main\n" +
                "import \"./tools\"\n" +
                "import \"fmt\"\n" +
                "func main() {\n" +
                "   fmt.Printf(\"%d\", tools.F())\n" +
                "}\n");

        assertEmpty(make());
        assertOutput("main", "10");

        touch(file.getVirtualFile(),
                "package tools\n" +
                "func F() int {\n" +
                "   return 11\n" +
                "}\n");

        assertEmpty(make());
        assertOutput("main", "11");
    }

    @Test
    public void testSimpleMainWithMultipleLocalLibrary() throws Exception {
        myFixture.addFileToProject("tools/a.go",
                "package tools\n" +
                "func f() int {\n" +
                "   return 10\n" +
                "}\n");

        PsiFile b = myFixture.addFileToProject("tools/b.go",
                "package tools\n" +
                "func G() int {\n" +
                "   return f()\n" +
                "}\n");

        myFixture.addFileToProject("main.go",
                "package main\n" +
                "import \"./tools\"\n" +
                "import \"fmt\"\n" +
                "func main() {\n" +
                "   fmt.Printf(\"%d\", tools.G())\n" +
                "}\n");

        assertEmpty(make());
        assertOutput("main", "10");

        touch(b.getVirtualFile(),
                "package tools\n" +
                "func G() int {\n" +
                "   return f() + f()\n" +
                "}\n");

        assertEmpty(make());
        assertOutput("main", "20");
    }
}
