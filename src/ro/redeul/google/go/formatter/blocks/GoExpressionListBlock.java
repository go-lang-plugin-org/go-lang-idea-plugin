package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;

class GoExpressionListBlock extends GoBlock {
    public GoExpressionListBlock(ASTNode node, Alignment alignment, CommonCodeStyleSettings settings) {
        super(node, alignment, NORMAL_INDENT_TO_CHILDREN, null, settings);
    }

    @Override
    protected Indent getChildIndent(PsiElement child) {
        return Indent.getNoneIndent();
    }
}
