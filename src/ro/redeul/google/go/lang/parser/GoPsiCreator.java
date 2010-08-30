package ro.redeul.google.go.lang.parser;

import com.intellij.lang.ASTFactory;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementImpl;
import ro.redeul.google.go.lang.psi.impl.expressions.GoIdentifierImpl;
import ro.redeul.google.go.lang.psi.impl.toplevel.*;
import ro.redeul.google.go.lang.psi.impl.types.GoArrayTypeImpl;
import ro.redeul.google.go.lang.psi.impl.types.GoTypeNameImpl;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 7:53:44 PM
 * To change this template use File | Settings | File Templates.
 */
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

        if ( elementType.equals(TYPE_DECLARATIONS) )
            return new GoTypeDeclarationImpl(node);

        if ( elementType.equals(TYPE_SPEC) )
            return new GoTypeSpecImpl(node);

        if ( elementType.equals(FUNCTION_DECLARATION) )
            return new GoFunctionDeclarationImpl(node);

        if ( elementType.equals(METHOD_DECLARATION) )
            return new GoMethodDeclarationImpl(node);

        if ( elementType.equals(TYPE_NAME) )
            return new GoTypeNameImpl(node);

        if ( elementType.equals(TYPE_ARRAY) )
            return new GoArrayTypeImpl(node);

        if ( elementType.equals(wsNLS) )
            return (PsiElement) ASTFactory.whitespace(node.getText());
        
        return new GoPsiElementImpl(node);
    }
}
