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
           "<pre> Println formats using the default formats for its operands and writes to standard output.<br/>" +
           " Spaces are always added between operands and a newline is appended.<br/>" +
           " It returns the number of bytes written and any write error encountered.</pre>");
  }
  
  public void testFprintln() {
    doTest("package a; import \"fmt\"; func foo() {fmt.Fprintl<caret>n(\"Hello\")}", 
    "<pre> Fprintln formats using the default formats for its operands and writes to w.<br/> Spaces are always added between operands and a newline is appended.<br/> It returns the number of bytes written and any write error encountered.</pre>");
  }
  
  public void testVariable() {
    doTest("package a; \n" +
           "// test\n" +
           "var an = 1; func foo() {a<caret>n}",
           "<pre> test</pre>"
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
           "<pre> Package io provides basic interfaces to I/O primitives.<br/> Its primary job is to wrap existing implementations of such primitives,<br/> such as those in package os, into shared public interfaces that<br/> abstract the functionality, plus some other related primitives.<br/><br/> Because these interfaces and primitives wrap lower-level operations with<br/> various implementations, unless otherwise informed clients should not<br/> assume they are safe for parallel execution.</pre>");
  }

  private void doTest(@NotNull String text, @NotNull String expected) {
    myFixture.configureByText("test.go", text);
    int caretPosition = myFixture.getEditor().getCaretModel().getOffset();
    PsiReference ref =  myFixture.getFile().findReferenceAt(caretPosition);
    assertNotNull(ref);
    PsiElement resolve = ref.resolve();
    assertNotNull(resolve);
    assertEquals(expected, new GoDocumentationProvider().generateDoc(resolve, null));
  }
}