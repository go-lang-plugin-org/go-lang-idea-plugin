package ro.redeul.google.go.lang.parser;

import com.intellij.lang.ASTFactory;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.psi.impl.GoIdentifierImpl;
import ro.redeul.google.go.lang.psi.impl.GoPackageReferenceImpl;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.impl.expressions.GoLiteralExpressionImpl;
import ro.redeul.google.go.lang.psi.impl.expressions.GoSelectorExpressionImpl;
import ro.redeul.google.go.lang.psi.impl.statements.GoBlockStatementImpl;
import ro.redeul.google.go.lang.psi.impl.toplevel.*;
import ro.redeul.google.go.lang.psi.impl.types.*;
import ro.redeul.google.go.lang.psi.types.GoChannelType;

public class GoPsiCreator implements GoElementTypes {
    
    public static PsiElement createElement(ASTNode node) {

        IElementType elementType = node.getElementType();
        
        if ( elementType.equals(IDENTIFIER) )
            return new GoIdentifierImpl(node);

        if ( elementType.equals(PACKAGE_DECLARATION) )
            return new GoPackageDeclarationImpl(node);

        if ( elementType.equals(IMPORT_DECLARATION) )
            return new GoImportDeclarationImpl(node);

        if ( elementType.equals(IMPORT_SPEC) )
            return new GoImportSpecImpl(node);

        if ( elementType.equals(PACKAGE_REFERENCE) )
            return new GoPackageReferenceImpl(node);

        if ( elementType.equals(TYPE_DECLARATIONS) )
            return new GoTypeDeclarationImpl(node);

        if ( elementType.equals(TYPE_SPEC) )
            return new GoTypeSpecImpl(node);

        if ( elementType.equals(TYPE_NAME_DECLARATION) )
            return new GoTypeNameDeclarationImpl(node);

        if ( elementType.equals(FUNCTION_DECLARATION) )
            return new GoFunctionDeclarationImpl(node);

        if ( elementType.equals(METHOD_DECLARATION) )
            return new GoMethodDeclarationImpl(node);

        if ( elementType.equals(TYPE_NAME) )
            return new GoTypeNameImpl(node);

        if ( elementType.equals(TYPE_ARRAY) )
            return new GoArrayTypeImpl(node);

        if ( elementType.equals(TYPE_SLICE) )
            return new GoSliceTypeImpl(node);

        if ( elementType.equals(TYPE_MAP) )
            return new GoMapTypeImpl(node);

        if ( elementType.equals(TYPE_POINTER) )
            return new GoPointerTypeImpl(node);

        if ( elementType.equals(FUNCTION_PARAMETER_LIST) )
            return new GoFunctionParameterListImpl(node);

        if ( elementType.equals(FUNCTION_PARAMETER) )
            return new GoFunctionParameterImpl(node);

        if ( elementType.equals(TYPE_CHAN_BIDIRECTIONAL) )
            return new GoChannelTypeImpl(node, GoChannelType.ChannelType.Bidirectional);

        if ( elementType.equals(TYPE_CHAN_SENDING) )
            return new GoChannelTypeImpl(node, GoChannelType.ChannelType.Sending);

        if ( elementType.equals(TYPE_CHAN_RECEIVING) )
            return new GoChannelTypeImpl(node, GoChannelType.ChannelType.Receiving);

        if ( elementType.equals(BLOCK_STATEMENT))
            return new GoBlockStatementImpl(node);

        if ( elementType.equals(SELECTOR_EXPRESSION) )
            return new GoSelectorExpressionImpl(node);

        if ( elementType.equals(LITERAL_EXPRESSION) )
            return new GoLiteralExpressionImpl(node);

        if ( elementType.equals(wsNLS) )
            return (PsiElement) ASTFactory.whitespace(node.getText());
        
        return new GoPsiElementBase(node);
    }
}
