package ro.redeul.google.go.lang.psi.visitors;

import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.impl.GoIdentifierImpl;
import ro.redeul.google.go.lang.psi.impl.toplevel.GoTypeDeclarationImpl;
import ro.redeul.google.go.lang.psi.impl.toplevel.GoTypeNameDeclarationImpl;
import ro.redeul.google.go.lang.psi.impl.toplevel.GoTypeSpecImpl;
import ro.redeul.google.go.lang.psi.impl.types.GoArrayTypeImpl;
import ro.redeul.google.go.lang.psi.impl.types.GoChannelTypeImpl;
import ro.redeul.google.go.lang.psi.impl.types.GoMapTypeImpl;
import ro.redeul.google.go.lang.psi.impl.types.GoSliceTypeImpl;
import ro.redeul.google.go.lang.psi.toplevel.*;
import ro.redeul.google.go.lang.psi.types.*;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 30, 2010
 * Time: 8:10:51 PM
 * To change this template use File | Settings | File Templates.
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

    public void visitIdentifier(GoIdentifierImpl goIdentifier) {
        visitElement(goIdentifier);
    }
}
