package ro.redeul.google.go.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.templateLanguages.OuterLanguageElement;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.lang.lexer.GoTokenTypeSets;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParserDefinition;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Sep 27, 2010
 * Time: 7:05:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoBlockGenerator {

    public static List<Block> generateSubBlocks(ASTNode node, Alignment alignment, Wrap wrap, CodeStyleSettings settings, GoBlock block) {

        //For binary expressions
//    PsiElement blockPsi = block.getNode().getPsi();
//    if (blockPsi instanceof GrBinaryExpression &&
//        !(blockPsi.getParent() instanceof GrBinaryExpression)) {
//      return generateForBinaryExpr(node, myWrap, mySettings);
//    }

        //For multiline strings
//    if ((block.getNode().getElementType() == mSTRING_LITERAL ||
//        block.getNode().getElementType() == mGSTRING_LITERAL) &&
//        block.getTextRange().equals(block.getNode().getTextRange())) {
//      String text = block.getNode().getText();
//      if (text.length() > 6) {
//        if (text.substring(0, 3).equals("'''") && text.substring(text.length() - 3).equals("'''") ||
//            text.substring(0, 3).equals("\"\"\"") & text.substring(text.length() - 3).equals("\"\"\"")) {
//          return generateForMultiLineString(block.getNode(), myAlignment, myWrap, mySettings);
//        }
//      }
//    }

//    if (block.getNode().getElementType() == mGSTRING_BEGIN &&
//        block.getTextRange().equals(block.getNode().getTextRange())) {
//      String text = block.getNode().getText();
//      if (text.length() > 3) {
//        if (text.substring(0, 3).equals("\"\"\"")) {
//          return generateForMultiLineGStringBegin(block.getNode(), myAlignment, myWrap, mySettings);
//        }
//      }
//
//    }

        //for gstrings
//    if (block.getNode().getElementType() == GSTRING) {
//      final ArrayList<Block> subBlocks = new ArrayList<Block>();
//      ASTNode[] children = getGroovyChildren(node);
//      ASTNode prevChildNode = null;
//      for (ASTNode childNode : children) {
//        if (childNode.getTextRange().getLength() > 0) {
//          final Indent indent = GroovyIndentProcessor.getChildIndent(block, prevChildNode, childNode);
//          subBlocks.add(new GroovyBlock(childNode, myAlignment, indent, myWrap, mySettings));
//        }
//        prevChildNode = childNode;
//      }
//      return subBlocks;
//    }

        //For nested selections
//    if (NESTED.contains(block.getNode().getElementType()) &&
//        blockPsi.getParent() != null &&
//        blockPsi.getParent().getNode() != null &&
//        !NESTED.contains(blockPsi.getParent().getNode().getElementType())) {
//      return generateForNestedExpr(node, myAlignment, myWrap, mySettings);
//    }

        // For Parameter lists
//    if (isListLikeClause(blockPsi)) {
//      final ArrayList<Block> subBlocks = new ArrayList<Block>();
//      ASTNode[] children = node.getChildren(null);
//      ASTNode prevChildNode = null;
//      final Alignment alignment = mustAlign(blockPsi, mySettings) ? Alignment.createAlignment() : null;
//      for (ASTNode childNode : children) {
//        if (canBeCorrectBlock(childNode)) {
//          final Indent indent = GroovyIndentProcessor.getChildIndent(block, prevChildNode, childNode);
//          subBlocks.add(new GroovyBlock(childNode, isKeyword(childNode) ? null : alignment, indent, myWrap, mySettings));
//          prevChildNode = childNode;
//        }
//      }
//      return subBlocks;
//    }

        if (node.getElementType() == GoElementTypes.BLOCK_STATEMENT) {

            final ArrayList<Block> subBlocks = new ArrayList<Block>();
            ASTNode[] children = node.getChildren(null);

            Alignment newAlignment = Alignment.createChildAlignment(alignment);
            Wrap none = Wrap.createWrap(WrapType.NONE, false);


            for (ASTNode childNode : children) {
                Indent childIndent = Indent.getNoneIndent();
                Alignment childAlignment = alignment;
                Wrap childWrapping = wrap;

                if (isStatement(childNode) || isComment(childNode)) {
                    childIndent = Indent.getNormalIndent(true);
                    childAlignment = newAlignment;
                    childWrapping = none;
                }

                subBlocks.add(new GoBlock(childNode, childAlignment, childIndent, childWrapping, settings));
            }

            return subBlocks;
        }

        if (node.getElementType() == GoElementTypes.IMPORT_DECLARATION) {

            final ArrayList<Block> subBlocks = new ArrayList<Block>();
            ASTNode[] children = node.getChildren(null);

            Alignment newAlignment = Alignment.createChildAlignment(alignment);
            Wrap none = Wrap.createWrap(WrapType.NONE, false);

            for (ASTNode childNode : children) {
//                if ( canBeCorrectBlock(node)) {
                Indent myIndent = Indent.getNoneIndent();
                Alignment myAlignment = alignment;
                Wrap myWrap = wrap;

                if (isImportSpec(childNode)) {
                    myIndent = Indent.getNormalIndent(true);
                    myAlignment = newAlignment;
                    myWrap = none;
                }

                subBlocks.add(new GoBlock(childNode, myAlignment, myIndent, myWrap, settings));
//                }
            }

            return subBlocks;
        }

        final ArrayList<Block> subBlocks = new ArrayList<Block>();
        ASTNode[] children = getGoChildren(node);
        for (ASTNode childNode : children) {
            final Indent indent = Indent.getNoneIndent();
            subBlocks.add(new GoBlock(childNode, alignment, indent, wrap, settings));
        }

        return subBlocks;
    }

    private static boolean isImportSpec(ASTNode childNode) {
        return childNode.getElementType() == GoElementTypes.IMPORT_SPEC;
    }

    private static boolean isNewLine(ASTNode childNode) {
//        return false;
        return childNode.getElementType() == GoTokenTypes.wsNLS;
    }

    private static boolean isComment(ASTNode childNode) {
        return childNode.getElementType() == GoTokenTypes.mSL_COMMENT || childNode.getElementType() == GoTokenTypes.mML_COMMENT;
    }

    private static boolean isStatement(ASTNode node) {
        return GoElementTypes.STATEMENTS.contains(node.getElementType());
    }

    private static boolean canBeCorrectBlock(final ASTNode node) {
        return (node.getText().trim().length() > 0) /*&& node.getElementType() != GoElementTypes.wsNLS*/;
    }

    private static ASTNode[] getGoChildren(final ASTNode node) {

        PsiElement psi = node.getPsi();
        if (psi instanceof OuterLanguageElement) {
            TextRange range = node.getTextRange();
            ArrayList<ASTNode> childList = new ArrayList<ASTNode>();
            PsiFile goFile = psi.getContainingFile().getViewProvider().getPsi(GoFileType.GO_LANGUAGE);
            if (goFile instanceof GoFile) {
                addChildNodes(goFile, childList, range);
            }

            return childList.toArray(new ASTNode[childList.size()]);
        }
        return node.getChildren(null);
    }

    private static void addChildNodes(PsiElement elem, ArrayList<ASTNode> childNodes, TextRange range) {
        ASTNode node = elem.getNode();
        if (range.contains(elem.getTextRange()) && node != null) {
            childNodes.add(node);
        } else {
            for (PsiElement child : elem.getChildren()) {
                addChildNodes(child, childNodes, range);
            }
        }
    }
}
