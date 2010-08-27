package ro.redeul.google.go.compiler;

import org.testng.annotations.Test;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 25, 2010
 * Time: 1:50:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class DependencyCompilerTest extends GoCompilerTestBase {

    @Test
    public void testSimpleApplication() throws Exception {
        myFixture.addFileToProject("main.go",
                "package main\n" +
                "import \"fmt\"\n" +
                "func main() {\n" +
                "   fmt.Printf(\"239\")\n" +
                "}\n");

        assertEmpty(make());
        assertOutput("main", "239");
    }

    @Test
    public void testMultipleSourceFiles() throws Exception {
        myFixture.addFileToProject("main.go",
                "package main\n" +
                "import \"fmt\"\n" +
                "func main() {\n" +
                "   fmt.Printf(method())\n" +
                "}\n");

        myFixture.addFileToProject("main1.go",
                "package main\n" +
                "func method() string {\n" +
                "   return \"239\" \n" +
                "}\n");

        assertEmpty(make());
        assertOutput("main", "239");
    }

    @Test
    public void testSimpleMainWithLocalLibrary() throws Exception {
        myFixture.addFileToProject("tools.go",
                "package tools\n" +
                "func F() int {\n" +
                "   return 10\n" +
                "}\n");

        myFixture.addFileToProject("main.go",
                "package main\n" +
                "import \"./tools\"\n" +
                "import \"fmt\"\n" +
                "func main() {\n" +
                "   fmt.Printf(tools.F())\n" +
                "}\n");

        assertEmpty(make());
        assertOutput("main", "10");
    }
}
