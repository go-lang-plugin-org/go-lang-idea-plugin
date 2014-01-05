package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;

import java.util.EnumSet;
import java.util.Set;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Alignments;
import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Indents;

class GoBlockStatementBlock extends GoSyntheticBlock<GoBlockStatement> {

    private static final GoBlockUtil.CustomSpacing MULTI_LINE_SPACING =
        GoBlockUtil.CustomSpacing.Builder()
            .set(pLCURLY, pRCURLY, Spacing.createSpacing(0, 0, 1, true, 1))
            .build();

    private static final GoBlockUtil.CustomSpacing SAME_LINE_SPACING =
        GoBlockUtil.CustomSpacing.Builder()
            .setNone(pLCURLY, pRCURLY)
            .build();

    public static final Set<Alignments.Key> ALIGNMENT_KEYS =
        EnumSet.of(Alignments.Key.Comments);

    public static final TokenSet LINE_BREAKING_TOKENS =
        TokenSet.orSet(
            GoElementTypes.STATEMENTS,
            GoElementTypes.COMMENTS
        );

    private final TokenSet INDENTED_STATEMENTS =
        TokenSet.andNot(GoElementTypes.STATEMENTS, TokenSet.create(LABELED_STATEMENT));


    public GoBlockStatementBlock(GoBlockStatement blockStatement,
                                 CommonCodeStyleSettings settings,
                                 Indent indent) {
        super(blockStatement, settings, indent);

        setMultiLineMode(StringUtil.containsLineBreak(blockStatement.getText()), pLCURLY, pRCURLY);

        setLineBreakingTokens(LINE_BREAKING_TOKENS);
        setAlignmentKeys(ALIGNMENT_KEYS);
        setHoldTogetherGroups(LINE_BREAKING_TOKENS);

        if (isMultiLine()) {
            setCustomSpacing(MULTI_LINE_SPACING);
        } else {
            setCustomSpacing(SAME_LINE_SPACING);
        }
    }

    @Override
    protected Indent getChildIndent(@NotNull PsiElement child, @Nullable PsiElement prevChild) {
        IElementType childType = child.getNode().getElementType();

        if (child instanceof PsiComment)
            return Indents.NORMAL;

        if (INDENTED_STATEMENTS.contains(childType))
            return Indents.NORMAL;

        if (childType == LABELED_STATEMENT)
            return Indents.NONE;

        return super.getChildIndent(child, prevChild);
    }
}
