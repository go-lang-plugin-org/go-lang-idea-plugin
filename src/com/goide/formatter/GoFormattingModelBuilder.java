/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov
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

package com.goide.formatter;

import com.goide.GoLanguage;
import com.goide.GoParserDefinition;
import com.goide.psi.GoStatement;
import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static com.goide.GoTypes.*;

public class GoFormattingModelBuilder implements FormattingModelBuilder {
  @NotNull
  @Override
  public FormattingModel createModel(@NotNull PsiElement element, @NotNull CodeStyleSettings settings) {
    Block block = new GoFormattingBlock(element.getNode(), null, Indent.getNoneIndent(), null, settings, createSpacingBuilder(settings));
    return FormattingModelProvider.createFormattingModelForPsiFile(element.getContainingFile(), block, settings);
  }

  private static SpacingBuilder createSpacingBuilder(@NotNull CodeStyleSettings settings) {
    return new SpacingBuilder(settings, GoLanguage.INSTANCE)
      .before(COMMA).spaceIf(false)
      .after(COMMA).spaceIf(true)
      .before(SEMICOLON).spaceIf(false)
      .after(SEMICOLON).spaceIf(true)
      .beforeInside(DOT, IMPORT_SPEC).none()
      .afterInside(DOT, IMPORT_SPEC).spaces(1)
      .around(DOT).none()
      .around(ASSIGN).spaces(1)
      .around(VAR_ASSIGN).spaces(1)
      .aroundInside(MUL, POINTER_TYPE).none()
      .before(ARGUMENT_LIST).none()
      .around(BUILTIN_ARGS).none()
      .afterInside(LPAREN, ARGUMENT_LIST).none()
      .beforeInside(RPAREN, ARGUMENT_LIST).none()
      .before(SIGNATURE).none()
      .afterInside(LPAREN, TYPE_ASSERTION_EXPR).none()
      .beforeInside(RPAREN, TYPE_ASSERTION_EXPR).none()
      .afterInside(LPAREN, PARAMETERS).none()
      .beforeInside(RPAREN, PARAMETERS).none()
      .afterInside(LPAREN, RESULT).none()
      .beforeInside(RPAREN, RESULT).none()
      .afterInside(LPAREN, IMPORT_DECLARATION).lineBreakInCode()
      .beforeInside(RPAREN, IMPORT_DECLARATION).lineBreakInCode()
      .between(PARAMETERS, RESULT).spaces(1)
      .before(BLOCK).spaces(1)
      .after(FUNC).spaces(1)
      .after(PACKAGE).spaces(1)
      .after(IMPORT).spaces(1)
      .after(CONST).spaces(1)
      .after(VAR).spaces(1)
      .after(STRUCT).spaces(1)
      .after(RETURN).spaces(1)
      .after(GO).spaces(1)
      .after(DEFER).spaces(1)
      .after(FALLTHROUGH).spaces(1)
      .after(GOTO).spaces(1)
      .after(CONTINUE).spaces(1)
      .after(BREAK).spaces(1)
      .after(SELECT).spaces(1)
      .after(FOR).spaces(1)
      .after(IF).spaces(1)
      .after(ELSE).spaces(1)
      .after(CASE).spaces(1)
      .after(RANGE).spaces(1)
      .after(SWITCH).spaces(1)
      .afterInside(SEND_CHANNEL, UNARY_EXPR).none()
      .aroundInside(SEND_CHANNEL, SEND_STATEMENT).spaces(1)
      .afterInside(CHAN, CHANNEL_TYPE).spaces(1)
      .afterInside(MAP, MAP_TYPE).none()
      .aroundInside(LBRACK, MAP_TYPE).none()
      .aroundInside(RBRACK, MAP_TYPE).none()
      .beforeInside(LITERAL_VALUE, COMPOSITE_LIT).none()
      .afterInside(LBRACE, LITERAL_VALUE).none()
      .beforeInside(LBRACE, LITERAL_VALUE).none()
      .afterInside(BIT_AND, UNARY_EXPR).none()
      .beforeInside(TYPE, VAR_SPEC).spaces(1)
      .after(GoParserDefinition.LINE_COMMENT).lineBreakInCodeIf(true)
      .after(GoParserDefinition.MULTILINE_COMMENT).lineBreakInCodeIf(true)
      ;
  }

  @Nullable
  @Override
  public TextRange getRangeAffectingIndent(PsiFile file, int offset, ASTNode elementAtOffset) {
    return null;
  }

  public static class GoFormattingBlock implements ASTBlock {
    public static final TokenSet BLOCKS_TOKEN_SET = TokenSet.create(
      BLOCK,
      STRUCT_TYPE,
      INTERFACE_TYPE,
      SELECT_STATEMENT,
      EXPR_SWITCH_STATEMENT,
      TYPE_SWITCH_STATEMENT,
      LITERAL_VALUE
    );

    public static final TokenSet BRACES_TOKEN_SET = TokenSet.create(
      LBRACE,
      RBRACE,
      LBRACK,
      RBRACK,
      LPAREN,
      RPAREN
    );

    private final ASTNode myNode;
    private final Alignment myAlignment;
    private final Indent myIndent;
    private final Wrap myWrap;
    private final CodeStyleSettings mySettings;
    private final SpacingBuilder mySpacingBuilder;
    private List<Block> mySubBlocks;

    public GoFormattingBlock(ASTNode node, Alignment alignment, Indent indent, Wrap wrap, CodeStyleSettings settings,
                             SpacingBuilder spacingBuilder) {
      myNode = node;
      myAlignment = alignment;
      myIndent = indent;
      myWrap = wrap;
      mySettings = settings;
      mySpacingBuilder = spacingBuilder;
    }

    @Override
    public ASTNode getNode() {
      return myNode;
    }

    @NotNull
    @Override
    public TextRange getTextRange() {
      return myNode.getTextRange();
    }

    @Override
    public Wrap getWrap() {
      return myWrap;
    }

    @Override
    public Indent getIndent() {
      return myIndent;
    }

    @Override
    public Alignment getAlignment() {
      return myAlignment;
    }

    @NotNull
    @Override
    public List<Block> getSubBlocks() {
      if (mySubBlocks == null) {
        mySubBlocks = buildSubBlocks();
      }
      return ContainerUtil.newArrayList(mySubBlocks);
    }

    private List<Block> buildSubBlocks() {
      List<Block> blocks = ContainerUtil.newArrayList();
      for (ASTNode child = myNode.getFirstChildNode(); child != null; child = child.getTreeNext()) {
        IElementType childType = child.getElementType();
        if (child.getTextRange().getLength() == 0) continue;
        if (childType == TokenType.WHITE_SPACE) continue;
        blocks.add(buildSubBlock(child));
      }
      return Collections.unmodifiableList(blocks);
    }

    @Nullable
    private Block buildSubBlock(@NotNull ASTNode child) {
      Indent indent = calcIndent(child);
      return new GoFormattingBlock(child, null, indent, null, mySettings, mySpacingBuilder);
    }

    private Indent calcIndent(@NotNull ASTNode child) {
      IElementType parentType = myNode.getElementType();
      IElementType type = child.getElementType();
      if (type == SWITCH_START) return Indent.getNoneIndent();
      if (parentType == BLOCK && type == SELECT_STATEMENT) return Indent.getNoneIndent();
      if (parentType == SELECT_STATEMENT && type == RBRACE) return Indent.getNormalIndent();
      if (BLOCKS_TOKEN_SET.contains(parentType)) return indentIfNotBrace(child);
      if (parentType == IMPORT_DECLARATION && type == IMPORT_SPEC) return Indent.getNormalIndent();
      if (parentType == CONST_DECLARATION && type == CONST_SPEC) return Indent.getNormalIndent();
      if (parentType == VAR_DECLARATION && type == VAR_SPEC) return Indent.getNormalIndent();
      if (parentType == COMM_CLAUSE && child.getPsi() instanceof GoStatement) return Indent.getNormalIndent();
      return Indent.getNoneIndent();
    }

    private static Indent indentIfNotBrace(@NotNull ASTNode child) {
      return BRACES_TOKEN_SET.contains(child.getElementType()) ? Indent.getNoneIndent() : Indent.getNormalIndent();
    }

    @Override
    public Spacing getSpacing(Block child1, @NotNull Block child2) {
      return mySpacingBuilder.getSpacing(this, child1, child2);
    }

    @NotNull
    @Override
    public ChildAttributes getChildAttributes(int newChildIndex) {
      Indent childIndent = Indent.getNoneIndent();
      IElementType parentType = myNode.getElementType();
      if (BLOCKS_TOKEN_SET.contains(parentType) ||
          parentType == IMPORT_DECLARATION ||
          parentType == CONST_DECLARATION ||
          parentType == VAR_DECLARATION) {
        childIndent = Indent.getNormalIndent();
      }
      return new ChildAttributes(childIndent, null);
    }

    @Override
    public boolean isIncomplete() {
      return false;
    }

    @Override
    public boolean isLeaf() {
      return myNode.getFirstChildNode() == null;
    }
  }
}
