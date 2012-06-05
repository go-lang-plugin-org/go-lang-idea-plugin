package ro.redeul.google.go.formatter;

import java.util.ArrayList;
import java.util.List;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.formatting.WrapType;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.templateLanguages.OuterLanguageElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.GoLanguage;
import ro.redeul.google.go.formatter.blocks.GoBlock;
import ro.redeul.google.go.formatter.blocks.GoConstBlock;
import ro.redeul.google.go.formatter.blocks.GoFileBlock;
import ro.redeul.google.go.formatter.blocks.GoLeafBlock;
import ro.redeul.google.go.formatter.blocks.GoPackageBlock;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoPackageDeclaration;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 *         Date: Sep 27, 2010
 */
public class GoBlockGenerator {

    public static final Wrap NO_WRAP = Wrap.createWrap(WrapType.NONE, false);

    public static List<Block> generateSubBlocks(ASTNode node,
                                                Alignment alignment, Wrap wrap,
                                                CodeStyleSettings settings,
                                                GoBlock block) {

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


        IElementType nodeType = node.getElementType();

        if (nodeType == GoElementTypes.BLOCK_STATEMENT)
            return indentChildrens(block, node,
                                   TokenSet.orSet(GoElementTypes.COMMENTS,
                                                  GoElementTypes.STATEMENTS),
                                   settings);
//            return forBlockStatement(node, block, alignment, wrap, settings);

        if (nodeType == GoElementTypes.IMPORT_DECLARATIONS)
            return forImportDeclarations(node, block, alignment, wrap,
                                         settings);

        if (nodeType == GoElementTypes.VAR_DECLARATIONS)
            return forVarDeclarations(node, block, alignment, wrap, settings);

        if (nodeType == GoElementTypes.TYPE_INTERFACE)
//            return forTypeInterface(node, block, alignment, wrap, settings);
            return indentChildrens(block, node,
                                   TokenSet.orSet(GoElementTypes.COMMENTS,
                                                  TokenSet.create(
                                                      GoElementTypes.METHOD_DECLARATION,
                                                      GoElementTypes.FUNCTION_DECLARATION)),
                                   settings);

//        final List<Block> subBlocks = new ArrayList<Block>();
//        ASTNode[] children = getGoChildren(node);
//        for (ASTNode childNode : children) {
//            subBlocks.add(
//                new GoBlock(childNode,
//                            block.getAlignment(),
//                            Indent.getNoneIndent(),
//                            block.getWrap(),
//                            settings));
//        }

//        return subBlocks;
        return null;
    }



    private static List<Block> indentChildrens(Block parent, ASTNode node,
                                               TokenSet childTypes,
                                               CodeStyleSettings settings) {
        List<Block> childBlocks = new ArrayList<Block>();
        ASTNode[] children = getGoChildren(node);

        Alignment childAlignment =
            Alignment.createAlignment();

        Block childBlock = null;
        for (ASTNode child : children) {
//            if (childTypes.contains(child.getElementType())) {
//                childBlock = new GoBlock(child,
//                                         childAlignment,
//                                         Indent.getNormalIndent(),
//                                         Wrap.createWrap(WrapType.NONE, false),
//                                         settings);
//            } else {
//                childBlock = new GoBlock(child,
//                                         Alignment.createAlignment(),
//                                         Indent.getNoneIndent(),
//                                         Wrap.createWrap(WrapType.NONE, false),
//                                         settings);
//            }

            childBlocks.add(childBlock);
        }

        return childBlocks;
    }

    private static List<Block> forTypeInterface(ASTNode node, GoBlock block,
                                                Alignment alignment, Wrap wrap,
                                                CodeStyleSettings settings) {
        List<Block> subBlocks = new ArrayList<Block>();
        ASTNode[] children = getGoChildren(node);

        for (ASTNode childNode : children) {
            Indent myIndent = Indent.getNoneIndent();
            Alignment myAlignment = Alignment.createAlignment();
            Wrap myWrap = Wrap.createWrap(WrapType.NONE, false);

            if (childNode.getElementType() == GoElementTypes.METHOD_DECLARATION ||
                childNode.getElementType() == GoElementTypes.mSL_COMMENT) {
                myIndent = Indent.getNormalIndent();
                myAlignment = Alignment.createAlignment();
                myWrap = Wrap.createWrap(WrapType.NONE, false);
            }

//            subBlocks.add(
//                new GoBlock(childNode, myAlignment, myIndent, myWrap,
//                            settings));
        }

        return subBlocks;
    }

    private static List<Block> forVarDeclarations(ASTNode node, GoBlock block,
                                                  Alignment alignment,
                                                  Wrap wrap,
                                                  CodeStyleSettings settings) {
        List<Block> subBlocks = new ArrayList<Block>();
        ASTNode[] children = getGoChildren(node);

        for (ASTNode childNode : children) {
            Indent myIndent = Indent.getNoneIndent();
            Alignment myAlignment = Alignment.createAlignment();
            Wrap myWrap = Wrap.createWrap(WrapType.NONE, false);

            if (childNode.getElementType() == GoElementTypes.VAR_DECLARATION) {
                myIndent = Indent.getNormalIndent();
                myAlignment = Alignment.createAlignment();
                myWrap = Wrap.createWrap(WrapType.NONE, false);
            }

//            subBlocks.add(
//                new GoBlock(childNode, myAlignment, myIndent, myWrap,
//                            settings));
        }

        return subBlocks;
    }

    private static List<Block> forImportDeclarations(ASTNode node,
                                                     GoBlock block,
                                                     Alignment alignment,
                                                     Wrap wrap,
                                                     CodeStyleSettings settings) {
        List<Block> subBlocks = new ArrayList<Block>();
        ASTNode[] children = getGoChildren(node);

        for (ASTNode childNode : children) {
            Indent myIndent = Indent.getNoneIndent();
            Alignment myAlignment = Alignment.createAlignment();
            Wrap myWrap = Wrap.createWrap(WrapType.NONE, false);

            if (isImportSpec(childNode)) {
                myIndent = Indent.getNormalIndent();
                myAlignment = Alignment.createAlignment();
                myWrap = Wrap.createWrap(WrapType.NONE, false);
            }

//            subBlocks.add(
//                new GoBlock(childNode, myAlignment, myIndent, myWrap,
//                            settings));
        }

        return subBlocks;
    }

    private static List<Block> forBlockStatement(ASTNode node, GoBlock block,
                                                 Alignment align,
                                                 Wrap wrap,
                                                 CodeStyleSettings settings) {
        List<Block> subBlocks = new ArrayList<Block>();
        ASTNode[] children = getGoChildren(node);

        Alignment newAlignment = Alignment.createChildAlignment(align);
        Wrap none = Wrap.createWrap(WrapType.NONE, false);

        for (int i = 0, len = children.length; i < len; i++) {
            ASTNode childNode = children[i];
            Indent chIdent = Indent.getNoneIndent();
            Alignment chAlign = align;
            Wrap chWrap = none;

            if (isStatement(childNode) || isComment(childNode)) {
                chIdent = Indent.getNormalIndent();
                chAlign = Alignment.createAlignment();
                chWrap = none;
            }

//            subBlocks.add(
//                new GoBlock(childNode, chAlign, chIdent, chWrap, settings));
        }

        return subBlocks;
    }

    private static boolean isImportSpec(ASTNode childNode) {
        return childNode.getElementType() == GoElementTypes.IMPORT_DECLARATION;
    }

    private static boolean isNewLine(ASTNode childNode) {
//        return false;
        return childNode.getElementType() == GoTokenTypes.wsNLS;
    }

    private static boolean isComment(ASTNode childNode) {
        return childNode.getElementType() == GoTokenTypes.mSL_COMMENT || childNode
            .getElementType() == GoTokenTypes.mML_COMMENT;
    }

    private static boolean isStatement(ASTNode node) {
        return GoElementTypes.STATEMENTS.contains(node.getElementType());
    }

    private static boolean canBeCorrectBlock(final ASTNode node) {
        return (node.getText()
                    .trim()
                    .length() > 0) /*&& node.getElementType() != GoElementTypes.wsNLS*/;
    }

    private static ASTNode[] getGoChildren(final ASTNode node) {

        PsiElement psi = node.getPsi();
        if (psi instanceof OuterLanguageElement) {
            TextRange range = node.getTextRange();
            List<ASTNode> childList = new ArrayList<ASTNode>();
            PsiFile goFile = psi.getContainingFile()
                                .getViewProvider()
                                .getPsi(GoLanguage.INSTANCE);
            if (goFile instanceof GoFile) {
                addChildNodes(goFile, childList, range);
            }

            return childList.toArray(new ASTNode[childList.size()]);
        }

//        return node.getChildren(GO_ELEMENT_TYPES);
        return null;
    }

    private static void addChildNodes(PsiElement elem,
                                      List<ASTNode> childNodes,
                                      TextRange range) {
        ASTNode node = elem.getNode();
        if (range.contains(elem.getTextRange()) && node != null) {
            childNodes.add(node);
        } else {
            for (PsiElement child : elem.getChildren()) {
                addChildNodes(child, childNodes, range);
            }
        }
    }

    public static Block generateBlock(ASTNode node,
                                      CommonCodeStyleSettings settings) {
        return generateBlock(node, Indent.getNoneIndent(), settings);
    }


    public static Block generateBlock(ASTNode node, Indent indent, CommonCodeStyleSettings styleSettings) {
        return generateBlock(node, null, indent, NO_WRAP,
                             styleSettings);
    }

    public static Block generateBlock(ASTNode node, Alignment align, Indent indent,
                                      Wrap wrap, CommonCodeStyleSettings settings)
    {
        if (node.getPsi() instanceof GoFile)
            return generateGoFileBlock(node, settings);

        if (node.getPsi() instanceof GoPackageDeclaration)
            return generatePackageBlock(node, settings);

        if (node.getPsi() instanceof GoFunctionDeclaration)
            return generateMethodBlock(node, settings);

        if (node.getPsi() instanceof GoConstDeclarations)
            return new GoConstBlock((GoConstDeclarations) node.getPsi(),
                                    align, indent, wrap, settings);
        return
            generateDefaultGoBlock(node, align, indent, wrap, settings);
    }

    private static Block generatePackageBlock(ASTNode node,
                                              CommonCodeStyleSettings settings) {
        return new GoPackageBlock(node,
                                  Alignment.createAlignment(),
                                  Indent.getNoneIndent(),
                                  Wrap.createWrap(WrapType.NONE, false),
                                  settings);
    }

    private static Block generateMethodBlock(ASTNode node,
                                             CommonCodeStyleSettings settings) {
        return new GoBlock(node, Alignment.createAlignment(),
                           Indent.getNoneIndent(), NO_WRAP, settings) {
            @Override
            protected TokenSet getIndentedElements() {
                return GoElementTypes.STATEMENTS;
            }
        };
    }

    private static Block generateGoFileBlock(ASTNode node,
                                             CommonCodeStyleSettings settings) {
        return new GoFileBlock(node,
                               Alignment.createAlignment(),
                               Indent.getAbsoluteNoneIndent(),
                               Wrap.createWrap(WrapType.NONE, false),
                               settings);
    }

    private static Block generateDefaultGoBlock(ASTNode node, Alignment align,
                                                Indent indent, Wrap wrap,
                                                CommonCodeStyleSettings settings) {

        if ( node.getElementType() == GoTokenTypes.kPACKAGE ||
            node.getElementType() == GoTokenTypes.mIDENT ||
            node.getElementType() == GoTokenTypes.oSEMI) {
            return new GoLeafBlock(node,
                                   align,
                                   Indent.getAbsoluteNoneIndent(),
                                   Wrap.createWrap(WrapType.NONE, false),
                                   settings);
        }

        return new GoBlock(node, align, indent, wrap, settings) {
        };
    }
}
