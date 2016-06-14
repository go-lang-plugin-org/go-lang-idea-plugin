/*
 * Copyright 2013-2016 Sergey Ignatov, Alexander Zolotov, Florin Patan
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

package com.goide.codeInsight.unwrap;

import com.intellij.codeInsight.unwrap.UnwrapHandler;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.LightPlatformCodeInsightTestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class GoUnwrapTestCase extends LightPlatformCodeInsightTestCase {
  private final static String FILE_NAME = "a.go";

  protected void assertUnwrapped(String codeBefore, String expectedCodeAfter) {
    assertUnwrapped(codeBefore, expectedCodeAfter, 0);
  }

  protected void assertUnwrapped(String codeBefore, String expectedCodeAfter, final int option) {
    configureFromFileText(FILE_NAME, createCode(codeBefore));

    UnwrapHandler h = new UnwrapHandler() {
      @Override
      protected void selectOption(List<AnAction> options, Editor editor, PsiFile file) {
        if (options.isEmpty()) return;
        options.get(option).actionPerformed(null);
      }
    };

    h.invoke(getProject(), getEditor(), getFile());

    checkResultByText(createCode(expectedCodeAfter));
  }

  protected static void assertOptions(String code, String... expectedOptions) {
    configureFromFileText(FILE_NAME, createCode(code));

    final List<String> actualOptions = new ArrayList<String>();

    UnwrapHandler h = new UnwrapHandler() {
      @Override
      protected void selectOption(List<AnAction> options, Editor editor, PsiFile file) {
        for (AnAction each : options) {
          actualOptions.add(each.getTemplatePresentation().getText());
        }
      }
    };

    h.invoke(getProject(), getEditor(), getFile());
    assertOrderedEquals(actualOptions, Arrays.asList(expectedOptions));
  }

  private static String createCode(String codeBefore) {
    StringBuilder result = new StringBuilder();
    for (String line : StringUtil.tokenize(codeBefore, "\n")) {
      result.append("\t").append(line).append("\n");
    }
    String resultString = result.toString();
    return "package main\n" +
           "func main() {\n" +
           (resultString.isEmpty() ? codeBefore : resultString) +
           "}";
  }
}
