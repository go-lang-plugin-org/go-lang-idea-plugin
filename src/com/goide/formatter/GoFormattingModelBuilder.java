package com.goide.formatter;

import com.goide.GoLanguage;
import com.goide.GoParserDefinition;
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
      .beforeInside(DOT, IMPORT_SPEC).none()
      .afterInside(DOT, IMPORT_SPEC).spaces(1)
      .around(DOT).none()
      .around(ASSIGN).spaces(1)
      .around(VAR_ASSIGN).spaces(1)
      .aroundInside(MUL, POINTER_TYPE).none()
      .before(ARGUMENT_LIST).none()
      .afterInside(LPAREN, ARGUMENT_LIST).none()
      .beforeInside(RPAREN, ARGUMENT_LIST).none()
      .before(SIGNATURE).none()
      .afterInside(LPAREN, TYPE_ASSERTION_EXPR).none()
      .beforeInside(RPAREN, TYPE_ASSERTION_EXPR).none()
      .afterInside(LPAREN, PARAMETERS).none()
      .beforeInside(RPAREN, PARAMETERS).none()
      .afterInside(LPAREN, RESULT).none()
      .beforeInside(RPAREN, RESULT).none()
      .between(PARAMETERS, RESULT).spaces(1)
      .before(BLOCK).spaces(1)
      .after(FUNC).spaces(1)
      .after(PACKAGE).spaces(1)
      .after(IMPORT).spaces(1)
      .after(CONST).spaces(1)
      .after(VAR).spaces(1)
      .after(STRUCT).spaces(1)
      .afterInside(MAP, MAP_TYPE).none()
      .aroundInside(LBRACK, MAP_TYPE).none()
      .aroundInside(RBRACK, MAP_TYPE).none()
      .between(LITERAL_TYPE_EXPR, LITERAL_VALUE).none()
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

    private ASTNode myNode;
    private Alignment myAlignment;
    private Indent myIndent;
    private Wrap myWrap;
    private CodeStyleSettings mySettings;
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
      if (BLOCKS_TOKEN_SET.contains(parentType)) return indentIfNotBrace(child);
      if (parentType == IMPORT_DECLARATION && type == IMPORT_SPEC) return Indent.getNormalIndent();
      if (parentType == CONST_DECLARATION && type == CONST_SPEC) return Indent.getNormalIndent();
      if (parentType == VAR_DECLARATION && type == VAR_SPEC) return Indent.getNormalIndent();
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
      if (BLOCKS_TOKEN_SET.contains(parentType)) {
        childIndent = Indent.getNormalIndent();
      }
      else if (parentType == IMPORT_DECLARATION || parentType == CONST_DECLARATION || parentType == VAR_DECLARATION) {
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