package ro.redeul.google.go.lang.psi.visitors;

import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.GoPsiElementList;
import ro.redeul.google.go.lang.psi.declarations.GoConstSpec;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.declarations.GoVarSpec;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclarations;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoExpressionList;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.GoUnaryExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.*;
import ro.redeul.google.go.lang.psi.expressions.literals.*;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralComposite;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralCompositeElement;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralCompositeValue;
import ro.redeul.google.go.lang.psi.expressions.primary.*;
import ro.redeul.google.go.lang.psi.statements.*;
import ro.redeul.google.go.lang.psi.statements.select.*;
import ro.redeul.google.go.lang.psi.statements.switches.*;
import ro.redeul.google.go.lang.psi.toplevel.*;
import ro.redeul.google.go.lang.psi.types.*;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;

/**
 * Go Type visitor which can return stuff and also which can pass stuff in.
 * It knows how to fall back to the class hierarchy when needed, eg: GoUnaryExpression -> GoPrimaryExpression ->
 * GoExpression.
 *
 * <p/>
 * Created on Jan-13-2014 18:38
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class GoTypedVisitor<T, S> {

    public T visitElement(GoPsiElement element, S s) {
        return null;
    }

    //
    // top level
    //
    public T visitFile(GoFile file, S s) {
        return visitElement(file, s);
    }

    public T visitPackageDeclaration(GoPackageDeclaration declaration, S s) {
        return visitElement(declaration, s);
    }

    public T visitImportDeclaration(GoImportDeclarations declarations, S s) {
        return visitElement(declarations, s);
    }

    public T visitImportSpec(GoImportDeclaration spec, S s) {
        return visitElement(spec, s);
    }

    public T visitConstDeclaration(GoConstDeclarations declaration, S s) {
        return visitElement(declaration, s);
    }

    public T visitConstSpec(GoConstSpec spec, S s) {
        return visitElement(spec, s);
    }

    public T visitVarDeclaration(GoVarDeclarations declaration, S s) {
        return visitElement(declaration, s);
    }

    public T visitVarSpec(GoVarSpec spec, S s) {
        return visitElement(spec, s);
    }

    public T visitTypeDeclaration(GoTypeDeclaration declaration, S s) {
        return visitElement(declaration, s);
    }

    public T visitTypeSpec(GoTypeSpec spec, S s) {
        return visitElement(spec, s);
    }

    public T visitTypeNameDeclaration(GoTypeNameDeclaration declaration, S s) {
        return visitElement(declaration, s);
    }

    public T visitFunctionDeclaration(GoFunctionDeclaration declaration, S s) {
        return visitElement(declaration, s);
    }

    public T visitMethodReceiver(GoMethodReceiver receiver, S s) {
        return visitElement(receiver, s);
    }

    public T visitFunctionParameterList(GoFunctionParameterList list, S s) {
        return visitElementList(list, s);
    }

    public T visitFunctionParameter(GoFunctionParameter parameter, S s) {
        return visitElement(parameter, s);
    }

    public T visitFunctionResult(GoFunctionResult result, S s) {
        return visitElement(result, s);
    }

    //
    // statements
    //
    public T visitStatement(GoStatement statement, S s) {
        return visitElement(statement, s);
    }

    public T visitStatementBlock(GoBlockStatement statement, S s) {
        return visitStatement(statement, s);
    }

    public T visitStatementAssign(GoAssignmentStatement assign, S s) {
        return visitStatement(assign, s);
    }

    public T visitStatementIncDec(GoIncDecStatement statement, S s) {
        return visitStatement(statement, s);
    }

    public T visitStatementSend(GoSendStatement statement, S s) {
        return visitStatement(statement, s);
    }

    public T visitStatementExpression(GoExpressionStatement statement, S s) {
        return visitStatement(statement, s);
    }

    public T visitStatementFallthrough(GoFallthroughStatement statement, S s) {
        return visitStatement(statement, s);
    }

    public T visitStatementBreak(GoBreakStatement statement, S s) {
        return visitStatement(statement, s);
    }

    public T visitStatementContinue(GoContinueStatement statement, S s) {
        return visitStatement(statement, s);
    }

    public T visitStatementDefer(GoDeferStatement statement, S s) {
        return visitStatement(statement, s);
    }

    public T visitStatementGoto(GoGotoStatement statement, S s) {
        return visitStatement(statement, s);
    }

    public T visitStatementReturn(GoReturnStatement statement, S s) {
        return visitStatement(statement, s);
    }

    public T visitStatementGo(GoGoStatement statement, S s) {
        return visitStatement(statement, s);
    }

    public T visitStatementLabeled(GoLabeledStatement statement, S s) {
        return visitStatement(statement, s);
    }

    public T visitStatementIf(GoIfStatement statement, S s) {
        return visitStatement(statement, s);
    }

    public T visitStatementFor(GoForStatement statement, S s) {
        return visitStatement(statement, s);
    }

    public T visitStatementForWithClauses(GoForWithClausesStatement statement, S s) {
        return visitStatementFor(statement, s);
    }

    public T visitStatementForWithCondition(GoForWithConditionStatement statement, S s) {
        return visitStatementFor(statement, s);
    }

    public T visitStatementForWithRange(GoForWithRangeStatement statement, S s) {
        return visitStatementFor(statement, s);
    }

    public T visitStatementForWithRangeAndVars(GoForWithRangeAndVarsStatement statement, S s) {
        return visitStatementFor(statement, s);
    }

    public T visitStatementSelect(GoSelectStatement statement, S s) {
        return visitStatement(statement, s);
    }

    public T visitSelectCommClause(GoSelectCommClause clause, S s) {
        return visitElement(clause, s);
    }

    public T visitSelectCommClauseSend(GoSelectCommClauseSend clause, S s) {
        return visitSelectCommClause(clause, s);
    }

    public T visitSelectCommClauseRecv(GoSelectCommClauseRecv clause, S s) {
        return visitSelectCommClause(clause, s);
    }

    public T visitSelectCommClauseDefault(GoSelectCommClauseDefault clause, S s) {
        return visitSelectCommClause(clause, s);
    }

    public T visitStatementSwitchExpression(GoSwitchExpressionStatement statement, S s) {
        return visitStatement(statement, s);
    }

    public T visitSwitchExpressionClause(GoSwitchExpressionClause clause, S s) {
        return visitElement(clause, s);
    }

    public T visitStatementSwitchType(GoSwitchTypeStatement statement, S s) {
        return visitStatement(statement, s);
    }

    public T visitSwitchTypeClause(GoSwitchTypeClause clause, S s) {
        return visitElement(clause, s);
    }

    public T visitSwitchTypeGuard(GoSwitchTypeGuard guard, S s) {
        return visitElement(guard, s);
    }

    //
    // types
    //
    public T visitType(GoPsiType type, S s) {
        return visitElement(type, s);
    }

    public T visitTypeList(GoPsiTypeList list, S s) {
        return visitElementList(list, s);
    }

    public T visitTypeParenthesized(GoPsiTypeParenthesized type, S s) {
        return visitType(type, s);
    }

    public T visitTypeName(GoPsiTypeName type, S s) {
        return visitType(type, s);
    }

    public T visitTypeArray(GoPsiTypeArray type, S s) {
        return visitType(type, s);
    }

    public T visitTypePointer(GoPsiTypePointer type, S s) {
        return visitType(type, s);
    }

    public T visitTypeSlice(GoPsiTypeSlice type, S s) {
        return visitType(type, s);
    }

    public T visitTypeMap(GoPsiTypeMap type, S s) {
        return visitType(type, s);
    }

    public T visitTypeChannel(GoPsiTypeChannel type, S s) {
        return visitType(type, s);
    }

    public T visitTypeStruct(GoPsiTypeStruct type, S s) {
        return visitType(type, s);
    }

    public T visitTypeStructField(GoTypeStructField typeStructField, S s) {
        return visitElement(typeStructField, s);
    }

    public T visitTypeStructAnonymousField(GoTypeStructAnonymousField field, S s) {
        return visitElement(field, s);
    }

    public T visitTypeInterface(GoPsiTypeInterface type, S s) {
        return visitType(type, s);
    }

    public T visitTypeFunction(GoPsiTypeFunction type, S s) {
        return visitType(type, s);
    }

    //
    // expressions
    //
    public T visitExpression(GoExpr expression, S s) {
        return visitElement(expression, s);
    }

    public T visitExpressionList(GoExpressionList expressions, S s) {
        return visitElementList(expressions, s);
    }

    public T visitExpressionBinary(GoBinaryExpression<?> expression, S s) {
        return visitElement(expression, s);
    }

    public T visitExpressionAdditive(GoAdditiveExpression expression, S s) {
        return visitExpressionBinary(expression, s);
    }

    public T visitExpressionMultiplicative(GoMultiplicativeExpression expression, S s) {
        return visitExpressionBinary(expression, s);
    }

    public T visitExpressionRelational(GoRelationalExpression expression, S s) {
        return visitExpressionBinary(expression, s);
    }

    public T visitExpressionLogicalOr(GoLogicalOrExpression expression, S s) {
        return visitExpressionBinary(expression, s);
    }

    public T visitExpressionLogicalAnd(GoLogicalAndExpression expression, S s) {
        return visitExpressionBinary(expression, s);
    }

    public T visitExpressionUnary(GoUnaryExpression expression, S s) {
        return visitExpression(expression, s);
    }

    private T visitExpressionPrimary(GoPrimaryExpression expression, S s) {
        return visitExpressionUnary(expression, s);
    }

    public T visitExpressionIndex(GoIndexExpression expression, S s) {
        return visitExpressionPrimary(expression, s);
    }

    public T visitExpressionLiteral(GoLiteralExpression expression, S s) {
        return visitExpressionPrimary(expression, s);
    }

    public T visitExpressionSlice(GoSliceExpression expression, S s) {
        return visitExpressionPrimary(expression, s);
    }

    public T visitExpressionSelector(GoSelectorExpression expression, S s) {
        return visitExpressionPrimary(expression, s);
    }

    public T visitExpressionCallOrConversion(GoCallOrConvExpression expression, S s) {
        return visitExpressionPrimary(expression, s);
    }

    public T visitExpressionBuiltinCall(GoBuiltinCallExpression expression, S s) {
        return visitExpressionCallOrConversion(expression, s);
    }

    public T visitExpressionTypeAssertion(GoTypeAssertionExpression expression, S s) {
        return visitExpressionPrimary(expression, s);
    }

    public T visitExpressionParenthesised(GoParenthesisedExpression expression, S s) {
        return visitExpressionPrimary(expression, s);
    }

    //
    // literals
    //
    public T visitLiteral(GoLiteral<?> literal, S s) {
        return visitElement(literal, s);
    }

    public T visitLiteralBool(GoLiteralBool literal, S s) {
        return visitLiteral(literal, s);
    }

    public T visitLiteralChar(GoLiteralChar literal, S s) {
        return visitLiteral(literal, s);
    }

    public T visitLiteralInteger(GoLiteralInteger literal, S s) {
        return visitLiteral(literal, s);
    }

    public T visitLiteralFloat(GoLiteralFloat literal, S s) {
        return visitLiteral(literal, s);
    }

    public T visitLiteralImaginary(GoLiteralImaginary literal, S s) {
        return visitLiteral(literal, s);
    }

    public T visitLiteralIdentifier(GoLiteralIdentifier literal, S s) {
        return visitLiteral(literal, s);
    }

    public T visitLiteralString(GoLiteralString literal, S s) {
        return visitLiteral(literal, s);
    }

    public T visitLiteralFunction(GoLiteralFunction literal, S s) {
        return visitLiteral(literal, s);
    }

    public T visitLiteralComposite(GoLiteralComposite literal, S s) {
        return visitLiteral(literal, s);
    }

    public T visitLiteralCompositeValue(GoLiteralCompositeValue value, S s) {
        return visitElement(value, s);
    }

    public T visitLiteralCompositeElement(GoLiteralCompositeElement element, S s) {
        return visitElement(element, s);
    }

    //
    // list of elements
    //
    public T visitElementList(GoPsiElementList<? extends GoPsiElement> list, S s) {
        return visitElement(list, s);
    }
}
