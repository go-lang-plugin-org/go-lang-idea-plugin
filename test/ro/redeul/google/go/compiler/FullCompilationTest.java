package ro.redeul.google.go.compiler;

public class FullCompilationTest extends GoCompilerTestCase {

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
                "   fmt.Printf(\"%d\", tools.F())\n" +
                "}\n");

        assertEmpty(make());
        assertOutput("main", "10");
    }

    public void testSimpleMainWithDifferentTargetAndLocalLibrary() throws Exception {
        myFixture.addFileToProject("tools.go",
                "package tools\n" +
                "func F() int {\n" +
                "   return 10\n" +
                "}\n");

        myFixture.addFileToProject("app.go",
                "package main\n" +
                "import \"./tools\"\n" +
                "import \"fmt\"\n" +
                "func main() {\n" +
                "   fmt.Printf(\"%d\", tools.F())\n" +
                "}\n");

        assertEmpty(make());
        assertOutput("app", "10");
    }

    public void testSimpleMainWithMultipleLocalLibrary() throws Exception {
        myFixture.addFileToProject("tools/a.go",
                "package tools\n" +
                "func f() int {\n" +
                "   return 10\n" +
                "}\n");

        myFixture.addFileToProject("tools/b.go",
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
    }

    public void testSimpleMainWithMultipleLocalLibraries() throws Exception {
        myFixture.addFileToProject("tools1/a.go",
                "package tools1\n" +
                "func f() int {\n" +
                "   return 10\n" +
                "}\n");

        myFixture.addFileToProject("tools1/b.go",
                "package tools1\n" +
                "func G() int {\n" +
                "   return f()\n" +
                "}\n");

        myFixture.addFileToProject("tools2/a.go",
                "package tools2\n" +
                "func f() int {\n" +
                "   return 11\n" +
                "}\n");

        myFixture.addFileToProject("tools2/b.go",
                "package tools2\n" +
                "func G() int {\n" +
                "   return f()\n" +
                "}\n");

        myFixture.addFileToProject("main.go",
                "package main\n" +
                "import \"./tools1\"\n" +
                "import \"./tools2\"\n" +
                "import \"fmt\"\n" +
                "func main() {\n" +
                "   fmt.Printf(\"%d-%d\", tools1.G(), tools2.G())\n" +
                "}\n");

        assertEmpty(make());
        assertOutput("main", "10-11");
    }
}
