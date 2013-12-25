package com.goide;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

public interface GoTokenTypes {
  IElementType WS = new GoElementType("WHITESPACE");
  IElementType NLS = new GoElementType("WS_NEW_LINES");

  IElementType CHAR = new GoElementType("LITERAL_CHAR");
  IElementType STRING = new GoElementType("LITERAL_STRING");

  IElementType OCT = new GoElementType("LITERAL_OCT");
  IElementType INT = new GoElementType("LITERAL_INT");
  IElementType HEX = new GoElementType("LITERAL_HEX");
  IElementType FLOAT = new GoElementType("LITERAL_FLOAT");
  IElementType FLOAT_I = new GoElementType("LITERAL_IMAGINARY_FLOAT");
  IElementType DECIMAL_I = new GoElementType("LITERAL_IMAGINARY_INTEGER");

  IElementType BREAK = new GoElementType("KEYWORD_BREAK");
  IElementType DEFAULT = new GoElementType("KEYWORD_DEFAULT");
  IElementType PACKAGE = new GoElementType("KEYWORD_PACKAGE");
  IElementType FUNC = new GoElementType("KEYWORD_FUNC");
  IElementType INTERFACE = new GoElementType("KEYWORD_INTERFACE");
  IElementType SELECT = new GoElementType("KEYWORD_SELECT");

  IElementType CASE = new GoElementType("KEYWORD_CASE");
  IElementType DEFER = new GoElementType("KEYWORD_DEFER");
  IElementType GO = new GoElementType("KEYWORD_GO");
  IElementType MAP = new GoElementType("KEYWORD_MAP");

  IElementType CHAN = new GoElementType("KEYWORD_CHAN");

  IElementType STRUCT = new GoElementType("KEYWORD_STRUCT");
  IElementType ELSE = new GoElementType("KEYWORD_ELSE");
  IElementType GOTO = new GoElementType("KEYWORD_GOTO");
  IElementType SWITCH = new GoElementType("KEYWORD_SWITCH");
  IElementType CONST = new GoElementType("KEYWORD_CONST");
  IElementType FALLTHROUGH = new GoElementType("KEYWORD_FALLTHROUGH");

  IElementType IF = new GoElementType("KEYWORD_IF");
  IElementType FOR = new GoElementType("KEYWORD_FOR");
  IElementType RETURN = new GoElementType("KEYWORD_RETURN");
  IElementType IMPORT = new GoElementType("KEYWORD_IMPORT");
  IElementType CONTINUE = new GoElementType("KEYWORD_CONTINUE");

  IElementType RANGE = new GoElementType("KEYWORD_RANGE");
  IElementType TYPE = new GoElementType("KEYWORD_TYPE");
  IElementType VAR = new GoElementType("KEYWORD_VAR");

  IElementType WRONG = new GoElementType("WRONG");

  IElementType LINE_COMMENT = new GoElementType("LINE_COMMENT");
  IElementType MULTILINE_COMMENT = new GoElementType("MULTILINE_COMMENT");

  IElementType IDENT = new GoElementType("IDENTIFIER");

  IElementType LCURLY = new GoElementType("{");
  IElementType RCURLY = new GoElementType("}");
  IElementType LBRACK = new GoElementType("[");
  IElementType RBRACK = new GoElementType("]");
  IElementType LPAREN = new GoElementType("(");
  IElementType RPAREN = new GoElementType(")");

  IElementType SEMI = new GoElementType(";");
  IElementType SEMI_SYNTHETIC = new GoElementType("; (synthetic)");

  IElementType TRIPLE_DOT = new GoElementType("...");
  IElementType DOT = new GoElementType(".");
  IElementType COLON = new GoElementType(":");
  IElementType COMMA = new GoElementType(",");

  IElementType EQ = new GoElementType("==");
  IElementType ASSIGN = new GoElementType("=");

  IElementType NOT_EQ = new GoElementType("!=");
  IElementType NOT = new GoElementType("!");

  IElementType PLUS_PLUS = new GoElementType("++");
  IElementType PLUS_ASSIGN = new GoElementType("+=");
  IElementType PLUS = new GoElementType("+");

  IElementType MINUS_MINUS = new GoElementType("--");
  IElementType MINUS_ASSIGN = new GoElementType("-=");
  IElementType MINUS = new GoElementType("-");

  IElementType BIT_OR_ASSIGN = new GoElementType("|=");
  IElementType BIT_OR = new GoElementType("|");
  IElementType COND_OR = new GoElementType("||");

  IElementType BIT_CLEAR_ASSIGN = new GoElementType("^&=");
  IElementType BIT_CLEAR = new GoElementType("^&");
  IElementType COND_AND = new GoElementType("&&");

  IElementType BIT_AND_ASSIGN = new GoElementType("&=");
  IElementType BIT_AND = new GoElementType("&");

  IElementType BIT_XOR_ASSIGN = new GoElementType("^=");
  IElementType BIT_XOR = new GoElementType("^");

  IElementType MUL_ASSIGN = new GoElementType("*=");
  IElementType MUL = new GoElementType("*");

  IElementType QUOTIENT_ASSIGN = new GoElementType("/=");
  IElementType QUOTIENT = new GoElementType("/");

  IElementType REMAINDER_ASSIGN = new GoElementType("%=");
  IElementType REMAINDER = new GoElementType("%");

  IElementType SEND_CHANNEL = new GoElementType("<-");
  IElementType SHIFT_LEFT_ASSIGN = new GoElementType("<<=");
  IElementType SHIFT_LEFT = new GoElementType("<<");

  IElementType SHIFT_RIGHT_ASSIGN = new GoElementType(">>=");
  IElementType SHIFT_RIGHT = new GoElementType(">>");

  IElementType LESS_OR_EQUAL = new GoElementType("<=");
  IElementType LESS = new GoElementType("<");

  IElementType GREATER_OR_EQUAL = new GoElementType(">=");
  IElementType GREATER = new GoElementType(">");

  IElementType VAR_ASSIGN = new GoElementType(":=");

  TokenSet WHITESPACES = TokenSet.create(WS, NLS);
  TokenSet COMMENTS = TokenSet.create(LINE_COMMENT, MULTILINE_COMMENT);
  TokenSet STRING_LITERALS = TokenSet.create(CHAR, STRING);
}