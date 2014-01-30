package com.goide.formatter;

import com.goide.GoLanguage;
import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.goide.GoTypes.*;

public class GoFormattingModelBuilder implements FormattingModelBuilder {
  @NotNull
  @Override
  public FormattingModel createModel(PsiElement element, CodeStyleSettings settings) {
    Block block = new GoBlock(element.getNode(), null, Indent.getNoneIndent(), null, settings, createSpacingBuilder(settings));
    return FormattingModelProvider.createFormattingModelForPsiFile(element.getContainingFile(), block, settings);
  }

  private static SpacingBuilder createSpacingBuilder(CodeStyleSettings settings) {
    return new SpacingBuilder(settings, GoLanguage.INSTANCE)
      .before(COMMA).spaceIf(false)
      .after(COMMA).spaceIf(true)
      ;
  }

  @Nullable
  @Override
  public TextRange getRangeAffectingIndent(PsiFile file, int offset, ASTNode elementAtOffset) {
    return null;
  }

  public static class GoBlock implements ASTBlock {
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

    public GoBlock(ASTNode node, Alignment alignment, Indent indent, Wrap wrap, CodeStyleSettings settings,
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
      return new ArrayList<Block>(mySubBlocks);
    }

    private List<Block> buildSubBlocks() {
      List<Block> blocks = new ArrayList<Block>();
      for (ASTNode child = myNode.getFirstChildNode(); child != null; child = child.getTreeNext()) {
        IElementType childType = child.getElementType();
        if (child.getTextRange().getLength() == 0) continue;
        if (childType == TokenType.WHITE_SPACE) continue;
        blocks.add(buildSubBlock(child));
      }
      return Collections.unmodifiableList(blocks);
    }

    private Block buildSubBlock(ASTNode child) {
      Indent childIndent = Indent.getNoneIndent();
      if (BLOCKS_TOKEN_SET.contains(myNode.getElementType())) {
        childIndent = indentIfNotBrace(child);
      }
      return new GoBlock(child, null, childIndent, null, mySettings, mySpacingBuilder);
    }

    private static Indent indentIfNotBrace(ASTNode child) {
      return BRACES_TOKEN_SET.contains(child.getElementType()) ? Indent.getNoneIndent() : Indent.getNormalIndent();
    }

    @Override
    public Spacing getSpacing(Block child1, Block child2) {
      return mySpacingBuilder.getSpacing(this, child1, child2);
    }

    @NotNull
    @Override
    public ChildAttributes getChildAttributes(int newChildIndex) {
      Indent childIndent = Indent.getNoneIndent();
      if (BLOCKS_TOKEN_SET.contains(myNode.getElementType())) {
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