package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;

class GoParenthesisedExpressionBlock extends GoBlock {
    public GoParenthesisedExpressionBlock(ASTNode node, Alignment alignment, Indent indent, CommonCodeStyleSettings settings) {
        super(node, alignment, indent, null, settings);
    }

    @Override
    protected Indent getChildIndent(PsiElement child) {
        if (child instanceof GoExpr) {
            return NORMAL_INDENT_TO_CHILDREN;
        }
        return Indent.getNoneIndent();
    }
}
