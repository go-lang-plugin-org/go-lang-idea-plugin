package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import org.jetbrains.annotations.Nullable;

class GoBlockStatementBlock extends GoBlock {
    public GoBlockStatementBlock(ASTNode node, Indent indent, CommonCodeStyleSettings settings) {
        super(node, null, indent, null, settings);
    }

    @Override
    protected Indent getChildIndent(@Nullable PsiElement child) {
        if (child == null) {
            return Indent.getNormalIndent();
        }

        String text = child.getText();
        if ("{".equals(text) || "}".equals(text)) {
            return Indent.getNoneIndent();
        }
        return Indent.getNormalIndent();
    }

    @Override
    protected Spacing getGoBlockSpacing(GoBlock child1, GoBlock child2) {
        if (child1 instanceof GoBlockStatementBlock || child2 instanceof GoBlockStatementBlock) {
            return ONE_LINE_SPACING_KEEP_LINE_BREAKS;
        }
        return super.getGoBlockSpacing(child1, child2);
    }
}
