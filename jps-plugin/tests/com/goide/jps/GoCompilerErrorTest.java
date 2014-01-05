package com.goide.jps;

import com.goide.jps.builder.GoCompilerError;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.testFramework.UsefulTestCase;
import org.jetbrains.annotations.NotNull;

public class GoCompilerErrorTest extends UsefulTestCase {
  public void testSyntaxError_1() throws Exception {
    doTest("/some/root/path", "./some_source.go:6: newline in string",
           "newline in string", CompilerMessageCategory.ERROR, "/some/root/path/some_source.go", 6);
  }

  public void testSyntaxError_2() throws Exception {
    doTest("/some/root/path", "./some_source.go:7: syntax error: unexpected }, expecting )",
           "syntax error: unexpected }, expecting )", CompilerMessageCategory.ERROR, "/some/root/path/some_source.go", 7);
  }

  private static void doTest(@NotNull String rootPath,
                             @NotNull String message,
                             String expectedMessage,
                             CompilerMessageCategory expectedCategory,
                             String expectedPath, int expectedLine) {
    GoCompilerError error = GoCompilerError.create(rootPath, message);
    assertNotNull(error);
    assertEquals(expectedMessage, error.getErrorMessage());
    assertEquals(expectedCategory, error.getCategory());
    assertEquals(VfsUtilCore.pathToUrl(expectedPath), error.getUrl());
    assertEquals(expectedLine, error.getLine());
  }
}
