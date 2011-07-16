package ro.redeul.google.go.lang.psi.visitors;

import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.toplevel.*;
import ro.redeul.google.go.lang.psi.types.*;

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

    public void visitImportDeclaration(GoImportDeclarations importDeclaration) {
        visitElement(importDeclaration);
    }

    public void visitImportSpec(GoImportDeclaration importSpec) {
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

    public void acceptFunctionParameterList(GoFunctionParameterList goFunctionParameterList) {
        visitElement(goFunctionParameterList);
    }

    public void acceptFunctionParameter(GoFunctionParameter functionParameter) {
        visitElement(functionParameter);
    }

    public void visitPointerType(GoTypePointer pointerType) {
        visitElement(pointerType);
    }

    public void visitLiteralExpr(GoLiteralExpression literalExpr) {
        visitElement(literalExpr);
    }
}
