package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.templateLanguages.OuterLanguageElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.GoLanguage;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNewLineNode;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isWhiteSpaceNode;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 *         Date: Sep 27, 2010
 */
class GoBlock implements ASTBlock, GoElementTypes {

    final ASTNode myNode;
    private final Alignment myAlignment;
    private final Indent myIndent;
    private final Wrap myWrap;
    final CommonCodeStyleSettings mySettings;
    private Boolean myIncomplete;

    private List<Block> mySubBlocks = null;

    static final Spacing ONE_LINE_SPACING = Spacing.createSpacing(0, 0, 1, false, 0);
    static final Spacing ONE_LINE_SPACING_KEEP_LINE_BREAKS = Spacing.createSpacing(0, 0, 1, true, 1);
    static final Spacing BASIC_SPACING = Spacing.createSpacing(1, 1, 0, false, 0);
    static final Spacing BASIC_SPACING_KEEP_LINE_BREAKS = Spacing.createSpacing(1, 1, 0, true, 0);
    static final Spacing EMPTY_SPACING = Spacing.createSpacing(0, 0, 0, false, 0);
    static final Spacing EMPTY_SPACING_KEEP_LINE_BREAKS = Spacing.createSpacing(0, 0, 0, true, 0);
    static final Spacing LINE_SPACING = Spacing.createSpacing(0, 0, 2, false, 0);

    static final Indent NORMAL_INDENT_TO_CHILDREN  = Indent.getIndent(Indent.Type.NORMAL, false, true);
    static final Indent CONTINUATION_WITHOUT_FIRST = Indent.getIndent(Indent.Type.CONTINUATION_WITHOUT_FIRST, false, false);

    /**
     * Those statements might contain comments which need to align together
     * e.g.
     *      const (
     *          A    = 2    // The aligned
     *          BCDE = 3456 // comment
     *      )
     */
    static final TokenSet ALIGN_COMMENT_STATEMENTS = TokenSet.create(
        CONST_DECLARATIONS,
        VAR_DECLARATIONS,
        TYPE_INTERFACE,
        TYPE_STRUCT
    );

    private static final TokenSet INDENT_STATEMENTS = TokenSet.create(
        ASSIGN_STATEMENT,
        BREAK_STATEMENT,
        CONST_DECLARATION,
        CONST_DECLARATIONS,
        CONTINUE_STATEMENT,
        DEFER_STATEMENT,
        EXPRESSION_STATEMENT,
        FALLTHROUGH_STATEMENT,
        FOR_WITH_CLAUSES_STATEMENT,
        FOR_WITH_CONDITION_STATEMENT,
        FOR_WITH_RANGE_STATEMENT,
        GOTO_STATEMENT,
        GO_STATEMENT,
        IF_STATEMENT,
        IMPORT_DECLARATION,
        INC_DEC_STATEMENT,
        LITERAL_COMPOSITE_ELEMENT,
        METHOD_DECLARATION,
        FUNCTION_DECLARATION,
        RETURN_STATEMENT,
        SELECT_STATEMENT,
        SHORT_VAR_STATEMENT,
        SWITCH_EXPR_STATEMENT,
        SWITCH_TYPE_STATEMENT,
//        TYPE_DECLARATION,
//        TYPE_DECLARATIONS,
        TYPE_STRUCT_FIELD,
        TYPE_STRUCT_FIELD_ANONYMOUS,
        VAR_DECLARATION,
        VAR_DECLARATIONS,
        mML_COMMENT,
        mSL_COMMENT
        );

    private static final TokenSet BASIC_SPACE_KEYWORDS_SET = TokenSet.create(
            kCASE, kCHAN, kCONST, kDEFER, kELSE, kFOR, kGO,
            kGOTO, kIF, kIMPORT, kRANGE, kRETURN, kSELECT, kSTRUCT,
            kSWITCH, kTYPE, kVAR
        );

    public GoBlock(ASTNode node, Alignment alignment, Indent indent, Wrap wrap,
                   CommonCodeStyleSettings settings) {
        myNode = node;
        myAlignment = alignment;
        myIndent = indent;
        myWrap = wrap;
        mySettings = settings;

    }

    @NotNull
    public ASTNode getNode() {
        return myNode;
    }

    @NotNull
    public TextRange getTextRange() {
        return myNode.getTextRange();
    }

    @NotNull
    public List<Block> getSubBlocks() {
        if (mySubBlocks == null) {
            List<Block> children = buildChildren();
            if (children == null || children.isEmpty()) {
                mySubBlocks = Collections.emptyList();
            } else {
                mySubBlocks = children;
            }
        }

        return mySubBlocks;
    }

    @Nullable
    List<Block> buildChildren() {
        List<Block> children = new ArrayList<Block>();

        ASTNode prevChild = null;
        for (ASTNode child : getGoChildren()) {
            if (child.getElementType() != END_OF_COMPILATION_UNIT &&
                    (child.getTextRange().getLength() == 0 || isWhiteSpaceNode(child.getPsi()))) {
                continue;
            }

            Indent indent = getChildIndent(prevChild != null ? prevChild.getPsi() : null, child.getPsi());
            children.add(GoBlockGenerator.generateBlock(child, indent, mySettings));
            prevChild = child;
        }

        return children;
    }

    TokenSet getIndentedElements() {
        return INDENT_STATEMENTS;
    }

    public Wrap getWrap() {
        return myWrap;
    }

    public Indent getIndent() {
        return myIndent;
    }

    public Alignment getAlignment() {
        return myAlignment;
    }

    static boolean isCommentBlock(Block block) {
        return block instanceof GoBlock &&
            COMMENTS.contains(((GoBlock) block).getNode().getElementType());
    }

    static boolean inTheSameLine(GoBlock block1, GoBlock block2) {
        return inTheSameLine(block1.getNode(), block2.getNode());
    }

    static boolean inTheSameLine(ASTNode node1, ASTNode node2) {
        int end = node2.getStartOffset();
        while ((node1 = node1.getTreeNext()) != null && node1.getStartOffset() < end) {
            if (isNewLineNode(node1.getPsi())) {
                return false;
            }
        }

        return true;
    }

    public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
        if (!(child1 instanceof GoBlock) || !(child2 instanceof GoBlock)) {
            return null;
        }

        return getGoBlockSpacing((GoBlock) child1, (GoBlock) child2);
    }

    Spacing getGoBlockSpacing(GoBlock child1, GoBlock child2) {
        IElementType child1Type = child1.getNode().getElementType();
        IElementType child2Type = child2.getNode().getElementType();
        // there should be a space after ","
        if (child1Type == oCOMMA) {
            return BASIC_SPACING_KEEP_LINE_BREAKS;
        }

        // there should be a space before and after "=" and ":="
        if (child1Type == oASSIGN || child1Type == oVAR_ASSIGN ||
            child2Type == oASSIGN || child2Type == oVAR_ASSIGN) {
            return BASIC_SPACING_KEEP_LINE_BREAKS;
        }

        // there should be a space after ";" where there is statement after ";"
        if (child1Type == oSEMI && inTheSameLine(child1, child2)) {
            return BASIC_SPACING;
        }

        // there should be a space before any block statement
        if (child2.getNode().getPsi() instanceof GoBlockStatement ||
                child2Type == FUNCTION_RESULT) {
            return BASIC_SPACING_KEEP_LINE_BREAKS;
        }

        if (child1Type == kINTERFACE) {
            try {
                if (child1.getNode().getTreeParent().getTreeParent().getTreeParent().getTreeParent().getPsi() instanceof GoFile) {
                    return BASIC_SPACING_KEEP_LINE_BREAKS;
                } else {
                    return EMPTY_SPACING_KEEP_LINE_BREAKS;
                }
            } catch (Exception ignored) {
                return BASIC_SPACING_KEEP_LINE_BREAKS;
            }
        }

        // there should be no space after "type" in type guard: p.(type)
        if (child1Type == kTYPE) {
            ASTNode parent = child1.getNode().getTreeParent();
            if (parent != null && parent.getElementType() == SWITCH_TYPE_GUARD) {
                return EMPTY_SPACING;
            }
        }

        // there should be a space after those keywords
        if (BASIC_SPACE_KEYWORDS_SET.contains(child1Type)) {
            return BASIC_SPACING;
        }

        if (child1Type == kFUNC) {
            // for function declarations, there should be exactly one space after "func"
            // for literal functions, there should be no space after "func"
            ASTNode parent = child1.getNode().getTreeParent();
            if (parent != null && (parent.getElementType() == LITERAL_FUNCTION || parent.getElementType() == TYPE_FUNCTION)) {
                return EMPTY_SPACING;
            }

            return BASIC_SPACING;
        }

        if (COMMENTS.contains(child1Type) && child2Type == FUNCTION_DECLARATION) {
            if (inTheSameLine(child1, child2)) {
                return BASIC_SPACING_KEEP_LINE_BREAKS;
            } else {
                return ONE_LINE_SPACING_KEEP_LINE_BREAKS;
            }
        }

        return null;
    }

    @Override
    @NotNull
    public ChildAttributes getChildAttributes(int newChildIndex) {
        return new ChildAttributes(getChildIndent(null), getFirstChildAlignment());
    }

    @Nullable
    private Alignment getFirstChildAlignment() {
        for (Block subBlock : getSubBlocks()) {
            Alignment alignment = subBlock.getAlignment();
            if (alignment != null) {
                return alignment;
            }
        }
        return null;
    }


    @Nullable
    Indent getChildIndent(@Nullable PsiElement prevChild, @Nullable PsiElement child) {
        return getChildIndent(child);
    }

    @Nullable
    Indent getChildIndent(@Nullable PsiElement child) {
        if (child == null) {
            return Indent.getNormalIndent();
        }

        ASTNode node = child.getNode();
        if (node == null || getIndentedElements().contains(node.getElementType())) {
            if (node != null && node.getElementType() == LITERAL_COMPOSITE_ELEMENT) {
                boolean inFunctionCall = false;
                ASTNode nodeParent = node;
                while (nodeParent != null) {
                    if (nodeParent.getElementType() == CALL_OR_CONVERSION_EXPRESSION) {
                        inFunctionCall = true;
                        break;
                    }

                    nodeParent = nodeParent.getTreeParent();
                }

                if (inFunctionCall) {
                    return Indent.getNoneIndent();
                }
            }
            return Indent.getNormalIndent();
        }

        return Indent.getNoneIndent();
    }

    @Override
    public boolean isIncomplete() {
        if (myIncomplete == null) {
            myIncomplete = FormatterUtil.isIncomplete(getNode());
        }
        return myIncomplete;
    }

    public boolean isLeaf() {
        return myNode.getFirstChildNode() == null;
    }

    ASTNode[] getGoChildren() {
        PsiElement psi = myNode.getPsi();
        if (psi instanceof OuterLanguageElement) {
            TextRange range = myNode.getTextRange();
            List<ASTNode> childList = new ArrayList<ASTNode>();
            PsiFile goFile = psi.getContainingFile()
                                .getViewProvider()
                                .getPsi(GoLanguage.INSTANCE);
            if (goFile instanceof GoFile) {
                addChildNodes(goFile, childList, range);
            }

            return childList.toArray(new ASTNode[childList.size()]);
        }

        return myNode.getChildren(null);
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
}
