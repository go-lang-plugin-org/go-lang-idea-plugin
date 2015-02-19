/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
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

import com.goide.psi.impl.GoStringLiteralImpl;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * @author stuartcarnie
 */
public class GoStringManipulator extends AbstractElementManipulator<GoStringLiteralImpl> {
  @NotNull
  public static TextRange getStringTokenRange(final GoStringLiteralImpl element) {
    return TextRange.from(1, element.getTextLength() - 2);
  }

  @Override
  public GoStringLiteralImpl handleContentChange(@NotNull GoStringLiteralImpl literal, @NotNull TextRange range, String newContent)
    throws IncorrectOperationException {
    final String oldText = literal.getText();
    final String newText = oldText.substring(0, range.getStartOffset()) + newContent + oldText.substring(range.getEndOffset());
    return literal.updateText(newText);
  }

  @NotNull
  @Override
  public TextRange getRangeInElement(@NotNull final GoStringLiteralImpl element) {
    return getStringTokenRange(element);
  }
}
