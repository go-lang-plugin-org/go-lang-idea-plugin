package com.goide;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.LightProjectDescriptor;
import org.jetbrains.annotations.NotNull;

public class GoDocumentationProviderTest extends GoCodeInsightFixtureTestCase {
  @Override
  protected LightProjectDescriptor getProjectDescriptor() {
    return createMockProjectDescriptor();
  }

  public void testPrintln() {
    doTest("package a; import \"fmt\"; func foo() {fmt.Printl<caret>n}",
           "<pre> <b>func Println(a ...interface{}) (n int, err error)</b><br/>" +
           " Println formats using the default formats for its operands and writes to standard output.\n" +
           " Spaces are always added between operands and a newline is appended.\n" +
           " It returns the number of bytes written and any write error encountered.</pre>");
  }

  public void testFprintln() {
    doTest("package a; import \"fmt\"; func foo() {fmt.Fprintl<caret>n(\"Hello\")}",
           "<pre> <b>func Fprintln(w io.Writer, a ...interface{}) (n int, err error)</b><br/>" +
           " Fprintln formats using the default formats for its operands and writes to w.\n" +
           " Spaces are always added between operands and a newline is appended.\n" +
           " It returns the number of bytes written and any write error encountered.</pre>");
  }

  public void testVariable() {
    doTest("package a; \n" +
           "// test\n" +
           "var an = 1; func foo() {a<caret>n}",
           "<pre> test</pre>"
    );
  }

  public void testEscape() {
    doTest("package a; \n" +
           "func _() {Replac<caret>e()}\n" +
           "// If n < 0, there is no limit on the number of replacements.\n" +
           "func Replace(s, old, new string, n int) string {return s}",
           "<pre> <b>func Replace(s string, old string, new string, n int) string</b><br/>" +
           " If n &lt; 0, there is no limit on the number of replacements.</pre>"
    );
  }

  public void testEscapeReturnValues() {
    doTest("package a; \n" +
           "func _() {Replac<caret>e()}\n" +
           "// If n < 0, there is no limit on the number of replacements.\n" +
           "func Replace(s, old, new string, n int) <-chan string {return s}",
           "<pre> <b>func Replace(s string, old string, new string, n int) &lt;-chan string</b><br/>" +
           " If n &lt; 0, there is no limit on the number of replacements.</pre>"
    );
  }

  public void testPackageWithDoc() {
    doTest("package a; import \"fm<caret>t\"",
           "<pre>\n" +
           "Package fmt implements formatted I/O with functions analogous\n" +
           "to C's printf and scanf.  The format 'verbs' are derived from C's but\n" +
           "are simpler.\n" +
           "</pre>");
  }

  public void testPackage() {
    doTest("package a; import \"io<caret>\"",
           "<pre> Package io provides basic interfaces to I/O primitives.\n" +
           " Its primary job is to wrap existing implementations of such primitives,\n" +
           " such as those in package os, into shared public interfaces that\n" +
           " abstract the functionality, plus some other related primitives.\n\n" +
           " Because these interfaces and primitives wrap lower-level operations with\n" +
           " various implementations, unless otherwise informed clients should not\n" +
           " assume they are safe for parallel execution.</pre>");
  }

  public void testTypeResultDefinition() {
    doTest("package a; import \"docs\"; func foo() {docs.Type<caret>Result()}",
           "<pre> <b>func TypeResult(s string) string</b><br/>" +
           " TypeResult func comment</pre>");
  }

  public void testMultilineTypeListDefinition() {
    doTest("package a; import \"docs\"; func foo() {docs.Multi<caret>Type()}",
           "<pre> <b>func MultiType(demo interface{}, err error) ([]interface{}, error)</b><br/>" +
           " MultiType is a function like all other functions</pre>");
  }

  private void doTest(@NotNull String text, @NotNull String expected) {
    myFixture.configureByText("test.go", text);
    int caretPosition = myFixture.getEditor().getCaretModel().getOffset();
    PsiReference ref = myFixture.getFile().findReferenceAt(caretPosition);
    assertNotNull(ref);
    PsiElement resolve = ref.resolve();
    assertNotNull(resolve);
    assertEquals(expected, new GoDocumentationProvider().generateDoc(resolve, null));
  }
}
