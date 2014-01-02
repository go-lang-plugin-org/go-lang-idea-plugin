package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclarations;
import ro.redeul.google.go.lang.psi.expressions.binary.GoBinaryExpression;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.*;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeInterface;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeStruct;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;

import java.util.Map;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Alignments;
import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Indents;
import static ro.redeul.google.go.lang.parser.GoElementTypes.*;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 *         Date: Sep 27, 2010
 */
public class GoBlocks {

  private static final TokenSet ALIGN_LIST_BLOCK_STATEMENTS = TokenSet.create(
    CONST_DECLARATIONS, VAR_DECLARATIONS
  );

  private static final TokenSet LEAF_BLOCKS = TokenSet.create(
    mSL_COMMENT, mML_COMMENT,
    LITERAL_BOOL,
    LITERAL_CHAR,
    LITERAL_IOTA,
    LITERAL_STRING,
    LITERAL_FLOAT, LITERAL_INTEGER, LITERAL_IMAGINARY,
    LITERAL_IDENTIFIER,
    kIMPORT, kVAR, kCONST, kTYPE, kSTRUCT, kPACKAGE, kINTERFACE,
    kSWITCH,
    oASSIGN, oVAR_ASSIGN, oCOMMA,
    TYPE_NAME_DECLARATION,
    pLPAREN, pRPAREN, pLBRACK, pRBRACK, pLCURLY, pRCURLY
  );

  private static final Wrap NO_WRAP = Wrap.createWrap(WrapType.NONE, false);

  public static Block generate(ASTNode node, CommonCodeStyleSettings settings) {
    return generate(node, settings, Indents.NONE, Alignments.NONE, Alignments.EMPTY_MAP, false);
  }

  public static Block generate(ASTNode node, CommonCodeStyleSettings settings,
                               @NotNull Alignment alignment) {
    return generate(node, settings, Indents.NONE, alignment, Alignments.EMPTY_MAP, false);
  }

  public static Block generate(ASTNode node, CommonCodeStyleSettings styleSettings, Indent indent) {
    return generate(node, styleSettings, indent, Alignments.NONE, Alignments.EMPTY_MAP, false);
  }

  public enum Xx {
    Name, Assign, Expression, Comment
  }
  public static Block generate(ASTNode node, CommonCodeStyleSettings settings,
                               @Nullable Indent indent, @Nullable Alignment alignment,
                               @NotNull Map<Alignments.Key, Alignment> alignmentsMap,
                               boolean isPartOfLeadingCommentGroup) {

    PsiElement psi = node.getPsi();

    if (psi instanceof GoBlockStatement)
      return new GoBlockStatementBlock((GoBlockStatement) psi, settings, indent);

    if (psi instanceof GoFile)
      return new GoFileBlock((GoFile) psi, settings);

    if (psi instanceof GoPackageDeclaration)
      return new GoSyntheticBlock<GoPackageDeclaration>((GoPackageDeclaration) psi, settings);

    if (psi instanceof GoImportDeclarations)
      return new GoImportsBlock((GoImportDeclarations) psi, settings);

    if (psi instanceof GoImportDeclaration)
      return new GoSyntheticBlock<GoImportDeclaration>((GoImportDeclaration) psi, settings, indent);

    if (psi instanceof GoConstDeclarations)
      return new GoConstsBlock((GoConstDeclarations) psi, settings);

    if (psi instanceof GoConstDeclaration)
      return new GoConstDeclarationBlock((GoConstDeclaration) psi, settings, indent, alignmentsMap);

    if (psi instanceof GoVarDeclarations)
      return new GoVarsBlock((GoVarDeclarations) psi, settings);

    if (psi instanceof GoVarDeclaration)
      return new GoVarDeclarationBlock((GoVarDeclaration) psi, settings, indent, alignmentsMap);

    if (psi instanceof GoTypeDeclaration)
      return new GoTypesBlock((GoTypeDeclaration) psi, settings);

    if (psi instanceof GoTypeSpec)
      return new GoTypeDeclarationBlock((GoTypeSpec) psi, settings, indent, alignmentsMap);

    if (psi instanceof GoPsiTypeStruct)
      return new GoTypeStructBlock((GoPsiTypeStruct) psi, settings, alignment, alignmentsMap);

    if (psi instanceof GoPsiTypeInterface)
      return new GoTypeInterfaceBlock((GoPsiTypeInterface) psi, settings, alignment, alignmentsMap);

    // here
    if (psi instanceof GoFunctionDeclaration)
      return new GoFunctionDeclarationBlock((GoFunctionDeclaration) psi, settings, indent, alignment, alignmentsMap);

    if (psi instanceof GoTypeStructField)
      return new GoTypeStructFieldBlock((GoTypeStructField) psi, settings, indent, alignmentsMap);

//    if (psi instanceof GoShortVarDeclaration)
//      return new GoShortVarDeclarationBlock((GoShortVarDeclaration)psi, settings, indent);

    if (psi instanceof GoBinaryExpression) {
      return new GoBinaryExpressionBlock(node, alignment, NO_WRAP, settings);
    }


    IElementType elementType = node.getElementType();
    if (elementType == GoTokenTypes.pLPAREN) {
      return new GoLeafBlock(node, null, indent, NO_WRAP, settings);
    } else if (elementType == GoTokenTypes.pRCURLY) {
      if (node.getTreeParent().getElementType() == GoElementTypes.LITERAL_COMPOSITE_VALUE) {
        ASTNode nodeParent = node;
        while (nodeParent != null) {
          if (nodeParent.getElementType() == GoElementTypes.CALL_OR_CONVERSION_EXPRESSION) {
            int indentTabSize = settings.getIndentOptions() == null ? 4 : settings.getIndentOptions().INDENT_SIZE;
            return new GoLeafBlock(node, null, Indent.getSpaceIndent(indentTabSize * -1), NO_WRAP, settings);
          }

          nodeParent = nodeParent.getTreeParent();
        }
      }
    } else if (elementType == GoTokenTypes.kPACKAGE || elementType == GoTokenTypes.oSEMI) {
      return new GoLeafBlock(node,
        null,
        Indent.getAbsoluteNoneIndent(),
        Wrap.createWrap(WrapType.NONE, false),
        settings);
    }

//    if (ALIGN_LIST_BLOCK_STATEMENTS.contains(elementType)) {
//      return new GoAssignListBlock(node, alignment, indent, settings);
//    }

//
//    if (elementType == EXPRESSION_LIST)
//      return new GoExpressionListBlock(node, alignment, indent, settings);
//
//    if (elementType == UNARY_EXPRESSION)
//      return new GoUnaryExpressionBlock(node, alignment, indent, NO_WRAP, settings);
//
//    if (GoElementTypes.FUNCTION_CALL_SETS.contains(elementType))
//      return new GoCallOrConvExpressionBlock(node, alignment, indent, NO_WRAP, settings);
//
//    if (elementType == PARENTHESISED_EXPRESSION)
//      return new GoParenthesisedExpressionBlock(node, alignment, indent, settings);
//
//    if (elementType == LABELED_STATEMENT)
//      return new GoLabeledStatmentBlock(node, settings);
//
//    if (elementType == FUNCTION_PARAMETER_LIST)
//      return new GoFunctionParameterListBlock(node, indent, settings);
//
//    if (elementType == FUNCTION_PARAMETER)
//      return new GoFunctionParameterBlock(node, indent, settings);

    if (psi instanceof PsiComment && isPartOfLeadingCommentGroup)
      return new GoCommentGroupPartBlock((PsiComment)psi, settings, alignment, indent);

    if (LEAF_BLOCKS.contains(elementType))
      return new GoLeafBlock(node, alignment, indent, null, settings);


    return new GoBlock(node, alignment, indent, null, settings);
  }

}
