package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Indent;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.statements.GoStatement;

import java.util.EnumSet;
import java.util.Set;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.*;

public class StatementBlock extends Code<GoBlockStatement> {

    public static final Set<Alignments.Key> ALIGNMENT_KEYS =
        EnumSet.of(Alignments.Key.Comments);

    public static final TokenSet LINE_BREAKING_TOKENS =
        TokenSet.orSet(
            GoElementTypes.STMTS,
            GoElementTypes.COMMENTS
        );

    private final TokenSet INDENTED_STATEMENTS =
        TokenSet.orSet(TokenSet.andNot(GoElementTypes.STMTS, TokenSet.create(LABELED_STATEMENT)), GoElementTypes.COMMENTS);

    public StatementBlock(GoBlockStatement blockStatement,
                          CommonCodeStyleSettings settings,
                          Indent indent) {
        super(blockStatement, settings, indent);

        setMultiLineMode(shouldForceMultiline(), pLCURLY, pRCURLY);

        setLineBreakingTokens(LINE_BREAKING_TOKENS);
        withAlignmentKeys(ALIGNMENT_KEYS);
        setHoldTogetherGroups(LINE_BREAKING_TOKENS);
        withCustomSpacing(CustomSpacings.STMT_BLOCK);
    }

    private final TokenSet FORCE_MULTILINE_STATEMENTS = TokenSet.create(
        IF_STATEMENT,
        FOR_WITH_CLAUSES_STATEMENT, FOR_WITH_CONDITION_STATEMENT,
        FOR_WITH_RANGE_STATEMENT, FOR_WITH_RANGE_AND_VARS_STATEMENT,
        SWITCH_EXPR_STATEMENT, SWITCH_TYPE_STATEMENT,
        SELECT_STATEMENT,
        LABELED_STATEMENT
    );

    private boolean shouldForceMultiline() {

        if ( StringUtil.containsLineBreak(getPsi().getText()) ) {
            return true;
        }

        for (GoStatement statement : getPsi().getStatements()) {
            if ( FORCE_MULTILINE_STATEMENTS.contains(statement.getNode().getElementType())) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected Indent getChildIndent(@NotNull PsiElement child, @Nullable PsiElement prevChild) {
        IElementType childType = child.getNode().getElementType();

        if (childType == LABELED_STATEMENT || childType == pRCURLY || childType == pLCURLY)
            return Indents.NONE;

//        if (child instanceof PsiComment)
//            return Indents.NORMAL;

//        if (INDENTED_STATEMENTS.contains(childType))
        return Indents.NORMAL;

//        return super.getChildIndent(child, prevChild);
    }
}
