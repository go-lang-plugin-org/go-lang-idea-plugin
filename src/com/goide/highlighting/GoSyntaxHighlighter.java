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
    fillMap(ATTRIBUTES, LINE_COMMENT, GoTokenTypes.LINE_COMMENT);
    fillMap(ATTRIBUTES, BLOCK_COMMENT, GoTokenTypes.MULTILINE_COMMENT);
    fillMap(ATTRIBUTES, PARENTHESES, GoTokenTypes.LPAREN, GoTokenTypes.RPAREN);
    fillMap(ATTRIBUTES, BRACES, GoTokenTypes.LBRACE, GoTokenTypes.RBRACE);
    fillMap(ATTRIBUTES, BRACKETS, GoTokenTypes.LBRACKET, GoTokenTypes.RBRACKET);
    fillMap(ATTRIBUTES, BAD_CHARACTER, GoTokenTypes.WRONG);
    fillMap(ATTRIBUTES, IDENTIFIER, GoTokenTypes.IDENT);
    fillMap(ATTRIBUTES, DOT, GoTokenTypes.DOT, GoTokenTypes.TRIPLE_DOT);
    fillMap(ATTRIBUTES, COLON, GoTokenTypes.COLON);
    fillMap(ATTRIBUTES, SEMICOLON, GoTokenTypes.SEMICOLON);
    fillMap(ATTRIBUTES, COMMA, GoTokenTypes.COMMA);
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