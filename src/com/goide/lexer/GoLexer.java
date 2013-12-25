package com.goide.lexer;

import com.goide.GoTokenTypes;
import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.MergingLexerAdapter;
import com.intellij.psi.tree.TokenSet;

public class GoLexer extends MergingLexerAdapter {
  public GoLexer() {
    super(new FlexAdapter(new _GoLexer()), TokenSet.orSet(GoTokenTypes.COMMENTS, GoTokenTypes.WHITESPACES));
  }
}
