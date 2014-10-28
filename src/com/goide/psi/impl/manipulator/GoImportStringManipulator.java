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
    if (!StringUtil.isQuotedString(element.getText())) {
      throw new IllegalStateException("import string should be quoted, given: " + element.getText());
    }
  }
}
