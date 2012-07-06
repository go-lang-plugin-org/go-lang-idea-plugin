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
import ro.redeul.google.go.lang.psi.impl.types.GoTypeInterfaceImpl;
import ro.redeul.google.go.lang.psi.statements.GoDeferStatement;
import ro.redeul.google.go.lang.psi.statements.GoForWithClausesStatement;
import ro.redeul.google.go.lang.psi.statements.GoForWithConditionStatement;
import ro.redeul.google.go.lang.psi.statements.GoForWithRangeStatement;
import ro.redeul.google.go.lang.psi.statements.GoGoStatement;
import ro.redeul.google.go.lang.psi.statements.GoIfStatement;
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
import ro.redeul.google.go.lang.psi.types.GoTypeArray;
import ro.redeul.google.go.lang.psi.types.GoTypeChannel;
import ro.redeul.google.go.lang.psi.types.GoTypeFunction;
import ro.redeul.google.go.lang.psi.types.GoTypeMap;
import ro.redeul.google.go.lang.psi.types.GoTypeName;
import ro.redeul.google.go.lang.psi.types.GoTypePointer;
import ro.redeul.google.go.lang.psi.types.GoTypeSlice;
import ro.redeul.google.go.lang.psi.types.GoTypeStruct;
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

    public void visitTypeName(GoTypeName typeName) {
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

    public void visitArrayType(GoTypeArray type) {
        visitElement(type);
    }

    public void visitSliceType(GoTypeSlice type) {
        visitElement(type);
    }

    public void visitMapType(GoTypeMap type) {
        visitElement(type);
    }

    public void visitChannelType(GoTypeChannel type) {
        visitElement(type);
    }

    public void visitPointerType(GoTypePointer type) {
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

    public void visitStructType(GoTypeStruct type) {
        visitElement(type);
    }

    public void visitInterfaceType(GoTypeInterfaceImpl type) {
        visitElement(type);
    }

    public void visitFunctionType(GoTypeFunction type) {
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
}
