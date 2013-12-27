package ro.redeul.google.go.lang.psi.visitors;

import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.binary.GoBinaryExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralBool;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralComposite;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralCompositeElement;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralCompositeValue;
import ro.redeul.google.go.lang.psi.expressions.primary.*;
import ro.redeul.google.go.lang.psi.impl.statements.GoForWithRangeAndVarsStatementImpl;
import ro.redeul.google.go.lang.psi.statements.*;
import ro.redeul.google.go.lang.psi.statements.select.GoSelectCommClauseDefault;
import ro.redeul.google.go.lang.psi.statements.select.GoSelectCommClauseRecv;
import ro.redeul.google.go.lang.psi.statements.select.GoSelectCommClauseSend;
import ro.redeul.google.go.lang.psi.statements.select.GoSelectStatement;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchTypeGuard;
import ro.redeul.google.go.lang.psi.toplevel.*;
import ro.redeul.google.go.lang.psi.types.*;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 30, 2010
 * Time: 8:10:51 PM
 */
public class GoElementVisitor  {

    public void visitElement(GoPsiElement element) {
    }

    public void visitFile(GoFile file) {
        visitElement(file);
    }

    public void visitTypeName(GoPsiTypeName typeName) {
        visitElement(typeName);
    }

    public void visitPackageDeclaration(GoPackageDeclaration declaration) {
        visitElement(declaration);
    }

    public void visitImportDeclarations(GoImportDeclarations declarations) {
        visitElement(declarations);
    }

    public void visitImportDeclaration(GoImportDeclaration declaration) {
        visitElement(declaration);
    }

    public void visitMethodDeclaration(GoMethodDeclaration declaration) {
        visitElement(declaration);
    }

    public void visitFunctionDeclaration(GoFunctionDeclaration declaration) {
        visitElement(declaration);
    }

    public void visitTypeDeclaration(GoTypeDeclaration declaration) {
        visitElement(declaration);
    }

    public void visitTypeSpec(GoTypeSpec type) {
        visitElement(type);
    }

    public void visitArrayType(GoPsiTypeArray type) {
        visitElement(type);
    }

    public void visitSliceType(GoPsiTypeSlice type) {
        visitElement(type);
    }

    public void visitMapType(GoPsiTypeMap type) {
        visitElement(type);
    }

    public void visitChannelType(GoPsiTypeChannel type) {
        visitElement(type);
    }

    public void visitPointerType(GoPsiTypePointer type) {
        visitElement(type);
    }

    public void visitTypeNameDeclaration(GoTypeNameDeclaration declaration) {
        visitElement(declaration);
    }

    public void visitLiteralIdentifier(GoLiteralIdentifier identifier) {
        visitElement(identifier);
    }

    public void visitFunctionParameter(GoFunctionParameter parameter) {
        visitElement(parameter);
    }

    public void visitLiteralExpression(GoLiteralExpression expression) {
        visitElement(expression);
    }

    public void visitConstDeclarations(GoConstDeclarations declarations) {
        visitElement(declarations);
    }

    public void visitConstDeclaration(GoConstDeclaration declaration) {
        visitElement(declaration);
    }

    public void visitFunctionLiteral(GoLiteralFunction literal) {
        visitElement(literal);
    }

    public void visitForWithRange(GoForWithRangeStatement statement) {
        visitElement(statement);
    }

    public void visitForWithRangeAndVars(GoForWithRangeAndVarsStatement statement) {
        visitElement(statement);
    }

    public void visitForWithClauses(GoForWithClausesStatement statement) {
        visitElement(statement);
    }

    public void visitForWithCondition(GoForWithConditionStatement statement) {
        visitElement(statement);
    }

    public void visitVarDeclaration(GoVarDeclaration declaration) {
        visitElement(declaration);
    }

    public void visitShortVarDeclaration(GoShortVarDeclaration declaration) {
        visitElement(declaration);
    }

    public void visitIndexExpression(GoIndexExpression expression) {
        visitElement(expression);
    }

    public void visitLiteralCompositeVal(GoLiteralCompositeValue compositeValue) {
        visitElement(compositeValue);
    }

    public void visitLiteralComposite(GoLiteralComposite composite) {
        visitElement(composite);
    }

    public void visitIfStatement(GoIfStatement statement) {
        visitElement(statement);
    }

    public void visitGoStatement(GoGoStatement statement) {
        visitElement(statement);
    }

    public void visitDeferStatement(GoDeferStatement statement) {
        visitElement(statement);
    }

    public void visitBuiltinCallExpression(GoBuiltinCallExpression expression) {
        visitElement(expression);
    }

    public void visitLiteralBool(GoLiteralBool literal) {
        visitElement(literal);
    }

    public void visitReturnStatement(GoReturnStatement statement) {
        visitElement(statement);
    }

    public void visitCallOrConvExpression(GoCallOrConvExpression expression) {
        visitElement(expression);
    }

    public void visitMethodReceiver(GoMethodReceiver receiver) {
        visitElement(receiver);
    }

    public void visitStructType(GoPsiTypeStruct type) {
        visitElement(type);
    }

    public void visitInterfaceType(GoPsiTypeInterface type) {
        visitElement(type);
    }

    public void visitFunctionType(GoPsiTypeFunction type) {
        visitElement(type);
    }

    public void visitTypeStructField(GoTypeStructField field) {
        visitElement(field);
    }

    public void visitFunctionParameterList(GoFunctionParameterList list) {
        visitElement(list);
    }

    public void visitTypeStructAnonymousField(GoTypeStructAnonymousField field) {
        visitElement(field);
    }

    public void visitLiteralCompositeElement(GoLiteralCompositeElement element) {
        visitElement(element);
    }

    public void visitLabeledStatement(GoLabeledStatement statement) {
        visitElement(statement);
    }

    public void visitBreakStatement(GoBreakStatement statement) {
        visitElement(statement);
    }

    public void visitContinueStatement(GoContinueStatement statement) {
        visitElement(statement);
    }

    public void visitSelectorExpression(GoSelectorExpression expression) {
        visitElement(expression);
    }

    public void visitGotoStatement(GoGotoStatement statement) {
        visitElement(statement);
    }

    public void visitAssignment(GoAssignmentStatement statement) {
        visitElement(statement);
    }

    public void visitSwitchTypeGuard(GoSwitchTypeGuard typeGuard) {
        visitElement(typeGuard);
    }

    public void visitSelectStatement(GoSelectStatement statement) {
        visitElement(statement);
    }

    public void visitSelectCommClauseDefault(GoSelectCommClauseDefault commClause) {
        visitElement(commClause);
    }

    public void visitSelectCommClauseRecv(GoSelectCommClauseRecv commClause) {
        visitElement(commClause);
    }

    public void visitSelectCommClauseSend(GoSelectCommClauseSend commClause) {
        visitElement(commClause);
    }

    public void visitSliceExpression(GoSliceExpression expression) {
        visitElement(expression);
    }

    public void visitSendStatement(GoSendStatement statement) {
        visitElement(statement);
    }

    public void visitBinaryExpression(GoBinaryExpression expression) {
        visitElement(expression);
    }
}
