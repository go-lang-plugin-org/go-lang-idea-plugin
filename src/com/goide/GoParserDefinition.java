package com.goide;

import com.goide.lexer.GoLexer;
import com.goide.psi.impl.GoCompositeElementImpl;
import com.goide.psi.GoFile;
import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

public class GoParserDefinition implements ParserDefinition {
  @NotNull
  @Override
  public Lexer createLexer(Project project) {
    return new GoLexer();
  }

  @Override
  public PsiParser createParser(Project project) {
    return new GoParserStub();
  }

  @Override
  public IFileElementType getFileNodeType() {
    return GoElementTypes.FILE;
  }

  @NotNull
  @Override
  public TokenSet getWhitespaceTokens() {
    return GoTokenTypes.WHITESPACES;
  }

  @NotNull
  @Override
  public TokenSet getCommentTokens() {
    return GoTokenTypes.COMMENTS;
  }

  @NotNull
  @Override
  public TokenSet getStringLiteralElements() {
    return GoTokenTypes.STRING_LITERALS;
  }

  @NotNull
  @Override
  public PsiElement createElement(ASTNode node) {
    return new GoCompositeElementImpl(node.getElementType());
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
