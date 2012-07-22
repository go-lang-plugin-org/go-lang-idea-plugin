package ro.redeul.google.go.lang.psi.visitors;

import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralBool;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralComposite;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralCompositeElement;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralCompositeValue;
import ro.redeul.google.go.lang.psi.expressions.primary.GoBuiltinCallExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoIndexExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.impl.statements.GoBreakStatementImpl;
import ro.redeul.google.go.lang.psi.impl.statements.GoContinueStatementImpl;
import ro.redeul.google.go.lang.psi.impl.statements.GoGotoStatementImpl;
import ro.redeul.google.go.lang.psi.impl.types.GoTypeInterfaceImpl;
import ro.redeul.google.go.lang.psi.statements.GoBreakStatement;
import ro.redeul.google.go.lang.psi.statements.GoContinueStatement;
import ro.redeul.google.go.lang.psi.statements.GoDeferStatement;
import ro.redeul.google.go.lang.psi.statements.GoForWithClausesStatement;
import ro.redeul.google.go.lang.psi.statements.GoForWithConditionStatement;
import ro.redeul.google.go.lang.psi.statements.GoForWithRangeStatement;
import ro.redeul.google.go.lang.psi.statements.GoGoStatement;
import ro.redeul.google.go.lang.psi.statements.GoGotoStatement;
import ro.redeul.google.go.lang.psi.statements.GoIfStatement;
import ro.redeul.google.go.lang.psi.statements.GoLabeledStatement;
import ro.redeul.google.go.lang.psi.statements.GoReturnStatement;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameterList;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodReceiver;
import ro.redeul.google.go.lang.psi.toplevel.GoPackageDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeArray;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeChannel;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeFunction;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeMap;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.types.GoPsiTypePointer;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeSlice;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeStruct;
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

    public void visitInterfaceType(GoPsiTypeInterfaceImpl type) {
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

    public void visitGotoStatement(GoGotoStatement statement) {
        visitElement(statement);
    }
}
