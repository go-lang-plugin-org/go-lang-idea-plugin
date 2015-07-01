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
import com.goide.psi.GoStatement;
import com.goide.psi.GoType;
import com.intellij.formatting.*;
import com.intellij.formatting.alignment.AlignmentStrategy;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static com.goide.GoParserDefinition.LINE_COMMENT;
import static com.goide.GoParserDefinition.MULTILINE_COMMENT;
import static com.goide.GoTypes.*;

public class GoFormattingModelBuilder implements FormattingModelBuilder {
  @NotNull
  @Override
  public FormattingModel createModel(@NotNull PsiElement element, @NotNull CodeStyleSettings settings) {
    Block block = new GoFormattingBlock(element.getNode(), null, Indent.getNoneIndent(), null, settings, createSpacingBuilder(settings));
    return FormattingModelProvider.createFormattingModelForPsiFile(element.getContainingFile(), block, settings);
  }

  @NotNull
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
      .after(LINE_COMMENT).lineBreakInCodeIf(true)
      .after(MULTILINE_COMMENT).lineBreakInCodeIf(true)
      .between(COMM_CASE, COLON).none()
      .afterInside(COLON, COMM_CLAUSE).lineBreakInCode()
      .betweenInside(FIELD_DECLARATION, LINE_COMMENT, STRUCT_TYPE).spaces(1)
      .betweenInside(FIELD_DECLARATION, MULTILINE_COMMENT, STRUCT_TYPE).spaces(1)
      .betweenInside(LBRACE, RBRACE, INTERFACE_TYPE).none()
      .betweenInside(LBRACE, RBRACE, STRUCT_TYPE).none()
      .betweenInside(LBRACK, RBRACK, ARRAY_OR_SLICE_TYPE).none()
      ;
  }

  @Nullable
  @Override
  public TextRange getRangeAffectingIndent(PsiFile file, int offset, ASTNode elementAtOffset) {
    return null;
  }

  public static class GoFormattingBlock extends UserDataHolderBase implements ASTBlock {
    public static final TokenSet BLOCKS_TOKEN_SET = TokenSet.create(
      BLOCK,
      STRUCT_TYPE,
      INTERFACE_TYPE,
      SELECT_STATEMENT,
      EXPR_CASE_CLAUSE,
      TYPE_CASE_CLAUSE,
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
    public static final Key<Alignment> TYPE_ALIGNMENT_INSIDE_STRUCT = Key.create("TYPE_ALIGNMENT_INSIDE_STRUCT");

    @NotNull private final ASTNode myNode;
    @Nullable private final Alignment myAlignment;
    @Nullable private final Indent myIndent;
    @Nullable private final Wrap myWrap;
    @NotNull private final CodeStyleSettings mySettings;
    @NotNull private final SpacingBuilder mySpacingBuilder;
    @Nullable private List<Block> mySubBlocks;

    public GoFormattingBlock(@NotNull ASTNode node,
                             @Nullable Alignment alignment,
                             @Nullable Indent indent,
                             @Nullable Wrap wrap,
                             @NotNull CodeStyleSettings settings,
                             @NotNull SpacingBuilder spacingBuilder) {
      myNode = node;
      myAlignment = alignment;
      myIndent = indent;
      myWrap = wrap;
      mySettings = settings;
      mySpacingBuilder = spacingBuilder;
    }

    @NotNull
    @Override
    public ASTNode getNode() {
      return myNode;
    }

    @NotNull
    @Override
    public TextRange getTextRange() {
      return myNode.getTextRange();
    }

    @Nullable
    @Override
    public Wrap getWrap() {
      return myWrap;
    }

    @Nullable
    @Override
    public Indent getIndent() {
      return myIndent;
    }

    @Nullable
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

    @NotNull
    private List<Block> buildSubBlocks() {
      AlignmentStrategy.AlignmentPerTypeStrategy strategy = null;
      boolean isStruct = getNode().getElementType() == STRUCT_TYPE;
      Alignment forType = null;
      if (isStruct) {
        strategy = AlignmentStrategy.createAlignmentPerTypeStrategy(ContainerUtil.list(FIELD_DECLARATION, LINE_COMMENT), STRUCT_TYPE, true);
        forType = Alignment.createAlignment(true);
      }

      List<Block> blocks = ContainerUtil.newArrayList();
      for (ASTNode child = myNode.getFirstChildNode(); child != null; child = child.getTreeNext()) {
        IElementType childType = child.getElementType();
        if (child.getTextRange().getLength() == 0) continue;
        if (childType == TokenType.WHITE_SPACE) continue;
        IElementType substitutor = childType == MULTILINE_COMMENT ? LINE_COMMENT : childType;
        Alignment alignment = strategy != null ? strategy.getAlignment(substitutor) : null;
        GoFormattingBlock e = buildSubBlock(child, alignment);
        if (isStruct) {
          e.putUserDataIfAbsent(TYPE_ALIGNMENT_INSIDE_STRUCT, forType);
        }
        blocks.add(e);
      }
      return Collections.unmodifiableList(blocks);
    }

    @NotNull
    private GoFormattingBlock buildSubBlock(@NotNull ASTNode child, @Nullable Alignment alignment) {
      if (child.getPsi() instanceof GoType && child.getTreeParent().getElementType() == FIELD_DECLARATION) {
        alignment = getUserData(TYPE_ALIGNMENT_INSIDE_STRUCT);
      }
      Indent indent = calcIndent(child);
      return new GoFormattingBlock(child, alignment, indent, null, mySettings, mySpacingBuilder);
    }

    @NotNull
    private Indent calcIndent(@NotNull ASTNode child) {
      IElementType parentType = myNode.getElementType();
      IElementType type = child.getElementType();
      if (type == SWITCH_START) return Indent.getNoneIndent();
      if (parentType == BLOCK && type == SELECT_STATEMENT) return Indent.getNoneIndent();
      if (parentType == SELECT_STATEMENT && type == RBRACE) return Indent.getNormalIndent();
      if (parentType == ARGUMENT_LIST && type != LPAREN && type != RPAREN) return Indent.getNormalIndent();
      if ((parentType == EXPR_CASE_CLAUSE || parentType == TYPE_CASE_CLAUSE) && (type == CASE || type == TYPE_SWITCH_CASE || type == DEFAULT)) return Indent.getNoneIndent();
      if (BLOCKS_TOKEN_SET.contains(parentType)) return indentIfNotBrace(child);
      if (parentType == IMPORT_DECLARATION && type == IMPORT_SPEC) return Indent.getNormalIndent();
      if (parentType == CONST_DECLARATION && type == CONST_SPEC) return Indent.getNormalIndent();
      if (parentType == VAR_DECLARATION && type == VAR_SPEC) return Indent.getNormalIndent();
      if (parentType == TYPE_DECLARATION && type == TYPE_SPEC) return Indent.getNormalIndent();
      if (parentType == COMM_CLAUSE && child.getPsi() instanceof GoStatement) return Indent.getNormalIndent();
      return Indent.getNoneIndent();
    }

    @NotNull
    private static Indent indentIfNotBrace(@NotNull ASTNode child) {
      return BRACES_TOKEN_SET.contains(child.getElementType()) ? Indent.getNoneIndent() : Indent.getNormalIndent();
    }

    @Override
    public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
      if (child1 instanceof GoFormattingBlock && child2 instanceof GoFormattingBlock) {
        ASTNode n1 = ((GoFormattingBlock)child1).getNode();
        ASTNode n2 = ((GoFormattingBlock)child2).getNode();
        if (n1.getElementType() == FIELD_DEFINITION && n2.getPsi() instanceof GoType) return one();
        if (n1.getElementType() == INTERFACE && n2.getElementType() == LBRACE) {
          ASTNode next = FormatterUtil.getNextNonWhitespaceSibling(n2);
          return next != null && next.getElementType() == RBRACE ? none() : one();
        }
      }
      return mySpacingBuilder.getSpacing(this, child1, child2);
    }

    private Spacing none() {
      return Spacing.createSpacing(0, 0, 0, mySettings.KEEP_LINE_BREAKS, mySettings.KEEP_BLANK_LINES_IN_CODE);
    }

    private Spacing one() {
      return Spacing.createSpacing(1, 1, 0, mySettings.KEEP_LINE_BREAKS, mySettings.KEEP_BLANK_LINES_IN_CODE);
    }

    @NotNull
    @Override
    public ChildAttributes getChildAttributes(int newChildIndex) {
      Indent childIndent = Indent.getNoneIndent();
      IElementType parentType = myNode.getElementType();
      if (BLOCKS_TOKEN_SET.contains(parentType) ||
          parentType == IMPORT_DECLARATION ||
          parentType == CONST_DECLARATION ||
          parentType == VAR_DECLARATION ||
          parentType == TYPE_DECLARATION ||
          parentType == ARGUMENT_LIST) {
        childIndent = Indent.getNormalIndent();
      }
      if (parentType == EXPR_SWITCH_STATEMENT || parentType == TYPE_SWITCH_STATEMENT) {
        List<Block> subBlocks = getSubBlocks();
        Block block = subBlocks.size() > newChildIndex ? subBlocks.get(newChildIndex - 1) : null;
        if (block instanceof GoFormattingBlock) {
          IElementType type = ((GoFormattingBlock)block).getNode().getElementType();
          if (type == TYPE_CASE_CLAUSE || type == EXPR_CASE_CLAUSE) {
            childIndent = Indent.getNormalIndent();
          }
        }
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