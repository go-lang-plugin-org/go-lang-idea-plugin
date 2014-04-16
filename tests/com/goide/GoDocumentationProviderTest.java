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
  
  public void testVariable() {
    doTest("package a; \n" +
           "// test\n" +
           "var an = 1; func foo() {a<caret>n}",
           "<pre> test</pre>"
    );
  }

  public void testPackage() {
    doTest("package a; import \"fm<caret>t\"", 
           "<pre> Copyright 2009 The Go Authors. All rights reserved.<br/> Use of this source code is governed by a BSD-style<br/> license that can be found in the LICENSE file.<br/>\n" +
           "Package fmt implements formatted I/O with functions analogous\n" +
           "to C's printf and scanf.  The format 'verbs' are derived from C's but\n" +
           "are simpler.\n" +
           "</pre>");
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