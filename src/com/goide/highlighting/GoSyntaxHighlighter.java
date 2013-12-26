package com.goide.highlighting;

import com.goide.GoParserDefinition;
import com.goide.GoTypes;
import com.goide.lexer.GoLexer;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.goide.highlighting.GoSyntaxHighlightingColors.*;

public class GoSyntaxHighlighter extends SyntaxHighlighterBase {
  private static final Map<IElementType, TextAttributesKey> ATTRIBUTES = new HashMap<IElementType, TextAttributesKey>();

  static {
    fillMap(ATTRIBUTES, LINE_COMMENT, GoParserDefinition.LINE_COMMENT);
    fillMap(ATTRIBUTES, BLOCK_COMMENT, GoParserDefinition.MULTILINE_COMMENT);
    fillMap(ATTRIBUTES, PARENTHESES, GoTypes.LPAREN, GoTypes.RPAREN);
    fillMap(ATTRIBUTES, BRACES, GoTypes.LBRACE, GoTypes.RBRACE);
    fillMap(ATTRIBUTES, BRACKETS, GoTypes.LBRACK, GoTypes.RBRACK);
    fillMap(ATTRIBUTES, BAD_CHARACTER, TokenType.BAD_CHARACTER);
    fillMap(ATTRIBUTES, IDENTIFIER, GoTypes.IDENTIFIER);
    fillMap(ATTRIBUTES, DOT, GoTypes.DOT, GoTypes.TRIPLE_DOT);
    fillMap(ATTRIBUTES, COLON, GoTypes.COLON);
    fillMap(ATTRIBUTES, SEMICOLON, GoTypes.SEMICOLON);
    fillMap(ATTRIBUTES, COMMA, GoTypes.COMMA);
    fillMap(ATTRIBUTES, GoParserDefinition.OPERATORS, OPERATOR);
    fillMap(ATTRIBUTES, GoParserDefinition.KEYWORDS, KEYWORD);
    fillMap(ATTRIBUTES, GoParserDefinition.NUMBERS, NUMBER);
    fillMap(ATTRIBUTES, GoParserDefinition.STRING_LITERALS, STRING);
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