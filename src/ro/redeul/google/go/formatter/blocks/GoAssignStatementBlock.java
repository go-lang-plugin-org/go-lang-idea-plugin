package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.statements.GoAssignmentStatement;

import java.util.Map;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Alignments;

class GoAssignStatementBlock extends GoSyntheticBlock<GoAssignmentStatement> {

    public GoAssignStatementBlock(GoAssignmentStatement assignStatement,
                                  CommonCodeStyleSettings settings,
                                  Indent indent,
                                  Map<Alignments.Key, Alignment> alignmentsMap) {
        super(assignStatement, settings, indent, null, alignmentsMap);
    }

    @Nullable
    @Override
    protected Alignment getChildAlignment(@NotNull PsiElement child, @Nullable PsiElement prevChild, Map<Alignments.Key, Alignment> alignments) {
        if (child instanceof PsiComment)
            return alignments.get(Alignments.Key.Comments);

        return super.getChildAlignment(child, prevChild, alignments);
    }
}
