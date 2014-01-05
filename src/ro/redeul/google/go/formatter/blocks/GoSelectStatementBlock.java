package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.statements.select.GoSelectStatement;

import java.util.Map;

/**
*
* <p/>
* Created on Jan-05-2014 01:06
*
* @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
*/
class GoSelectStatementBlock extends GoStatementBlock<GoSelectStatement> {

    private static final TokenSet HOLD_TOGETHER_TOKS = TokenSet.orSet(SELECT_CLAUSES, COMMENTS);

    public GoSelectStatementBlock(GoSelectStatement selectStatement, CommonCodeStyleSettings settings,
                                  Indent indent, Map<GoBlockUtil.Alignments.Key, Alignment> alignmentsMap) {
        super(selectStatement, settings, indent, alignmentsMap);

        setMultiLineMode(selectStatement.getCommClauses().length > 0, pLCURLY, pRCURLY);

        setLineBreakingTokens(HOLD_TOGETHER_TOKS);
        setHoldTogetherGroups(HOLD_TOGETHER_TOKS);
        setCustomSpacing(GoBlockUtil.CustomSpacing.Builder().setNone(pLCURLY, pRCURLY).build());
    }

}
