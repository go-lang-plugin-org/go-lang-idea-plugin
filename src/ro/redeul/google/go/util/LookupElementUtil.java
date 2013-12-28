package ro.redeul.google.go.util;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.util.PlatformIcons;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.lang.completion.insertHandler.FunctionInsertHandler;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.*;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class LookupElementUtil extends GoElementVisitor {

    private LookupElementBuilder lookupElement;

    private LookupElementUtil(LookupElementBuilder lookupElement) {
        this.lookupElement = lookupElement;
    }

    public static LookupElementBuilder createLookupElement(GoPsiElement element) {
        return createLookupElement(element, element.getPresentationText(),
                                   element);
    }

    public static LookupElementBuilder createLookupElement(GoPsiElement element, GoPsiElement child) {
        return createLookupElement(element, child.getPresentationText(), child);
    }

    public static LookupElementBuilder createLookupElement(GoPsiElement element, String text, GoPsiElement child) {

        LookupElementBuilder lookup =
            LookupElementBuilder.create(child, text)
                                .withTailText(element.getPresentationTailText())
                                .withTypeText( element.getPresentationTypeText());

        LookupElementUtil visitor = new LookupElementUtil(lookup);
        element.accept(visitor);

        return visitor.getLookupElement();
    }

    @Override
    public void visitLiteralIdentifier(GoLiteralIdentifier identifier) {
        ((GoPsiElement) identifier.getParent()).accept(this);
    }

    @Override
    public void visitTypeSpec(GoTypeSpec type) {
        if (type.getType() != null)
            type.getType().accept(this);
    }

    @Override
    public void visitTypeNameDeclaration(GoTypeNameDeclaration declaration) {
        visitTypeSpec(declaration.getTypeSpec());
    }

    @Override
    public void visitInterfaceType(GoPsiTypeInterface type) {
        lookupElement = lookupElement.withIcon(PlatformIcons.INTERFACE_ICON);
    }

    @Override
    public void visitArrayType(GoPsiTypeArray type) {
        lookupElement = lookupElement.withIcon(PlatformIcons.CLASS_ICON);
    }

    @Override
    public void visitSliceType(GoPsiTypeSlice type) {
        lookupElement = lookupElement.withIcon(PlatformIcons.CLASS_ICON);
    }

    @Override
    public void visitChannelType(GoPsiTypeChannel type) {
        lookupElement = lookupElement.withIcon(PlatformIcons.CLASS_ICON);
    }

    @Override
    public void visitStructType(GoPsiTypeStruct type) {
        lookupElement = lookupElement.withIcon(PlatformIcons.CLASS_ICON);
    }

    @Override
    public void visitMapType(GoPsiTypeMap type) {
        lookupElement = lookupElement.withIcon(PlatformIcons.CLASS_ICON);
    }

    @Override
    public void visitPointerType(GoPsiTypePointer type) {
        lookupElement = lookupElement.withIcon(PlatformIcons.CLASS_ICON);
    }

    @Override
    public void visitTypeName(GoPsiTypeName typeName) {
        lookupElement = lookupElement.withIcon(PlatformIcons.CLASS_ICON);
    }

    @Override
    public void visitMethodDeclaration(GoMethodDeclaration declaration) {
        lookupElement = lookupElement
            .withInsertHandler(new FunctionInsertHandler())
            .withIcon(PlatformIcons.METHOD_ICON);
    }

    @Override
    public void visitFunctionDeclaration(GoFunctionDeclaration declaration) {
        lookupElement = lookupElement
            .withInsertHandler(new FunctionInsertHandler())
            .withIcon(PlatformIcons.FUNCTION_ICON);
    }

    @Override
    public void visitVarDeclaration(GoVarDeclaration declaration) {
        lookupElement = lookupElement.withIcon(PlatformIcons.VARIABLE_ICON);
    }

    @Override
    public void visitConstDeclaration(GoConstDeclaration declaration) {
        lookupElement = lookupElement.withIcon(GoIcons.CONST_ICON);
    }

    @Override
    public void visitTypeStructField(GoTypeStructField field) {
        lookupElement = lookupElement.withIcon(PlatformIcons.FIELD_ICON);
    }

    @Override
    public void visitTypeStructAnonymousField(GoTypeStructAnonymousField field) {
        lookupElement = lookupElement.withIcon(PlatformIcons.FIELD_ICON);
    }

    LookupElementBuilder getLookupElement() {
        return lookupElement;
    }
}
