package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.statements.GoLabeledStatement;
import ro.redeul.google.go.lang.psi.statements.GoStatement;

import java.util.Map;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Indents;

/**
 * Formatting block for a labeled statement
 * <p/>
 * Created on Jan-04-2014 23:39
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
class GoLabeledStatementBlock extends GoSyntheticBlock<GoLabeledStatement> {

    private final static GoBlockUtil.CustomSpacing CUSTOM_SPACING =
        GoBlockUtil.CustomSpacing.Builder()
            .setNone(LITERAL_IDENTIFIER, oCOLON)
            .build();

    public static final TokenSet LINE_BREAKING_TOKENS =
        TokenSet.orSet(STMTS, COMMENTS, TokenSet.create(oCOLON));

    private static final TokenSet HOLD_TOGETHER_GROUP1 =
        TokenSet.orSet(COMMENTS, TokenSet.create(oCOLON));

    private static final TokenSet HOLD_TOGETHER_GROUP2 =
        TokenSet.orSet(STMTS, TokenSet.create(oCOLON));

    public GoLabeledStatementBlock(GoLabeledStatement labeledStatement, CommonCodeStyleSettings settings,
                                   Indent indent,
                                   Map<GoBlockUtil.Alignments.Key, Alignment> alignmentsMap) {
        super(labeledStatement, settings, indent, null, alignmentsMap);

        setLeadingCommentGroupIndent(Indents.NORMAL);
        setLineBreakingTokens(LINE_BREAKING_TOKENS);
        setHoldTogetherGroups(HOLD_TOGETHER_GROUP1, HOLD_TOGETHER_GROUP2);
        setCustomSpacing(CUSTOM_SPACING);
        setMultiLineMode(true, null, null);
    }

    @Override
    protected Indent getChildIndent(@NotNull PsiElement child, @Nullable PsiElement prevChild) {
        if (child instanceof GoStatement || child instanceof PsiComment)
            return Indents.NORMAL;

        return super.getChildIndent(child, prevChild);
    }

    @Nullable
    @Override
    protected Alignment getChildAlignment(@NotNull PsiElement child, @Nullable PsiElement prevChild, Map<GoBlockUtil.Alignments.Key, Alignment> alignments) {
        return null;
    }
}
