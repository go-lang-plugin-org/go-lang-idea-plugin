package ro.redeul.google.go.formatter.blocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.ChildAttributes;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.templateLanguages.OuterLanguageElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.GoLanguage;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNodeOfType;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 *         Date: Sep 27, 2010
 */
class GoBlock implements Block, GoElementTypes {

    final protected ASTNode myNode;
    final protected Alignment myAlignment;
    final protected Indent myIndent;
    final protected Wrap myWrap;
    final protected CommonCodeStyleSettings mySettings;
    private Boolean myIncomplete;

    final static TokenSet GO_BLOCK_ELEMENTS =
        TokenSet.not(
            TokenSet.create(GoTokenTypes.wsWS, TokenType.WHITE_SPACE));

    protected List<Block> mySubBlocks = null;

    protected static final Spacing BASIC_SPACING = Spacing.createSpacing(1, 1, 0, false, 0);
    protected static final Spacing EMPTY_SPACING = Spacing.createSpacing(0, 0, 0, false, 0);
    protected static final Spacing LINE_SPACING = Spacing.createSpacing(0, 0, 2, false, 0);

    /**
     * Those statements might contain comments which need to align together
     * e.g.
     *      const (
     *          A    = 2    // The aligned
     *          BCDE = 3456 // comment
     *      )
     */
    protected static final TokenSet ALIGN_COMMENT_STATEMENTS = TokenSet.create(
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
        RETURN_STATEMENT,
        SELECT_STATEMENT,
        SHORT_VAR_STATEMENT,
        SWITCH_EXPR_STATEMENT,
        SWITCH_TYPE_STATEMENT,
        TYPE_DECLARATION,
        TYPE_DECLARATIONS,
        TYPE_STRUCT_FIELD,
        TYPE_STRUCT_FIELD_ANONYMOUS,
        VAR_DECLARATION,
        VAR_DECLARATIONS,
        mML_COMMENT,
        mSL_COMMENT
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
    protected List<Block> buildChildren() {
        List<Block> children = new ArrayList<Block>();

        for (ASTNode child : getGoChildren()) {
            if (child.getTextRange().getLength() == 0 || child.getElementType() == GoElementTypes.wsNLS) {
                continue;
            }

            IElementType type = child.getElementType();
            Block childBlock;
            if (getIndentedElements().contains(type)) {
                childBlock =
                    GoBlockGenerator.generateBlock(
                        child, Indent.getNormalIndent(), mySettings);
            } else {
                childBlock =
                    GoBlockGenerator.generateBlock(
                        child, mySettings);
            }

            children.add(childBlock);
        }

        return children;
    }

    protected TokenSet getIndentedElements() {
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

    protected static boolean isCommentBlock(Block block) {
        return block instanceof GoBlock &&
            COMMENTS.contains(((GoBlock) block).getNode().getElementType());
    }

    protected static boolean inTheSameLine(GoBlock block1, GoBlock block2) {
        ASTNode node = block1.getNode();
        int end = block2.getNode().getStartOffset();
        while ((node = node.getTreeNext()) != null && node.getStartOffset() < end) {
            if (node.getElementType() == GoElementTypes.wsNLS) {
                return false;
            }
        }

        return true;
    }

    public Spacing getSpacing(Block child1, Block child2) {
        return null;
    }


    @Override
    @NotNull
    public ChildAttributes getChildAttributes(int newChildIndex) {
        return new ChildAttributes(getChildIndent(), getFirstChildAlignment());
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
    protected Indent getChildIndent() {
        return Indent.getNormalIndent();
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

    protected ASTNode[] getGoChildren() {
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

        return myNode.getChildren(GO_BLOCK_ELEMENTS);
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
