package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 *         Date: 6/3/12
 */
class GoFileBlock extends GoBlock {
    private static final TokenSet NEED_NEW_LINE_TOKENS = TokenSet.create(
        PACKAGE_DECLARATION,
        IMPORT_DECLARATIONS,
        CONST_DECLARATIONS,
        VAR_DECLARATIONS,
        TYPE_DECLARATIONS,
        FUNCTION_DECLARATION,
        METHOD_DECLARATION
    );

    public GoFileBlock(ASTNode node, Alignment alignment, Indent indent,
                       Wrap wrap, CommonCodeStyleSettings settings) {
        super(node, alignment, indent, wrap, settings);
    }

    // nothing should be indented on the top level of file
    @Override
    protected TokenSet getIndentedElements() {
        return TokenSet.EMPTY;
    }

    @Override
    public Spacing getSpacing(Block child1, @NotNull Block child2) {
        if (!(child1 instanceof GoBlock)) {
            return null;
        }

        IElementType type1 = ((GoBlock) child1).getNode().getElementType();
        if (NEED_NEW_LINE_TOKENS.contains(type1)) {
            if (child2 instanceof GoBlock) {
                // 2 consecutive same type statements could be put together
                // e.g. several const declarations could be put together without blank lines
                IElementType type2 = ((GoBlock) child2).getNode().getElementType();
                if (type1 == type2) {
                    return null;
                }
            }

            return LINE_SPACING;
        }

        return null;
    }
}
