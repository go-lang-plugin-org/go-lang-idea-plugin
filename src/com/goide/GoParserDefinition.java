package com.goide;

import com.goide.lexer.GoLexer;
import com.goide.parser.GoParser;
import com.goide.psi.GoFile;
import com.goide.psi.GoTokenType;
import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

import static com.goide.GoTypes.*;

public class GoParserDefinition implements ParserDefinition {
  public static final IElementType GO_LINE_COMMENT = new GoTokenType("GO_LINE_COMMENT");
  public static final IElementType GO_MULTILINE_COMMENT = new GoTokenType("GO_MULTILINE_COMMENT");

  public static final IElementType GO_WS = new GoTokenType("GO_WHITESPACE");
  public static final IElementType GO_NLS = new GoTokenType("GO_WS_NEW_LINES");

  public static final TokenSet WHITESPACES = TokenSet.create(GO_WS, GO_NLS);
  public static final TokenSet COMMENTS = TokenSet.create(GO_LINE_COMMENT, GO_MULTILINE_COMMENT);
  public static final TokenSet STRING_LITERALS = TokenSet.create(GO_STRING); // todo: leave GO_CHAR in lexer
  public static final TokenSet NUMBERS = TokenSet.create(GO_INT, GO_FLOAT, GO_IMAGINARY); // todo: GO_HEX, GO_OCT,
  public static final TokenSet KEYWORDS = TokenSet.create(GO_PACKAGE, GO_IMPORT, GO_BREAK, GO_CASE, GO_CHAN, GO_CONST, GO_CONTINUE, GO_DEFAULT, GO_DEFER,
      GO_ELSE, GO_FALLTHROUGH, GO_FOR, GO_FUNC, GO_GO, GO_GOTO, GO_IF, GO_IMPORT, GO_INTERFACE, GO_MAP, GO_PACKAGE, GO_RANGE, GO_RETURN,
      GO_SELECT, GO_STRUCT, GO_SWITCH, GO_TYPE, GO_VAR);
  public static final TokenSet OPERATORS = TokenSet.create(GO_EQ, GO_ASSIGN, GO_NOT_EQ, GO_NOT, GO_PLUS_PLUS,
      GO_PLUS_ASSIGN, GO_PLUS, GO_MINUS_MINUS, GO_MINUS_ASSIGN, GO_MINUS, GO_COND_OR, GO_BIT_OR_ASSIGN, GO_BIT_OR, GO_BIT_CLEAR_ASSIGN,
      GO_BIT_CLEAR, GO_COND_AND, GO_BIT_AND_ASSIGN, GO_BIT_AND, GO_SHIFT_LEFT_ASSIGN, GO_SHIFT_LEFT, GO_SEND_CHANNEL, GO_LESS_OR_EQUAL,
      GO_LESS, GO_BIT_XOR_ASSIGN, GO_BIT_XOR, GO_MUL_ASSIGN, GO_MUL, GO_QUOTIENT_ASSIGN, GO_QUOTIENT, GO_REMAINDER_ASSIGN,
      GO_REMAINDER, GO_SHIFT_RIGHT_ASSIGN, GO_SHIFT_RIGHT, GO_GREATER_OR_EQUAL, GO_GREATER, GO_VAR_ASSIGN);

  @NotNull
  @Override
  public Lexer createLexer(Project project) {
    return new GoLexer();
  }

  @Override
  public PsiParser createParser(Project project) {
    return new GoParser();
  }

  @Override
  public IFileElementType getFileNodeType() {
    return GoElementTypes.FILE;
  }

  @NotNull
  @Override
  public TokenSet getWhitespaceTokens() {
    return WHITESPACES;
  }

  @NotNull
  @Override
  public TokenSet getCommentTokens() {
    return COMMENTS;
  }

  @NotNull
  @Override
  public TokenSet getStringLiteralElements() {
    return STRING_LITERALS;
  }

  @NotNull
  @Override
  public PsiElement createElement(ASTNode node) {
    return GoTypes.Factory.createElement(node);
  }

  @Override
  public PsiFile createFile(FileViewProvider viewProvider) {
    return new GoFile(viewProvider);
  }

  @Override
  public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
    return SpaceRequirements.MAY;
  }
}
