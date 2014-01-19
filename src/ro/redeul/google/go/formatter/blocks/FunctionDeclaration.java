package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.*;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.util.GoPsiTextUtil;

import java.util.Map;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Alignments;
import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.CustomSpacings;
import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Spacings;
import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Spacings.*;

public class FunctionDeclaration extends Code<GoFunctionDeclaration> {

    private static final GoBlockUtil.CustomSpacing EMPTY_BLOCK_SPACING =
        GoBlockUtil.CustomSpacing.Builder()
            .none(pLCURLY, pRCURLY)
            .build();

    private static final GoBlockUtil.CustomSpacing NON_EMPTY_BLOCK_SPACING =
        GoBlockUtil.CustomSpacing.Builder()
            .set(pLCURLY, pRCURLY, LINE_HOLD_BREAKS)
            .build();

    private static final TokenSet FORCE_MULTILINE_CHILDS = TokenSet.create(
        LABELED_STATEMENT,
        FOR_WITH_CLAUSES_STATEMENT,
        FOR_WITH_CONDITION_STATEMENT,
        FOR_WITH_RANGE_AND_VARS_STATEMENT,
        FOR_WITH_RANGE_STATEMENT,
        SWITCH_EXPR_STATEMENT,
        SWITCH_TYPE_STATEMENT,
        IF_STATEMENT
    );

    public FunctionDeclaration(GoFunctionDeclaration psi,
                               CommonCodeStyleSettings settings,
                               Indent indent,
                               Alignment alignment,
                               Map<Alignments.Key, Alignment> alignmentsMap) {
        super(psi, settings, indent, null, alignmentsMap);

        withCustomSpacing(CustomSpacings.FUNCTION);
        withDefaultSpacing(Spacings.SPACE);
    }

    @Nullable
    @Override
    protected Alignment getChildAlignment(@NotNull PsiElement child, @Nullable PsiElement prevChild,
                                          Map<Alignments.Key, Alignment> alignments) {
        if (child instanceof PsiComment)
            return alignments.get(Alignments.Key.Comments);

        return super.getChildAlignment(child, prevChild, alignments);
    }


    @Override
    protected Block customizeBlock(@NotNull Block childBlock, @NotNull PsiElement childPsi) {
        childBlock = super.customizeBlock(childBlock, childPsi);

        if (childPsi == getPsi().getBlock() && childBlock instanceof StatementBlock) {
            GoFunctionDeclaration function = getPsi();
            StatementBlock block = (StatementBlock) childBlock;

            // if the block has not forced itself to be multiline we make it compact by default
            if (!block.isMultiLine())
                block.withCustomSpacing(CustomSpacings.STMT_BLOCK_COMPACT);

            // if however our signature is multiline we force the block to be multiline again
            TextRange range = GoPsiTextUtil.getFunctionSignatureRange(getPsi(), true);
            if ( range != null && StringUtil.containsLineBreak(range.substring(getPsi().getText()))) {
                block.setMultiLineMode(true, pLCURLY, pRCURLY);
                block.withCustomSpacing(CustomSpacings.STMT_BLOCK);
            }
        }

        return childBlock;
    }
}
