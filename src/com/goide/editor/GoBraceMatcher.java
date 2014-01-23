package com.goide.editor;

import com.goide.GoParserDefinition;
import com.goide.GoTypes;
import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoBraceMatcher implements PairedBraceMatcher {
  private static final BracePair[] PAIRS = new BracePair[]{
    new BracePair(GoTypes.LBRACE, GoTypes.RBRACE, true),
    new BracePair(GoTypes.LPAREN, GoTypes.RPAREN, false),
    new BracePair(GoTypes.LBRACK, GoTypes.RBRACK, false),
  };

  @Override
  public BracePair[] getPairs() {
    return PAIRS;
  }

  @Override
  public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType type) {
    return GoParserDefinition.COMMENTS.contains(type)
           || GoParserDefinition.WHITESPACES.contains(type)
           || type == GoTypes.SEMICOLON
           || type == GoTypes.COMMA
           || type == GoTypes.RPAREN
           || type == GoTypes.RBRACK
           || type == GoTypes.RBRACE
           || null == type;
  }

  @Override
  public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
    return openingBraceOffset;
  }
}
