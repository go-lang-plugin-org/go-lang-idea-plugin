package ro.redeul.google.go.formatter.builder;

import com.intellij.formatting.Block;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.formatter.TokenSets;
import ro.redeul.google.go.formatter.blocks.*;
import ro.redeul.google.go.lang.psi.statements.*;
import ro.redeul.google.go.lang.psi.statements.select.GoSelectCommClause;
import ro.redeul.google.go.lang.psi.statements.select.GoSelectStatement;
import ro.redeul.google.go.lang.psi.statements.switches.*;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.CustomSpacings;
import static ro.redeul.google.go.lang.lexer.GoTokenTypes.oCOLON;
import static ro.redeul.google.go.lang.lexer.GoTokenTypes.pLCURLY;
import static ro.redeul.google.go.lang.lexer.GoTokenTypes.pRCURLY;
import static ro.redeul.google.go.lang.parser.GoElementTypes.STMTS_OR_COMMENTS;
import static ro.redeul.google.go.lang.parser.GoElementTypes.SWITCH_EXPR_CASE;
import static ro.redeul.google.go.lang.parser.GoElementTypes.SWITCH_TYPE_CASE;

/**
 * <p/>
 * Created on Jan-13-2014 22:21
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
abstract class Statements extends TopLevel {

    @Override
    public Block visitStatement(GoStatement statement, State s) {
        return new Statement<GoStatement>(statement, s.settings, s.indent, s.alignmentsMap);
    }

    @Override
    public Block visitStatementReturn(GoReturnStatement statement, State s) {
        return new Statement<GoReturnStatement>(statement, s.settings, s.indent, s.alignmentsMap);
    }

    @Override
    public Block visitStatementBlock(GoBlockStatement statement, State s) {
        return new StatementBlock(statement, s.settings, s.indent);
    }

    @Override
    public Block visitStatementAssign(GoAssignmentStatement assign, State s) {
        return new StatementAssign(assign, s.settings, s.indent, s.alignmentsMap);
    }

    @Override
    public Block visitStatementSwitchExpression(GoSwitchExpressionStatement statement, State s) {
        return new Code<GoSwitchExpressionStatement>(statement, s.settings, s.indent, null, s.alignmentsMap)
            .setMultiLineMode(true, pLCURLY, pRCURLY)
            .withDefaultSpacing(GoBlockUtil.Spacings.SPACE)
            .setLineBreakingTokens(TokenSet.create(SWITCH_EXPR_CASE))
            .setHoldTogetherGroups(TokenSet.create(SWITCH_EXPR_CASE))
            .withCustomSpacing(CustomSpacings.STMT_SWITCH_EXPR);
    }

    @Override
    public Block visitSwitchExpressionClause(GoSwitchExpressionClause clause, State s) {
        return new Code<GoSwitchExpressionClause>(clause, s.settings, s.indent, null, s.alignmentsMap)
            .setMultiLineMode(true, oCOLON, null)
            .withDefaultSpacing(GoBlockUtil.Spacings.SPACE)
            .withCustomSpacing(CustomSpacings.CLAUSES_COLON)
            .setLineBreakingTokens(STMTS_OR_COMMENTS)
            .setIndentedChildTokens(STMTS_OR_COMMENTS);
    }

    @Override
    public Block visitStatementSwitchType(GoSwitchTypeStatement statement, State s) {
        return new Code<GoSwitchTypeStatement>(statement, s.settings, s.indent, null, s.alignmentsMap)
            .setMultiLineMode(true, pLCURLY, pRCURLY)
            .withDefaultSpacing(GoBlockUtil.Spacings.SPACE)
            .setLineBreakingTokens(TokenSet.create(SWITCH_TYPE_CASE))
            .setHoldTogetherGroups(TokenSet.create(SWITCH_TYPE_CASE))
            .withCustomSpacing(CustomSpacings.STMT_SWITCH_TYPE);
    }

    @Override
    public Block visitSwitchTypeClause(GoSwitchTypeClause clause, State s) {
        return new Code<GoSwitchTypeClause>(clause, s.settings, s.indent, null, s.alignmentsMap)
            .setMultiLineMode(true, oCOLON, null)
            .withDefaultSpacing(GoBlockUtil.Spacings.SPACE)
            .withCustomSpacing(CustomSpacings.CLAUSES_COLON)
            .setLineBreakingTokens(STMTS_OR_COMMENTS)
            .setIndentedChildTokens(STMTS_OR_COMMENTS);
    }

    @Override
    public Block visitSwitchTypeGuard(GoSwitchTypeGuard guard, State s) {
        return new Code<GoSwitchTypeGuard>(guard, s.settings, s.indent)
            .withDefaultSpacing(GoBlockUtil.Spacings.SPACE)
            .withCustomSpacing(CustomSpacings.SWITCH_TYPE_GUARD);
    }

    @Override
    public Block visitStatementLabeled(GoLabeledStatement statement, State s) {
        return new StatementLabeled(statement, s.settings, s.indent, s.alignmentsMap);
    }

    @Override
    public Block visitStatementIncDec(GoIncDecStatement statement, State s) {
        return new Statement<GoIncDecStatement>(statement, s.settings, s.indent, s.alignmentsMap)
            .withCustomSpacing(CustomSpacings.STMT_INC_DEC);
    }

    @Override
    public Block visitStatementIf(GoIfStatement statement, State s) {
        return new Statement<GoIfStatement>(statement, s.settings, s.indent, s.alignmentsMap)
            .withDefaultSpacing(GoBlockUtil.Spacings.SPACE)
            .withCustomSpacing(CustomSpacings.STMT_IF);
    }

    @Override
    public Block visitStatementFor(GoForStatement statement, State s) {
        return new Statement<GoForStatement>(statement, s.settings, s.indent, s.alignmentsMap)
            .withCustomSpacing(CustomSpacings.STMT_FOR);
    }

    @Override
    public Block visitStatementSelect(GoSelectStatement statement, State s) {
        return new Statement<GoSelectStatement>(statement, s.settings, s.indent, s.alignmentsMap)
            .setMultiLineMode(statement.getCommClauses().length > 0, pLCURLY, pRCURLY)
            .setLineBreakingTokens(TokenSets.STMT_SELECT_HOLD_TOGETHER)
            .setHoldTogetherGroups(TokenSets.STMT_SELECT_HOLD_TOGETHER)
            .withCustomSpacing(CustomSpacings.STMT_SELECT);
    }

    @Override
    public Block visitSelectCommClause(GoSelectCommClause clause, State s) {
        return new Code<GoSelectCommClause>(clause, s.settings, s.indent, null, s.alignmentsMap)
            .setMultiLineMode(true, oCOLON, null)
            .withDefaultSpacing(GoBlockUtil.Spacings.SPACE)
            .withCustomSpacing(CustomSpacings.CLAUSES_COLON)
            .setLineBreakingTokens(STMTS_OR_COMMENTS)
            .setIndentedChildTokens(STMTS_OR_COMMENTS);
    }
}
