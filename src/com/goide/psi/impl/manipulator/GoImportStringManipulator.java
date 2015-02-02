/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov
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

package com.goide.psi.impl.manipulator;

import com.goide.psi.GoImportString;
import com.goide.psi.impl.GoElementFactory;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public class GoImportStringManipulator extends AbstractElementManipulator<GoImportString> {
  @NotNull
  @Override
  public GoImportString handleContentChange(@NotNull GoImportString string, @NotNull TextRange range, String s) throws IncorrectOperationException {
    String newPackage = range.replace(string.getText(), s);
    checkQuoted(string);
    return (GoImportString)string.replace(GoElementFactory.createImportString(string.getProject(), newPackage));
  }

  @NotNull
  @Override
  public TextRange getRangeInElement(@NotNull GoImportString element) {
    checkQuoted(element);
    return TextRange.create(1, element.getTextLength() - 1);
  }

  private static void checkQuoted(@NotNull GoImportString element) {
    String text = element.getText();
    if (!StringUtil.isQuotedString(text) &&
        (text.length() < 2 || !StringUtil.startsWithChar(text, '\'') || !StringUtil.endsWithChar(text, '\''))) {
      throw new IllegalStateException("import string should be quoted, given: " + text);
    }
  }
}
