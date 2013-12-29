package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.lang.parser.GoElementTypes;

import java.util.ArrayList;
import java.util.List;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.*;

class GoTypeStructBlock extends GoBlock {
    private final TokenSet NOINDENT = TokenSet.create(
            TYPE_SLICE,
            TYPE_MAP,
            TYPE_POINTER
    );

    public GoTypeStructBlock(ASTNode node, Alignment alignment, Indent indent, CommonCodeStyleSettings settings) {
        super(node, alignment, indent, null, settings);
    }

    @Override
    protected List<Block> buildChildren() {
        List<Block> children = new ArrayList<Block>();
        int newLinesAfterLastField = 0;
        int newLinesAfterLastComment = 0;
        Alignment fieldAlignment = null;
        Alignment commentAlignment = null;
        boolean needAlignComment = isNodeOfType(myNode.getPsi(), ALIGN_COMMENT_STATEMENTS);

        // Whether there is a field definition in current line.
        // We should only align those comments that after a field definition.
        boolean fieldInCurrentLine = false;

        for (ASTNode child : getGoChildren()) {
            if (child.getTextRange().getLength() == 0) {
                continue;
            }

            IElementType type = child.getElementType();
            if (isWhiteSpaceNode(child.getPsi())){
                if (isNewLineNode(child.getPsi())) {
                    newLinesAfterLastField += child.getTextLength();
                    newLinesAfterLastComment += child.getTextLength();
                    fieldInCurrentLine = false;
                }
                continue;
            } else if (type == GoElementTypes.TYPE_STRUCT_FIELD) {
                fieldInCurrentLine = true;
                if (newLinesAfterLastField > 1 || fieldAlignment == null) {
                    fieldAlignment = Alignment.createAlignment(true);
                }
                newLinesAfterLastField = 0;

                if (child.getTreeParent().getTreeParent().getTreeParent().getElementType() == GoElementTypes.TYPE_DECLARATIONS ||
                        NOINDENT.contains(child.getTreeParent().getTreeParent().getElementType())) {
                    children.add(new GoTypeStructFieldBlock(child, fieldAlignment, Indent.getNoneIndent(), mySettings));
                } else {
                    children.add(new GoTypeStructFieldBlock(child, fieldAlignment, Indent.getNormalIndent(), mySettings));
                }
                continue;
            } else if (needAlignComment && fieldInCurrentLine && COMMENTS.contains(type)) {
                if (newLinesAfterLastComment > 1 || commentAlignment == null) {
                    commentAlignment = Alignment.createAlignment(true);
                }
                newLinesAfterLastComment = 0;
                children.add(GoBlockGenerator.generateBlock(child, commentAlignment, mySettings));
                continue;
            } else if (type == GoElementTypes.pRCURLY) {
                if (child.getTreeParent().getTreeParent().getTreeParent().getElementType() == GoElementTypes.TYPE_DECLARATIONS ||
                        NOINDENT.contains(child.getTreeParent().getTreeParent().getElementType())) {
                    children.add(new GoTypeStructFieldBlock(child, fieldAlignment, Indent.getNoneIndent(), mySettings));
                } else {
                    children.add(new GoTypeStructFieldBlock(child, fieldAlignment, Indent.getNormalIndent(), mySettings));
                }
                continue;
            }

            Block childBlock;
            if (getIndentedElements().contains(type)) {
                childBlock = GoBlockGenerator.generateBlock(child, Indent.getNormalIndent(), mySettings);
            } else {
                childBlock = GoBlockGenerator.generateBlock(child, mySettings);
            }

            children.add(childBlock);
        }

        return children;
    }

    @Override
    public Spacing getGoBlockSpacing(GoBlock child1, GoBlock child2) {
        // put only 1 space between field block and the same line comment block
        if (child1 instanceof GoTypeStructFieldBlock && isCommentBlock(child2) &&
            inTheSameLine(child1, child2)) {
            return BASIC_SPACING;
        }

        return null;
    }
}
