package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.parser.GoElementTypes;

import java.util.ArrayList;
import java.util.List;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Indents;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.*;

class GoTypeInterfaceBlock extends GoBlock {
    public GoTypeInterfaceBlock(ASTNode node, Alignment alignment, Indent indent, CommonCodeStyleSettings settings) {
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
            } else if (type == GoElementTypes.FUNCTION_DECLARATION) {
                fieldInCurrentLine = true;
                if (newLinesAfterLastField > 1 || fieldAlignment == null) {
                    fieldAlignment = Alignment.createAlignment(true);
                }
                newLinesAfterLastField = 0;
                children.add(new GoFunctionDeclarationBlock(child, fieldAlignment, Indent.getNormalIndent(), mySettings));
                continue;
            } else if (needAlignComment && fieldInCurrentLine && COMMENTS.contains(type)) {
                if (newLinesAfterLastComment > 1 || commentAlignment == null) {
                    commentAlignment = Alignment.createAlignment(true);
                }
                newLinesAfterLastComment = 0;
                children.add(GoBlocks.generate(child, mySettings, commentAlignment));
              continue;
            }

            Block childBlock;
            if (getIndentedElements().contains(type)) {
                childBlock = GoBlocks.generate(child, mySettings, Indents.NORMAL);
            } else {
                childBlock = GoBlocks.generate(child, mySettings);
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
