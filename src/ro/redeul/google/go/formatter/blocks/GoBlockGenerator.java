package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import static ro.redeul.google.go.lang.parser.GoElementTypes.*;

import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.binary.GoBinaryExpression;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoPackageDeclaration;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 *         Date: Sep 27, 2010
 */
public class GoBlockGenerator {

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
            LITERAL_IDENTIFIER
    );

    private static final Wrap NO_WRAP = Wrap.createWrap(WrapType.NONE, false);

    public static Block generateBlock(ASTNode node,
                                      CommonCodeStyleSettings settings) {
        return generateBlock(node, Indent.getNoneIndent(), settings);
    }

    public static Block generateBlock(ASTNode node, Alignment alignment,
                                      CommonCodeStyleSettings settings) {
        return generateBlock(node, Indent.getNoneIndent(), alignment, settings);
    }

    public static Block generateBlock(ASTNode node, Indent indent, CommonCodeStyleSettings styleSettings) {
        return generateBlock(node, indent, null, styleSettings);
    }

    public static Block generateBlock(ASTNode node, Indent indent, Alignment alignment, CommonCodeStyleSettings styleSettings) {
        PsiElement psi = node.getPsi();
        if (psi instanceof GoBlockStatement)
            return new GoBlockStatementBlock(node, indent, styleSettings);

        if (psi instanceof GoFile)
            return generateGoFileBlock(node, styleSettings);

        if (psi instanceof GoPackageDeclaration)
            return generatePackageBlock(node, styleSettings);

        if (psi instanceof GoBinaryExpression) {
            return new GoBinaryExpressionBlock(node, alignment, NO_WRAP, styleSettings);
        }

        if (psi instanceof GoFunctionDeclaration) {
            return new GoFunctionDeclarationBlock(node, alignment, indent, styleSettings);
        }

        IElementType elementType = node.getElementType();
        if (elementType == GoTokenTypes.pLPAREN) {
            return new GoLeafBlock(node, null, indent, NO_WRAP, styleSettings);
        } else if(elementType == GoTokenTypes.pRCURLY) {
            if (node.getTreeParent().getElementType() == GoElementTypes.LITERAL_COMPOSITE_VALUE) {
                ASTNode nodeParent = node;
                while (nodeParent != null) {
                    if (nodeParent.getElementType() == GoElementTypes.CALL_OR_CONVERSION_EXPRESSION) {
                        int indentTabSize = styleSettings.getIndentOptions() == null ? 4 : styleSettings.getIndentOptions().INDENT_SIZE;
                        return new GoLeafBlock(node, null, Indent.getSpaceIndent(indentTabSize * -1), NO_WRAP, styleSettings);
                    }

                    nodeParent = nodeParent.getTreeParent();
                }
            }
        } else if (elementType == GoTokenTypes.kPACKAGE || elementType == GoTokenTypes.oSEMI) {
            return new GoLeafBlock(node,
                                   null,
                                   Indent.getAbsoluteNoneIndent(),
                                   Wrap.createWrap(WrapType.NONE, false),
                                   styleSettings);
        }

        if (ALIGN_LIST_BLOCK_STATEMENTS.contains(elementType)) {
            return new GoAssignListBlock(node, alignment, indent, styleSettings);
        }

        if (elementType == TYPE_STRUCT) {
            return new GoTypeStructBlock(node, alignment, indent, styleSettings);
//        } else if (elementType == GoElementTypes.TYPE_INTERFACE) {
//            return new GoTypeInterfaceBlock(node, alignment, indent, styleSettings);
        }

        if (elementType == EXPRESSION_LIST)
            return new GoExpressionListBlock(node, alignment, indent, styleSettings);

        if (elementType == UNARY_EXPRESSION)
            return new GoUnaryExpressionBlock(node, alignment, indent, NO_WRAP, styleSettings);

        if (GoElementTypes.FUNCTION_CALL_SETS.contains(elementType))
            return new GoCallOrConvExpressionBlock(node, alignment, indent, NO_WRAP, styleSettings);

        if (elementType == PARENTHESISED_EXPRESSION)
            return new GoParenthesisedExpressionBlock(node, alignment, indent, styleSettings);

        if (elementType == LABELED_STATEMENT)
            return new GoLabeledStatmentBlock(node, styleSettings);

        if (elementType == FUNCTION_PARAMETER_LIST)
            return new GoFunctionParameterListBlock(node, indent, styleSettings);

        if (elementType == FUNCTION_PARAMETER)
            return new GoFunctionParameterBlock(node, indent, styleSettings);

        if (LEAF_BLOCKS.contains(elementType))
            return new GoLeafBlock(node, alignment, indent, Wrap.createWrap(WrapType.NONE, false), styleSettings);


        return new GoBlock(node, alignment, indent, NO_WRAP, styleSettings);
    }

    private static Block generatePackageBlock(ASTNode node,
                                              CommonCodeStyleSettings settings) {
        return new GoPackageBlock(node,
                                  Alignment.createAlignment(),
                                  Indent.getNoneIndent(),
                                  Wrap.createWrap(WrapType.NONE, false),
                                  settings);
    }

    private static Block generateGoFileBlock(ASTNode node,
                                             CommonCodeStyleSettings settings) {
        return new GoFileBlock(node, null,
                Indent.getAbsoluteNoneIndent(),
                Wrap.createWrap(WrapType.NORMAL, true),
                settings);
    }
}
