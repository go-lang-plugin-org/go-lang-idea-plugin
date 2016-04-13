/*
 * Copyright 2013-2016 Sergey Ignatov, Alexander Zolotov, Florin Patan
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
import com.goide.psi.*;
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
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static com.goide.GoParserDefinition.*;
import static com.goide.GoTypes.*;

public class GoFormattingModelBuilder implements FormattingModelBuilder {
  @NotNull
  private static SpacingBuilder createSpacingBuilder(@NotNull CodeStyleSettings settings) {
    return new SpacingBuilder(settings, GoLanguage.INSTANCE)
      .before(COMMA).spaceIf(false)
      .after(COMMA).spaceIf(true)
      .betweenInside(SEMICOLON, SEMICOLON, FOR_CLAUSE).spaces(1)
      .before(SEMICOLON).spaceIf(false)
      .after(SEMICOLON).spaceIf(true)
      .beforeInside(DOT, IMPORT_SPEC).none()
      .afterInside(DOT, IMPORT_SPEC).spaces(1)
      .around(DOT).none()
      .around(ASSIGN).spaces(1)
      .around(VAR_ASSIGN).spaces(1)
      .aroundInside(MUL, POINTER_TYPE).none()
      .before(ARGUMENT_LIST).none()
      .before(BUILTIN_ARGUMENT_LIST).none()
      .afterInside(LPAREN, ARGUMENT_LIST).none()
      .beforeInside(RPAREN, ARGUMENT_LIST).none()
      .afterInside(LPAREN, BUILTIN_ARGUMENT_LIST).none()
      .beforeInside(RPAREN, BUILTIN_ARGUMENT_LIST).none()
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
      .after(INTERFACE).spaces(1)
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
      .before(ELSE_STATEMENT).spaces(1)
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
      .after(LINE_COMMENT).lineBreakInCode()
      .after(MULTILINE_COMMENT).lineBreakInCode()
      .between(COMM_CASE, COLON).none()
      .afterInside(COLON, COMM_CLAUSE).lineBreakInCode()
      .betweenInside(FIELD_DECLARATION, LINE_COMMENT, STRUCT_TYPE).spaces(1)
      .betweenInside(FIELD_DECLARATION, MULTILINE_COMMENT, STRUCT_TYPE).spaces(1)
      .betweenInside(LBRACK, RBRACK, ARRAY_OR_SLICE_TYPE).none()
      .around(ASSIGN_OP).spaces(1)
      .aroundInside(OPERATORS, TokenSet.create(MUL_EXPR, ADD_EXPR, OR_EXPR, CONDITIONAL_EXPR)).spaces(1)

      .betweenInside(LBRACE, RBRACE, BLOCK).spacing(0, 0, 0, true, 1)
      .afterInside(LBRACE, BLOCK).spacing(0, 0, 1, true, 1)
      .beforeInside(RBRACE, BLOCK).spacing(0, 0, 1, true, 1)
      
      .betweenInside(LPAREN, RPAREN, IMPORT_DECLARATION).spacing(0, 0, 0, false, 0)
      .afterInside(LPAREN, IMPORT_DECLARATION).spacing(0, 0, 1, false, 0)
      .beforeInside(RPAREN, IMPORT_DECLARATION).spacing(0, 0, 1, false, 0)
      .between(IMPORT_SPEC, IMPORT_SPEC).spacing(0, 0, 1, true, 1)
      
      .betweenInside(LPAREN, RPAREN, VAR_DECLARATION).spacing(0, 0, 0, false, 0)
      .afterInside(LPAREN, VAR_DECLARATION).spacing(0, 0, 1, false, 0)
      .beforeInside(RPAREN, VAR_DECLARATION).spacing(0, 0, 1, false, 0)
      .beforeInside(TYPE, VAR_SPEC).spaces(1)
      .between(VAR_SPEC, VAR_SPEC).spacing(0, 0, 1, true, 1)
      
      .betweenInside(LPAREN, RPAREN, CONST_DECLARATION).spacing(0, 0, 0, false, 0)
      .afterInside(LPAREN, CONST_DECLARATION).spacing(0, 0, 1, false, 0)
      .beforeInside(RPAREN, CONST_DECLARATION).spacing(0, 0, 1, false, 0)
      .beforeInside(TYPE, CONST_SPEC).spaces(1)
      .between(CONST_SPEC, CONST_SPEC).spacing(0, 0, 1, true, 1)
      
      .between(FIELD_DECLARATION, FIELD_DECLARATION).spacing(0, 0, 1, true, 1)
      .between(METHOD_SPEC, METHOD_SPEC).spacing(0, 0, 1, true, 1)
      ;
  }

  @NotNull
  @Override
  public FormattingModel createModel(@NotNull PsiElement element, @NotNull CodeStyleSettings settings) {
    Block block = new GoFormattingBlock(element.getNode(), null, Indent.getNoneIndent(), null, settings, createSpacingBuilder(settings));
    return FormattingModelProvider.createFormattingModelForPsiFile(element.getContainingFile(), block, settings);
  }

  @Nullable
  @Override
  public TextRange getRangeAffectingIndent(PsiFile file, int offset, ASTNode elementAtOffset) {
    return null;
  }

  private static class GoFormattingBlock extends UserDataHolderBase implements ASTBlock {
    private static final TokenSet BLOCKS_TOKEN_SET = TokenSet.create(
      BLOCK,
      STRUCT_TYPE,
      INTERFACE_TYPE,
      SELECT_STATEMENT,
      EXPR_CASE_CLAUSE,
      TYPE_CASE_CLAUSE,
      LITERAL_VALUE
    );

    private static final TokenSet BRACES_TOKEN_SET = TokenSet.create(
      LBRACE,
      RBRACE,
      LBRACK,
      RBRACK,
      LPAREN,
      RPAREN
    );
    private static final Key<Alignment> TYPE_ALIGNMENT_INSIDE_STRUCT = Key.create("TYPE_ALIGNMENT_INSIDE_STRUCT");

    @NotNull private final ASTNode myNode;
    @Nullable private final Alignment myAlignment;
    @Nullable private final Indent myIndent;
    @Nullable private final Wrap myWrap;
    @NotNull private final CodeStyleSettings mySettings;
    @NotNull private final SpacingBuilder mySpacingBuilder;
    @Nullable private List<Block> mySubBlocks;

    private GoFormattingBlock(@NotNull ASTNode node,
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
    private static Indent indentIfNotBrace(@NotNull ASTNode child) {
      return BRACES_TOKEN_SET.contains(child.getElementType()) ? Indent.getNoneIndent() : Indent.getNormalIndent();
    }

    private static boolean isTopLevelDeclaration(@NotNull PsiElement element) {
      return element instanceof GoPackageClause || element instanceof GoImportList 
             || element instanceof GoTopLevelDeclaration && element.getParent() instanceof GoFile; 
    }

    private static Spacing lineBreak() {
      return lineBreak(true);
    }

    private static Spacing lineBreak(boolean keepLineBreaks) {
      return lineBreak(0, keepLineBreaks);
    }

    private static Spacing lineBreak(int lineBreaks, boolean keepLineBreaks) {
      return Spacing.createSpacing(0, 0, lineBreaks + 1, keepLineBreaks, keepLineBreaks ? 1 : 0);
    }

    private static Spacing none() {
      return Spacing.createSpacing(0, 0, 0, false, 0);
    }

    private static Spacing one() {
      return Spacing.createSpacing(1, 1, 0, false, 0);
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
      if ((parentType == EXPR_CASE_CLAUSE || parentType == TYPE_CASE_CLAUSE) && (type == CASE || type == DEFAULT)) return Indent.getNoneIndent();
      if (BLOCKS_TOKEN_SET.contains(parentType)) return indentIfNotBrace(child);
      if (parentType == IMPORT_DECLARATION) return indentOfMultipleDeclarationChild(type, IMPORT_SPEC);
      if (parentType == CONST_DECLARATION) return indentOfMultipleDeclarationChild(type, CONST_SPEC);
      if (parentType == VAR_DECLARATION) return indentOfMultipleDeclarationChild(type, VAR_SPEC);
      if (parentType == TYPE_DECLARATION) return indentOfMultipleDeclarationChild(type, TYPE_SPEC);
      if (parentType == COMM_CLAUSE && child.getPsi() instanceof GoStatement) return Indent.getNormalIndent();
      if (child.getPsi() instanceof GoExpression) return Indent.getContinuationWithoutFirstIndent(); 
      return Indent.getNoneIndent();
    }

    private Indent indentOfMultipleDeclarationChild(@NotNull IElementType childType, @NotNull IElementType specType) {
      if (childType == specType) {
        return Indent.getNormalIndent();
      }
      return COMMENTS.contains(childType) && myNode.findChildByType(specType) != null ? Indent.getNormalIndent() : Indent.getNoneIndent();
    }

    @Override
    public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
      if (child1 instanceof GoFormattingBlock && child2 instanceof GoFormattingBlock) {
        ASTNode n1 = ((GoFormattingBlock)child1).getNode();
        ASTNode n2 = ((GoFormattingBlock)child2).getNode();
        PsiElement psi1 = n1.getPsi();
        PsiElement psi2 = n2.getPsi();
        if (n1.getElementType() == FIELD_DEFINITION && psi2 instanceof GoType) return one();
        
        PsiElement parent = psi1.getParent();
        if (parent instanceof GoStructType || parent instanceof GoInterfaceType) {
          boolean oneLineType = !parent.textContains('\n');
          if ((n1.getElementType() == STRUCT || n1.getElementType() == INTERFACE) && n2.getElementType() == LBRACE) {
            return oneLineType ? none() : one();
          }
          if (n1.getElementType() == LBRACE && n2.getElementType() == RBRACE) {
            return oneLineType ? none() : lineBreak();
          }
          if (n1.getElementType() == LBRACE) {
            return oneLineType ? one() : lineBreak(false);
          }
          if (n2.getElementType() == RBRACE) {
            return oneLineType ? one() : lineBreak(false);
          }
        }
          
        if (psi1 instanceof GoStatement && psi2 instanceof GoStatement) {
          return lineBreak();
        }
        if (isTopLevelDeclaration(psi2) && (isTopLevelDeclaration(psi1) || n1.getElementType() == SEMICOLON)) {
          // Different declarations should be separated by blank line 
          boolean sameKind = psi1.getClass().equals(psi2.getClass())
                             || psi1 instanceof GoFunctionOrMethodDeclaration && psi2 instanceof GoFunctionOrMethodDeclaration;
          return sameKind ? lineBreak() : lineBreak(1, true);
        }
      }
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
          parentType == VAR_DECLARATION ||
          parentType == TYPE_DECLARATION ||
          parentType == ARGUMENT_LIST) {
        childIndent = Indent.getNormalIndent();
      }
      if (parentType == EXPR_SWITCH_STATEMENT || parentType == TYPE_SWITCH_STATEMENT || parentType == SELECT_STATEMENT) {
        List<Block> subBlocks = getSubBlocks();
        Block block = subBlocks.size() > newChildIndex ? subBlocks.get(newChildIndex - 1) : null;
        if (block instanceof GoFormattingBlock) {
          IElementType type = ((GoFormattingBlock)block).getNode().getElementType();
          if (type == TYPE_CASE_CLAUSE || type == EXPR_CASE_CLAUSE) {
            childIndent = Indent.getNormalIndent();
          }
          else if (type == COMM_CLAUSE) {
            childIndent = Indent.getNormalIndent(true);
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