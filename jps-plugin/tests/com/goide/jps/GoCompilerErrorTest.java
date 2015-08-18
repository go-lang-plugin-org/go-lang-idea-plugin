/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Florin Patan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.goide.jps;

import com.goide.jps.builder.GoCompilerError;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.testFramework.UsefulTestCase;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoCompilerErrorTest extends UsefulTestCase {
  public void testSyntaxError_1() throws Exception {
    doTest("/some/root/path", "./some_source.go:6: newline in string",
           "newline in string", CompilerMessageCategory.ERROR, "/some/root/path/some_source.go", 6);
  }

  public void testSyntaxError_2() throws Exception {
    doTest("/some/root/path", "./some_source.go:7: syntax error: unexpected }, expecting )",
           "syntax error: unexpected }, expecting )", CompilerMessageCategory.ERROR, "/some/root/path/some_source.go", 7);
  }

  public void testBuildError() throws Exception {
    doTest("/some/root/path", "can't load package: package /package/name: import \"/package/name\": cannot import absolute path\n",
           "can't load package: package /package/name: import \"/package/name\": cannot import absolute path\n",
           CompilerMessageCategory.ERROR, null, -1);
  }

  private static void doTest(@NotNull String rootPath,
                             @NotNull String message,
                             @NotNull String expectedMessage,
                             @NotNull CompilerMessageCategory expectedCategory,
                             @Nullable String expectedPath,
                             long expectedLine) {
    GoCompilerError error = GoCompilerError.create(rootPath, message);
    assertNotNull(error);
    assertEquals(expectedMessage, error.getErrorMessage());
    assertEquals(expectedCategory, error.getCategory());
    String url = error.getUrl();
    if (expectedPath == null) {
      assertNull(url);
    }
    else {
      String expected = VfsUtilCore.pathToUrl(expectedPath);
      assertEquals(PathUtil.toSystemIndependentName(expected), PathUtil.toSystemIndependentName(url));
    }
    assertEquals(expectedLine, error.getLine());
  }
}
