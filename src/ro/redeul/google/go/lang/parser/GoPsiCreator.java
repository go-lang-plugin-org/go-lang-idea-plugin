package ro.redeul.google.go.lang.parser;

import com.intellij.lang.ASTFactory;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.psi.impl.declarations.*;
import ro.redeul.google.go.lang.psi.impl.expressions.GoBuiltinCallExprImpl;
import ro.redeul.google.go.lang.psi.impl.expressions.literals.GoIdentifierImpl;
import ro.redeul.google.go.lang.psi.impl.GoPackageReferenceImpl;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.impl.expressions.literals.GoLiteralExprImpl;
import ro.redeul.google.go.lang.psi.impl.expressions.GoSelectorExpressionImpl;
import ro.redeul.google.go.lang.psi.impl.statements.GoBlockStatementImpl;
import ro.redeul.google.go.lang.psi.impl.toplevel.*;
import ro.redeul.google.go.lang.psi.impl.types.*;
import ro.redeul.google.go.lang.psi.impl.types.struct.GoTypeStructAnonymousFieldImpl;
import ro.redeul.google.go.lang.psi.impl.types.struct.GoTypeStructFieldImpl;
import ro.redeul.google.go.lang.psi.types.GoTypeChannel;

public class GoPsiCreator implements GoElementTypes {
    
    public static PsiElement createElement(ASTNode node) {

        IElementType elementType = node.getElementType();
        
        if ( elementType.equals(IDENTIFIER) )
            return new GoIdentifierImpl(node);

        if ( elementType.equals(PACKAGE_DECLARATION) )
            return new GoPackageDeclarationImpl(node);

        if ( elementType.equals(IMPORT_DECLARATIONS) )
            return new GoImportDeclarationsImpl(node);

        if ( elementType.equals(IMPORT_DECLARATION) )
            return new GoImportDeclarationImpl(node);

        if ( elementType.equals(PACKAGE_REFERENCE) )
            return new GoPackageReferenceImpl(node);

        if ( elementType.equals(TYPE_DECLARATIONS) )
            return new GoTypeDeclarationImpl(node);

        if ( elementType.equals(TYPE_DECLARATION) )
            return new GoTypeSpecImpl(node);

        if ( elementType.equals(VAR_DECLARATIONS) )
            return new GoVarDeclarationsImpl(node);

        if ( elementType.equals(VAR_DECLARATION) )
            return new GoVarDeclarationImpl(node);

        if (elementType.equals(CONST_DECLARATIONS) )
            return new GoConstDeclarationsImpl(node);

        if (elementType.equals(CONST_DECLARATION) )
            return new GoConstDeclarationImpl(node);

        if ( elementType.equals(TYPE_NAME_DECLARATION) )
            return new GoTypeNameDeclarationImpl(node);

        if ( elementType.equals(FUNCTION_DECLARATION) )
            return new GoFunctionDeclarationImpl(node);

        if ( elementType.equals(METHOD_DECLARATION) )
            return new GoMethodDeclarationImpl(node);

        if ( elementType.equals(TYPE_NAME) )
            return new GoTypeNameImpl(node);

        if ( elementType.equals(TYPE_ARRAY) )
            return new GoTypeArrayImpl(node);

        if ( elementType.equals(TYPE_SLICE) )
            return new GoTypeSliceImpl(node);

        if ( elementType.equals(TYPE_MAP) )
            return new GoTypeMapImpl(node);

        if ( elementType.equals(TYPE_POINTER) )
            return new GoTypePointerImpl(node);

        if ( elementType.equals(TYPE_STRUCT) )
            return new GoTypeStructImpl(node);

        if ( elementType.equals(TYPE_STRUCT_FIELD) )
            return new GoTypeStructFieldImpl(node);

        if ( elementType.equals(TYPE_STRUCT_FIELD_ANONYMOUS) )
            return new GoTypeStructAnonymousFieldImpl(node);

        if ( elementType.equals(TYPE_INTERFACE) )
            return new GoTypeInterfaceImpl(node);

        if ( elementType.equals(FUNCTION_PARAMETER_LIST) )
            return new GoFunctionParameterListImpl(node);

        if ( elementType.equals(FUNCTION_PARAMETER) )
            return new GoFunctionParameterImpl(node);

        if ( elementType.equals(TYPE_CHAN_BIDIRECTIONAL) )
            return new GoTypeChannelImpl(node, GoTypeChannel.ChannelType.Bidirectional);

        if ( elementType.equals(TYPE_CHAN_SENDING) )
            return new GoTypeChannelImpl(node, GoTypeChannel.ChannelType.Sending);

        if ( elementType.equals(TYPE_CHAN_RECEIVING) )
            return new GoTypeChannelImpl(node, GoTypeChannel.ChannelType.Receiving);

        if ( elementType.equals(BLOCK_STATEMENT))
            return new GoBlockStatementImpl(node);

        if ( elementType.equals(SELECTOR_EXPRESSION) )
            return new GoSelectorExpressionImpl(node);

        if ( elementType.equals(LITERAL_EXPRESSION) )
            return new GoLiteralExprImpl(node);

        if ( elementType.equals(SHORT_VAR_STATEMENT) )
            return new GoShortVarDeclarationImpl(node);

        if (elementType.equals(BUILTIN_CALL_EXPRESSION) )
            return new GoBuiltinCallExprImpl(node);

        if ( elementType.equals(wsNLS) )
            return (PsiElement) ASTFactory.whitespace(node.getText());
        
        return new GoPsiElementBase(node);
    }
}
