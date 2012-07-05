package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;

import java.util.ArrayList;
import java.util.List;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNodeOfType;

class GoAssignListBlock extends GoBlock {
    /**
     * Those statements might contain assignments which need to align to the assign mark.
     * e.g.
     *      const (
     *          A   = 2
     *          BCD = 3
     *      )
     */
    protected static final TokenSet ALIGN_ASSIGNMENT_STATEMENTS = TokenSet.create(
        CONST_DECLARATION,
        VAR_DECLARATION
    );

    public GoAssignListBlock(ASTNode node, Alignment alignment, Indent indent, CommonCodeStyleSettings settings) {
        super(node, alignment, indent, null, settings);
    }

    @Override
    protected List<Block> buildChildren() {
        List<Block> children = new ArrayList<Block>();
        int newLinesAfterLastAssign = 0;
        int newLinesAfterLastComment = 0;
        Alignment assignAlignment = null;
        Alignment commentAlignment = null;
        boolean needAlignComment = isNodeOfType(myNode.getPsi(), ALIGN_COMMENT_STATEMENTS);

        // Whether there is an assignment statement in current line.
        // We should only align those comments that after an assignment statement.
        boolean assignInCurrentLine = false;

        for (ASTNode child : getGoChildren()) {
            if (child.getTextRange().getLength() == 0) {
                continue;
            }

            IElementType type = child.getElementType();
            if (type == GoTokenTypes.wsNLS) {
                newLinesAfterLastAssign += child.getTextLength();
                newLinesAfterLastComment += child.getTextLength();
                assignInCurrentLine = false;
                continue;
            } else if (ALIGN_ASSIGNMENT_STATEMENTS.contains(type)) {
                assignInCurrentLine = true;
                if (newLinesAfterLastAssign > 1 || assignAlignment == null) {
                    assignAlignment = Alignment.createAlignment(true);
                }
                newLinesAfterLastAssign = 0;
                children.add(new GoAssignBlock(child, assignAlignment, Indent.getNoneIndent(), mySettings));
                continue;
            } else if (needAlignComment && assignInCurrentLine && COMMENTS.contains(type)) {
                if (newLinesAfterLastComment > 1 || commentAlignment == null) {
                    commentAlignment = Alignment.createAlignment(true);
                }
                newLinesAfterLastComment = 0;
                children.add(GoBlockGenerator.generateBlock(child, commentAlignment, mySettings));
                continue;
            }

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

    @Override
    protected Spacing getGoBlockSpacing(GoBlock child1, GoBlock child2) {
        // put only 1 space between assign block and the same line comment block
        if (child1 instanceof GoAssignBlock && isCommentBlock(child2) &&
            inTheSameLine(child1, child2)) {
            return BASIC_SPACING;
        }

        return null;
    }
}
