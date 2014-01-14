package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.expressions.GoExpressionList;
import ro.redeul.google.go.lang.psi.statements.GoAssignmentStatement;

import java.util.Map;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Alignments;

public class StatementAssign extends Code<GoAssignmentStatement> {

    private int childDepth = 1;

    public StatementAssign(GoAssignmentStatement assignStatement,
                           CommonCodeStyleSettings settings,
                           Indent indent,
                           Map<Alignments.Key, Alignment> alignmentsMap) {
        super(assignStatement, settings, indent, null, alignmentsMap);

        GoExpressionList leftSide = assignStatement.getLeftSideExpressions();
        GoExpressionList rightSide = assignStatement.getRightSideExpressions();

        if (leftSide != null && rightSide != null)
            if (leftSide.getExpressions().length > 1 && rightSide.getExpressions().length > 1)
                childDepth++;
    }

    @Nullable
    @Override
    protected Alignment getChildAlignment(@NotNull PsiElement child, @Nullable PsiElement prevChild, Map<Alignments.Key, Alignment> alignments) {
        if (child instanceof PsiComment)
            return alignments.get(Alignments.Key.Comments);

        return super.getChildAlignment(child, prevChild, alignments);
    }

    @Override
    protected Block customizeBlock(@NotNull Block childBlock, @NotNull PsiElement childPsi) {
        childBlock = super.customizeBlock(childBlock, childPsi);

        if ( childBlock instanceof ExpressionList) {
            ExpressionList expressionListBlock = (ExpressionList) childBlock;

            expressionListBlock.setDepth(childDepth);
        }

        return childBlock;
    }
}
