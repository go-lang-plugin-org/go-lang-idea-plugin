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
import ro.redeul.google.go.lang.psi.impl.expressions.GoExpressionListImpl;
import ro.redeul.google.go.lang.psi.impl.expressions.GoUnaryExpressionImpl;
import ro.redeul.google.go.lang.psi.impl.expressions.binary.*;
import ro.redeul.google.go.lang.psi.impl.expressions.literals.*;
import ro.redeul.google.go.lang.psi.impl.expressions.literals.composite.GoLiteralCompositeElementImpl;
import ro.redeul.google.go.lang.psi.impl.expressions.literals.composite.GoLiteralCompositeImpl;
import ro.redeul.google.go.lang.psi.impl.expressions.literals.composite.GoLiteralCompositeValueImpl;
import ro.redeul.google.go.lang.psi.impl.expressions.primary.*;
import ro.redeul.google.go.lang.psi.impl.statements.*;
import ro.redeul.google.go.lang.psi.impl.statements.select.GoSelectCommClauseDefaultImpl;
import ro.redeul.google.go.lang.psi.impl.statements.select.GoSelectCommClauseRecvImpl;
import ro.redeul.google.go.lang.psi.impl.statements.select.GoSelectCommClauseSendImpl;
import ro.redeul.google.go.lang.psi.impl.statements.select.GoSelectStatementImpl;
import ro.redeul.google.go.lang.psi.impl.statements.switches.*;
import ro.redeul.google.go.lang.psi.impl.toplevel.*;
import ro.redeul.google.go.lang.psi.impl.types.*;
import ro.redeul.google.go.lang.psi.impl.types.struct.GoTypeStructAnonymousFieldImpl;
import ro.redeul.google.go.lang.psi.impl.types.struct.GoTypeStructFieldImpl;

import static ro.redeul.google.go.lang.psi.typing.GoTypeChannel.ChannelType;

class GoPsiCreator implements GoElementTypes {

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
            return new GoPsiTypeParenthesizedImpl(node);

        if (elementType.equals(TYPE_NAME))
            return new GoPsiTypeNameImpl(node);

        if (elementType.equals(TYPE_ARRAY))
            return new GoPsiTypeArrayImpl(node);

        if (elementType.equals(TYPE_SLICE))
            return new GoPsiTypeSliceImpl(node);

        if (elementType.equals(TYPE_MAP))
            return new GoPsiTypeMapImpl(node);

        if (elementType.equals(TYPE_POINTER))
            return new GoPsiTypePointerImpl(node);

        if (elementType.equals(TYPE_STRUCT))
            return new GoPsiTypeStructImpl(node);

        if (elementType.equals(TYPE_STRUCT_FIELD))
            return new GoTypeStructFieldImpl(node);

        if (elementType.equals(TYPE_STRUCT_FIELD_ANONYMOUS))
            return new GoTypeStructAnonymousFieldImpl(node);

        if (elementType.equals(TYPE_INTERFACE))
            return new GoPsiTypeInterfaceImpl(node);

        if (elementType.equals(TYPE_FUNCTION))
            return new GoPsiTypeFunctionImpl(node);

        if (elementType.equals(FUNCTION_PARAMETER_LIST))
            return new GoFunctionParameterListImpl(node);

        if (elementType.equals(FUNCTION_PARAMETER))
            return new GoFunctionParameterImpl(node);

        if (elementType.equals(FUNCTION_PARAMETER_VARIADIC))
            return new GoFunctionParameterImpl(node);

        if (elementType.equals(TYPE_CHAN_BIDIRECTIONAL))
            return new GoPsiTypeChannelImpl(node, ChannelType.Bidirectional);

        if (elementType.equals(TYPE_CHAN_SENDING))
            return new GoPsiTypeChannelImpl(node, ChannelType.Sending);

        if (elementType.equals(TYPE_CHAN_RECEIVING))
            return new GoPsiTypeChannelImpl(node, ChannelType.Receiving);

        if (elementType.equals(BLOCK_STATEMENT))
            return new GoBlockStatementImpl(node);

        if (elementType.equals(INDEX_EXPRESSION))
            return new GoIndexExpressionImpl(node);

        if (elementType.equals(SLICE_EXPRESSION))
            return new GoSliceExpressionImpl(node);

        if (elementType.equals(SELECTOR_EXPRESSION))
            return new GoSelectorExpressionImpl(node);

        if (elementType.equals(PARENTHESISED_EXPRESSION))
            return new GoParenthesisedExpressionImpl(node);

        if (elementType.equals(TYPE_ASSERTION_EXPRESSION))
            return new GoTypeAssertionExpressionImpl(node);

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

        if (elementType.equals(EXPRESSION_LIST))
            return new GoExpressionListImpl(node);

        if (elementType.equals(ADD_EXPRESSION))
            return new GoAdditiveExpressionImpl(node);

        if (elementType.equals(MUL_EXPRESSION))
            return new GoMultiplicativeExpressionImpl(node);

        if (elementType.equals(REL_EXPRESSION))
            return new GoRelationalExpressionImpl(node);

        if (elementType.equals(LOG_AND_EXPRESSION))
            return new GoLogicalAndExpressionImpl(node);

        if (elementType.equals(LOG_OR_EXPRESSION))
            return new GoLogicalOrExpressionImpl(node);

        if (elementType.equals(RETURN_STATEMENT))
            return new GoReturnStatementImpl(node);

        if (elementType.equals(SHORT_VAR_STATEMENT))
            return new GoShortVarDeclarationImpl(node);

        if (elementType.equals(EXPRESSION_STATEMENT))
            return new GoExpressionStatementImpl(node);

        if (elementType.equals(UNARY_EXPRESSION))
            return new GoUnaryExpressionImpl(node);

        if (elementType.equals(CALL_OR_CONVERSION_EXPRESSION))
            return new GoCallOrConvExpressionImpl(node);

        if (elementType.equals(FOR_WITH_CONDITION_STATEMENT))
            return new GoForWithConditionStatementImpl(node);

        if (elementType.equals(FOR_WITH_CLAUSES_STATEMENT))
            return new GoForWithClausesStatementImpl(node);

        if (elementType.equals(FOR_WITH_RANGE_STATEMENT))
            return new GoForWithRangeStatementImpl(node);

        if (elementType.equals(FOR_WITH_RANGE_AND_VARS_STATEMENT))
            return new GoForWithRangeAndVarsStatementImpl(node);

        if (elementType.equals(BUILTIN_CALL_EXPRESSION))
            return new GoBuiltinCallExpressionImpl(node);

        if (elementType.equals(ASSIGN_STATEMENT))
            return new GoAssignmentStatementImpl(node);

        if (elementType.equals(IF_STATEMENT))
            return new GoIfStatementImpl(node);

        if (elementType.equals(SWITCH_EXPR_STATEMENT))
            return new GoSwitchExpressionStatementImpl(node);

        if (elementType.equals(SWITCH_EXPR_CASE))
            return new GoSwitchExpressionClauseImpl(node);

        if (elementType.equals(SWITCH_TYPE_STATEMENT))
            return new GoSwitchTypeStatementImpl(node);

        if (elementType.equals(SWITCH_TYPE_GUARD))
            return new GoSwitchTypeGuardImpl(node);

        if (elementType.equals(SWITCH_TYPE_CASE))
            return new GoSwitchTypeClauseImpl(node);

        if (elementType.equals(LABELED_STATEMENT))
            return new GoLabeledStatementImpl(node);

        if (elementType.equals(GO_STATEMENT))
            return new GoGoStatementImpl(node);

        if (elementType.equals(DEFER_STATEMENT))
            return new GoDeferStatementImpl(node);

        if (elementType.equals(FALLTHROUGH_STATEMENT))
            return new GoFallthroughStatementImpl(node);

        if (elementType.equals(BREAK_STATEMENT))
            return new GoBreakStatementImpl(node);

        if (elementType.equals(CONTINUE_STATEMENT))
            return new GoContinueStatementImpl(node);

        if (elementType.equals(GOTO_STATEMENT))
            return new GoGotoStatementImpl(node);

        if (elementType.equals(SELECT_STATEMENT))
            return new GoSelectStatementImpl(node);

        if (elementType.equals(INC_DEC_STATEMENT))
            return new GoIncDecStatementImpl(node);

        if (elementType.equals(SELECT_COMM_CLAUSE_SEND))
            return new GoSelectCommClauseSendImpl(node);

        if (elementType.equals(SELECT_COMM_CLAUSE_RECV))
            return new GoSelectCommClauseRecvImpl(node);

        if (elementType.equals(SELECT_COMM_CLAUSE_DEFAULT))
            return new GoSelectCommClauseDefaultImpl(node);

        if (elementType.equals(SEND_STATEMENT))
            return new GoSendStatementImpl(node);

        if (elementType.equals(wsNLS))
            return (PsiElement) ASTFactory.whitespace(node.getText());

        return new GoPsiElementBase(node);
    }
}
