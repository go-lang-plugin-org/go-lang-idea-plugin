package ro.redeul.google.go.lang.psi.visitors;

import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.impl.types.GoPointerTypeImpl;
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

    public void visitImportDeclaration(GoImportDeclaration importDeclaration) {
        visitElement(importDeclaration);
    }

    public void visitImportSpec(GoImportSpec importSpec) {
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

    public void visitArrayType(GoArrayType arrayType) {
        visitElement(arrayType);
    }

    public void visitSliceType(GoSliceType sliceType) {
        visitElement(sliceType);
    }

    public void visitMapType(GoMapType mapType) {
        visitElement(mapType);
    }

    public void visitChannelType(GoChannelType channelType) {
        visitElement(channelType);
    }

    public void acceptTypeNameDeclaration(GoTypeNameDeclaration nameDeclaration) {
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

    public void visitPointerType(GoPointerType pointerType) {
        visitElement(pointerType);
    }
}
