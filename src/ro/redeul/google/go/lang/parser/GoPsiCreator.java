package ro.redeul.google.go.lang.parser;

import com.intellij.lang.ASTFactory;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.psi.impl.GoPackageReferenceImpl;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.impl.declarations.GoConstDeclarationImpl;
import ro.redeul.google.go.lang.psi.impl.declarations.GoConstDeclarationsImpl;
import ro.redeul.google.go.lang.psi.impl.declarations.GoVarDeclarationImpl;
import ro.redeul.google.go.lang.psi.impl.declarations.GoVarDeclarationsImpl;
import ro.redeul.google.go.lang.psi.impl.expressions.GoBuiltinCallExprImpl;
import ro.redeul.google.go.lang.psi.impl.expressions.GoCallOrConversionExpressionImpl;
import ro.redeul.google.go.lang.psi.impl.expressions.GoIndexExpressionImpl;
import ro.redeul.google.go.lang.psi.impl.expressions.GoLiteralExpressionImpl;
import ro.redeul.google.go.lang.psi.impl.expressions.GoSelectorExpressionImpl;
import ro.redeul.google.go.lang.psi.impl.expressions.binary.GoAdditiveExpressionImpl;
import ro.redeul.google.go.lang.psi.impl.expressions.binary.GoMultiplicativeExpressionImpl;
import ro.redeul.google.go.lang.psi.impl.expressions.literals.GoLiteralBoolImpl;
import ro.redeul.google.go.lang.psi.impl.expressions.literals.GoLiteralFloatImpl;
import ro.redeul.google.go.lang.psi.impl.expressions.literals.GoLiteralFunctionImpl;
import ro.redeul.google.go.lang.psi.impl.expressions.literals.GoLiteralIdentifierImpl;
import ro.redeul.google.go.lang.psi.impl.expressions.literals.GoLiteralImaginaryImpl;
import ro.redeul.google.go.lang.psi.impl.expressions.literals.GoLiteralIntegerImpl;
import ro.redeul.google.go.lang.psi.impl.expressions.literals.GoLiteralStringImpl;
import ro.redeul.google.go.lang.psi.impl.expressions.literals.composite.GoLiteralCompositeElementImpl;
import ro.redeul.google.go.lang.psi.impl.expressions.literals.composite.GoLiteralCompositeImpl;
import ro.redeul.google.go.lang.psi.impl.expressions.literals.composite.GoLiteralCompositeValueImpl;
import ro.redeul.google.go.lang.psi.impl.statements.GoBlockStatementImpl;
import ro.redeul.google.go.lang.psi.impl.statements.GoDeferStatementImpl;
import ro.redeul.google.go.lang.psi.impl.statements.GoExpressionStatementImpl;
import ro.redeul.google.go.lang.psi.impl.statements.GoForWithClausesStatementImpl;
import ro.redeul.google.go.lang.psi.impl.statements.GoForWithConditionStatementImpl;
import ro.redeul.google.go.lang.psi.impl.statements.GoForWithRangeStatementImpl;
import ro.redeul.google.go.lang.psi.impl.statements.GoGoStatementImpl;
import ro.redeul.google.go.lang.psi.impl.statements.GoIfStatementImpl;
import ro.redeul.google.go.lang.psi.impl.statements.GoReturnStatementImpl;
import ro.redeul.google.go.lang.psi.impl.statements.GoShortVarDeclarationImpl;
import ro.redeul.google.go.lang.psi.impl.toplevel.GoFunctionDeclarationImpl;
import ro.redeul.google.go.lang.psi.impl.toplevel.GoFunctionParameterImpl;
import ro.redeul.google.go.lang.psi.impl.toplevel.GoImportDeclarationImpl;
import ro.redeul.google.go.lang.psi.impl.toplevel.GoImportDeclarationsImpl;
import ro.redeul.google.go.lang.psi.impl.toplevel.GoMethodDeclarationImpl;
import ro.redeul.google.go.lang.psi.impl.toplevel.GoMethodReceiverImpl;
import ro.redeul.google.go.lang.psi.impl.toplevel.GoPackageDeclarationImpl;
import ro.redeul.google.go.lang.psi.impl.toplevel.GoTypeDeclarationImpl;
import ro.redeul.google.go.lang.psi.impl.toplevel.GoTypeNameDeclarationImpl;
import ro.redeul.google.go.lang.psi.impl.toplevel.GoTypeSpecImpl;
import ro.redeul.google.go.lang.psi.impl.types.GoTypeArrayImpl;
import ro.redeul.google.go.lang.psi.impl.types.GoTypeChannelImpl;
import ro.redeul.google.go.lang.psi.impl.types.GoTypeInterfaceImpl;
import ro.redeul.google.go.lang.psi.impl.types.GoTypeMapImpl;
import ro.redeul.google.go.lang.psi.impl.types.GoTypeNameImpl;
import ro.redeul.google.go.lang.psi.impl.types.GoTypeParenthesizedImpl;
import ro.redeul.google.go.lang.psi.impl.types.GoTypePointerImpl;
import ro.redeul.google.go.lang.psi.impl.types.GoTypeSliceImpl;
import ro.redeul.google.go.lang.psi.impl.types.GoTypeStructImpl;
import ro.redeul.google.go.lang.psi.impl.types.struct.GoTypeStructAnonymousFieldImpl;
import ro.redeul.google.go.lang.psi.impl.types.struct.GoTypeStructFieldImpl;

import static ro.redeul.google.go.lang.psi.types.GoTypeChannel.ChannelType;

public class GoPsiCreator implements GoElementTypes {

    public static PsiElement createElement(ASTNode node) {

        IElementType elementType = node.getElementType();

//        if (elementType.equals(IDENTIFIER))
//            return new GoLiteralIdentifierImpl(node);

        if (elementType.equals(PACKAGE_DECLARATION))
            return new GoPackageDeclarationImpl(node);

        if (elementType.equals(IMPORT_DECLARATIONS))
            return new GoImportDeclarationsImpl(node);

        if (elementType.equals(IMPORT_DECLARATION))
            return new GoImportDeclarationImpl(node);

        if (elementType.equals(PACKAGE_REFERENCE))
            return new GoPackageReferenceImpl(node);

        if (elementType.equals(TYPE_DECLARATIONS))
            return new GoTypeDeclarationImpl(node);

        if (elementType.equals(TYPE_DECLARATION))
            return new GoTypeSpecImpl(node);

        if (elementType.equals(VAR_DECLARATIONS))
            return new GoVarDeclarationsImpl(node);

        if (elementType.equals(VAR_DECLARATION))
            return new GoVarDeclarationImpl(node);

        if (elementType.equals(CONST_DECLARATIONS))
            return new GoConstDeclarationsImpl(node);

        if (elementType.equals(CONST_DECLARATION))
            return new GoConstDeclarationImpl(node);

        if (elementType.equals(TYPE_NAME_DECLARATION))
            return new GoTypeNameDeclarationImpl(node);

        if (elementType.equals(FUNCTION_DECLARATION))
            return new GoFunctionDeclarationImpl(node);

        if (elementType.equals(METHOD_DECLARATION))
            return new GoMethodDeclarationImpl(node);

        if (elementType.equals(METHOD_RECEIVER))
            return new GoMethodReceiverImpl(node);

        if (elementType.equals(TYPE_PARENTHESIZED))
            return new GoTypeParenthesizedImpl(node);

        if (elementType.equals(TYPE_NAME))
            return new GoTypeNameImpl(node);

        if (elementType.equals(TYPE_ARRAY))
            return new GoTypeArrayImpl(node);

        if (elementType.equals(TYPE_SLICE))
            return new GoTypeSliceImpl(node);

        if (elementType.equals(TYPE_MAP))
            return new GoTypeMapImpl(node);

        if (elementType.equals(TYPE_POINTER))
            return new GoTypePointerImpl(node);

        if (elementType.equals(TYPE_STRUCT))
            return new GoTypeStructImpl(node);

        if (elementType.equals(TYPE_STRUCT_FIELD))
            return new GoTypeStructFieldImpl(node);

        if (elementType.equals(TYPE_STRUCT_FIELD_ANONYMOUS))
            return new GoTypeStructAnonymousFieldImpl(node);

        if (elementType.equals(TYPE_INTERFACE))
            return new GoTypeInterfaceImpl(node);

        if (elementType.equals(FUNCTION_PARAMETER))
            return new GoFunctionParameterImpl(node);

        if (elementType.equals(FUNCTION_PARAMETER_VARIADIC))
            return new GoFunctionParameterImpl(node);

        if (elementType.equals(TYPE_CHAN_BIDIRECTIONAL))
            return new GoTypeChannelImpl(node, ChannelType.Bidirectional);

        if (elementType.equals(TYPE_CHAN_SENDING))
            return new GoTypeChannelImpl(node, ChannelType.Sending);

        if (elementType.equals(TYPE_CHAN_RECEIVING))
            return new GoTypeChannelImpl(node, ChannelType.Receiving);

        if (elementType.equals(BLOCK_STATEMENT))
            return new GoBlockStatementImpl(node);

        if (elementType.equals(INDEX_EXPRESSION))
            return new GoIndexExpressionImpl(node);

        if (elementType.equals(SELECTOR_EXPRESSION))
            return new GoSelectorExpressionImpl(node);

        if (elementType.equals(LITERAL_BOOL))
            return new GoLiteralBoolImpl(node);

        if (elementType.equals(LITERAL_FLOAT))
            return new GoLiteralFloatImpl(node);

        if (elementType.equals(LITERAL_INTEGER))
            return new GoLiteralIntegerImpl(node);

        if (elementType.equals(LITERAL_STRING))
            return new GoLiteralStringImpl(node);

        if (elementType.equals(LITERAL_CHAR))
            return new GoLiteralStringImpl(node);

        if (elementType.equals(LITERAL_IMAGINARY))
            return new GoLiteralImaginaryImpl(node);

        if (elementType.equals(LITERAL_IOTA))
            return new GoLiteralIdentifierImpl(node, true);

        if (elementType.equals(LITERAL_IDENTIFIER))
            return new GoLiteralIdentifierImpl(node);

        if (elementType.equals(LITERAL_EXPRESSION))
            return new GoLiteralExpressionImpl(node);

        if (elementType.equals(LITERAL_COMPOSITE))
            return new GoLiteralCompositeImpl(node);

        if (elementType.equals(LITERAL_COMPOSITE_VALUE))
            return new GoLiteralCompositeValueImpl(node);

        if (elementType.equals(LITERAL_COMPOSITE_ELEMENT))
            return new GoLiteralCompositeElementImpl(node);
//
        if (elementType.equals(LITERAL_FUNCTION))
            return new GoLiteralFunctionImpl(node);

        if (elementType.equals(ADD_EXPRESSION))
            return new GoAdditiveExpressionImpl(node);

        if (elementType.equals(MUL_EXPRESSION))
            return new GoMultiplicativeExpressionImpl(node);

        if (elementType.equals(RETURN_STATEMENT))
            return new GoReturnStatementImpl(node);

        if (elementType.equals(SHORT_VAR_STATEMENT))
            return new GoShortVarDeclarationImpl(node);

        if ( elementType.equals(EXPRESSION_STATEMENT) )
            return new GoExpressionStatementImpl(node);

        if ( elementType.equals(CALL_OR_CONVERSION_EXPRESSION) )
            return new GoCallOrConversionExpressionImpl(node);

        if (elementType.equals(FOR_WITH_CONDITION_STATEMENT))
            return new GoForWithConditionStatementImpl(node);

        if (elementType.equals(FOR_WITH_CLAUSES_STATEMENT))
            return new GoForWithClausesStatementImpl(node);

        if (elementType.equals(FOR_WITH_RANGE_STATEMENT))
            return new GoForWithRangeStatementImpl(node);

        if (elementType.equals(BUILTIN_CALL_EXPRESSION))
            return new GoBuiltinCallExprImpl(node);

        if (elementType.equals(IF_STATEMENT))
            return new GoIfStatementImpl(node);

        if (elementType.equals(GO_STATEMENT))
            return new GoGoStatementImpl(node);

        if (elementType.equals(DEFER_STATEMENT))
            return new GoDeferStatementImpl(node);

        if (elementType.equals(wsNLS))
            return (PsiElement) ASTFactory.whitespace(node.getText());

        return new GoPsiElementBase(node);
    }
}
