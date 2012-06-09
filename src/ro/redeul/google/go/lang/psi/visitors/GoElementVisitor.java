package ro.redeul.google.go.lang.psi.visitors;

import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.GoIndexExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoFunctionLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.statements.GoForWithClausesStatement;
import ro.redeul.google.go.lang.psi.statements.GoForWithConditionStatement;
import ro.redeul.google.go.lang.psi.statements.GoForWithRangeStatement;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoPackageDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoTypeArray;
import ro.redeul.google.go.lang.psi.types.GoTypeChannel;
import ro.redeul.google.go.lang.psi.types.GoTypeMap;
import ro.redeul.google.go.lang.psi.types.GoTypeName;
import ro.redeul.google.go.lang.psi.types.GoTypePointer;
import ro.redeul.google.go.lang.psi.types.GoTypeSlice;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 30, 2010
 * Time: 8:10:51 PM
 */
public class GoElementVisitor {

    public void visitElement(GoPsiElement element) {
    }

    public void visitFile(GoFile file) {
        visitElement(file);
    }

    public void visitTypeName(GoTypeName typeName) {
        visitElement(typeName);
    }

    public void visitPackageDeclaration(GoPackageDeclaration packageDeclaration) {
        visitElement(packageDeclaration);
    }

    public void visitImportDeclarations(GoImportDeclarations importDeclaration) {
        visitElement(importDeclaration);
    }

    public void visitImportDeclaration(GoImportDeclaration importSpec) {
        visitElement(importSpec);
    }

    public void visitMethodDeclaration(GoMethodDeclaration methodDeclaration) {
        visitElement(methodDeclaration);
    }

    public void visitFunctionDeclaration(GoFunctionDeclaration functionDeclaration) {
        visitElement(functionDeclaration);
    }

    public void visitTypeDeclaration(GoTypeDeclaration typeDeclaration) {
        visitElement(typeDeclaration);
    }

    public void visitTypeSpec(GoTypeSpec typeSpec) {
        visitElement(typeSpec);
    }

    public void visitArrayType(GoTypeArray arrayType) {
        visitElement(arrayType);
    }

    public void visitSliceType(GoTypeSlice sliceType) {
        visitElement(sliceType);
    }

    public void visitMapType(GoTypeMap mapType) {
        visitElement(mapType);
    }

    public void visitChannelType(GoTypeChannel channelType) {
        visitElement(channelType);
    }

    public void visitTypeNameDeclaration(GoTypeNameDeclaration nameDeclaration) {
        visitElement(nameDeclaration);
    }

    public void visitIdentifier(GoIdentifier goIdentifier) {
        visitElement(goIdentifier);
    }

    public void acceptFunctionParameter(GoFunctionParameter functionParameter) {
        visitElement(functionParameter);
    }

    public void visitPointerType(GoTypePointer pointerType) {
        visitElement(pointerType);
    }

    public void visitLiteralExpr(GoLiteral literalExpr) {
        visitElement(literalExpr);
    }

    public void visitConstDeclarations(GoConstDeclarations constDeclarations) {
        visitElement(constDeclarations);
    }

    public void visitConstDeclaration(GoConstDeclaration constDeclaration) {
        visitElement(constDeclaration);
    }

    public void visitFunctionLiteral(GoFunctionLiteral functionLiteral) {
        visitElement(functionLiteral);
    }

    public void visitForWithRange(GoForWithRangeStatement forWithRange) {
        visitElement(forWithRange);
    }

    public void visitForWithClauses(GoForWithClausesStatement forWithClauses) {
        visitElement(forWithClauses);
    }

    public void visitForWithCondition(GoForWithConditionStatement forWithCondition) {
        visitElement(forWithCondition);
    }

    public void visitVarDeclaration(GoVarDeclaration varDeclaration) {
        visitElement(varDeclaration);
    }

    public void visitShortVarDeclaration(GoShortVarDeclaration shortVarDeclaration) {
        visitElement(shortVarDeclaration);
    }

    public void visitIndexExpression(GoIndexExpression indexExpression) {
        visitElement(indexExpression);
    }
}
