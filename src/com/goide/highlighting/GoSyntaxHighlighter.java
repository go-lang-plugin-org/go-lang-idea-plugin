package com.goide.highlighting;

import com.goide.GoTokenTypes;
import com.goide.lexer.GoLexer;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.goide.highlighting.GoSyntaxHighlightingColors.*;

public class GoSyntaxHighlighter extends SyntaxHighlighterBase {
  private static final Map<IElementType, TextAttributesKey> ATTRIBUTES = new HashMap<IElementType, TextAttributesKey>();

  static {
    fillMap(ATTRIBUTES, LINE_COMMENT, GoTokenTypes.mSL_COMMENT);
    fillMap(ATTRIBUTES, BLOCK_COMMENT, GoTokenTypes.mML_COMMENT);
    fillMap(ATTRIBUTES, PARENTHESES, GoTokenTypes.pLPAREN, GoTokenTypes.pRPAREN);
    fillMap(ATTRIBUTES, BRACES, GoTokenTypes.pLCURLY, GoTokenTypes.pRCURLY);
    fillMap(ATTRIBUTES, BRACKETS, GoTokenTypes.pLBRACK, GoTokenTypes.pRBRACK);
    fillMap(ATTRIBUTES, BAD_CHARACTER, GoTokenTypes.mWRONG);
    fillMap(ATTRIBUTES, IDENTIFIER, GoTokenTypes.mIDENT);
    fillMap(ATTRIBUTES, DOT, GoTokenTypes.oDOT, GoTokenTypes.oTRIPLE_DOT);
    fillMap(ATTRIBUTES, COLON, GoTokenTypes.oCOLON);
    fillMap(ATTRIBUTES, SEMICOLON, GoTokenTypes.oSEMI);
    fillMap(ATTRIBUTES, COMMA, GoTokenTypes.oCOMMA);
    fillMap(ATTRIBUTES, GoTokenTypes.OPERATORS, OPERATOR);
    fillMap(ATTRIBUTES, GoTokenTypes.KEYWORDS, KEYWORD);
    fillMap(ATTRIBUTES, GoTokenTypes.NUMBERS, NUMBER);
    fillMap(ATTRIBUTES, GoTokenTypes.STRING_LITERALS, STRING);
  }

  @NotNull
  public Lexer getHighlightingLexer() {
    return new GoLexer();
  }

  @NotNull
  public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
    return pack(ATTRIBUTES.get(tokenType));
  }
}