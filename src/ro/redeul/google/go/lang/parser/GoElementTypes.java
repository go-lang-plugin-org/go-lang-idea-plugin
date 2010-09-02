package ro.redeul.google.go.lang.parser;

import ro.redeul.google.go.lang.lexer.GoElementType;
import ro.redeul.google.go.lang.lexer.GoElementTypeImpl;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 7:40:35 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GoElementTypes extends GoTokenTypes {
    GoElementType NONE = new GoElementTypeImpl("no token"); //not a node

    GoElementType IDENTIFIER = new GoElementTypeImpl("Go identifier");

    GoElementType BUILTIN_FUNCTION = new GoElementTypeImpl("Built in call");

    // Indicates the wrongway of parsing
    GoElementType WRONGWAY = new GoElementTypeImpl("Wrong way!");
    GoElementType LITERAL = new GoElementTypeImpl("Literal");

    //Packaging
    GoElementType PACKAGE_NAME = new GoElementTypeImpl("Package name");
    GoElementType PACKAGE_DECLARATION = new GoElementTypeImpl("PackageDeclaration");

    GoElementType IMPORT_DECLARATION = new GoElementTypeImpl("ImportDeclarations");
    GoElementType IMPORT_SPEC = new GoElementTypeImpl("ImportSpec");

    GoElementType CONST_DECLARATIONS = new GoElementTypeImpl("ConstDeclarations");
    GoElementType CONST_SPEC = new GoElementTypeImpl("ConstSpec");

    GoElementType VAR_DECLARATIONS = new GoElementTypeImpl("VarDeclarations");
    GoElementType VAR_SPEC = new GoElementTypeImpl("VarSpec");

    GoElementType TYPE_DECLARATIONS = new GoElementTypeImpl("TypeDeclarations");
    GoElementType TYPE_SPEC = new GoElementTypeImpl("TypeSpec");

    GoElementType IDENTIFIERS = new GoElementTypeImpl("Identifiers");
    GoElementType EXPRESSION_LIST = new GoElementTypeImpl("ExpressionList");
    GoElementType EXPRESSION_PARENTHESIZED = new GoElementTypeImpl("ParenthesisedExpression");

    GoElementType ADD_EXPRESSION = new GoElementTypeImpl("AdditiveExpression");
    GoElementType MUL_EXPRESSION = new GoElementTypeImpl("MultiplicativeExpression");
    GoElementType REL_EXPRESSION = new GoElementTypeImpl("RelationalExpression");
    GoElementType COM_EXPRESSION = new GoElementTypeImpl("CommunicationExpression");
    GoElementType LOG_AND_EXPRESSION = new GoElementTypeImpl("LogicalAndExpression");
    GoElementType LOG_OR_EXPRESSION = new GoElementTypeImpl("LogicalOrExpression");

    GoElementType CALL_OR_CONVERSION_EXPRESSION = new GoElementTypeImpl("CallOrConversionExpression");
    GoElementType INDEX_EXPRESSION = new GoElementTypeImpl("IndexExpression");
    GoElementType SLICE_EXPRESSION = new GoElementTypeImpl("SliceExpression");
    GoElementType TYPE_ASSERTION_EXPRESSION = new GoElementTypeImpl("TypeAssertionExpression");


    GoElementType UNARY_OPERATOR = new GoElementTypeImpl("unary operator");
    GoElementType UNARY_EXPRESSION = new GoElementTypeImpl("UnaryExpression");
    GoElementType PRIMARY_EXPRESSION = new GoElementTypeImpl("primary expression");
    GoElementType SELECTOR_EXPRESSION = new GoElementTypeImpl("SelectorExpression");
    GoElementType BUILTIN_CALL_EXPRESSION = new GoElementTypeImpl("BuiltInCallExpression");
    GoElementType FUNCTION_LITERAL_EXPRESSION = new GoElementTypeImpl("FunctionLiteral");
    GoElementType COMPOSITE_LITERAL_EXPRESSION = new GoElementTypeImpl("CompositeLiteral");
    GoElementType COMPOSITE_LITERAL_ELEMENT_LIST = new GoElementTypeImpl("CompositeLiteralElementList");
    GoElementType COMPOSITE_LITERAL_ELEMENT= new GoElementTypeImpl("CompositeLiteralElement");
    GoElementType COMPOSITE_LITERAL_ELEMENT_KEY= new GoElementTypeImpl("CompositeLiteralElementKey");
    GoElementType COMPOSITE_LITERAL_ELEMENT_VALUE= new GoElementTypeImpl("CompositeLiteralElementValue");


    GoElementType TYPE_PARANTHESIZED = new GoElementTypeImpl("paranthesized type");

    GoElementType PACKAGE_REFERENCE = new GoElementTypeImpl("PackageReference");

    GoElementType TYPE_LIST = new GoElementTypeImpl("TypeList");
    GoElementType TYPE_NAME = new GoElementTypeImpl("TypeName");
    GoElementType TYPE_NAME_DECLARATION = new GoElementTypeImpl("TypeNameDeclaration");

    GoElementType TYPE_ARRAY = new GoElementTypeImpl("TypeArray");
    GoElementType TYPE_MAP = new GoElementTypeImpl("TypeMap");

    GoElementType TYPE_CHAN_BIDIRECTIONAL = new GoElementTypeImpl("TypeChanBidi");
    GoElementType TYPE_CHAN_SENDING = new GoElementTypeImpl("TypeChanSend");
    GoElementType TYPE_CHAN_RECEIVING = new GoElementTypeImpl("TypeChanRecv");

    GoElementType TYPE_SLICE = new GoElementTypeImpl("TypeSlice");
    GoElementType TYPE_POINTER = new GoElementTypeImpl("TypePointer");
    GoElementType TYPE_INTERFACE = new GoElementTypeImpl("TypeInterface");
    GoElementType TYPE_FUNCTION = new GoElementTypeImpl("TypeFunction");
    
    GoElementType INTERFACE_REFERENCE  = new GoElementTypeImpl("interface type");
    GoElementType TYPE_STRUCT = new GoElementTypeImpl("TypeStruct");
    GoElementType TYPE_STRUCT_FIELD = new GoElementTypeImpl("TypeStruct field");
    GoElementType TYPE_STRUCT_FIELD_ADDRESS = new GoElementTypeImpl("struct field address type");
    GoElementType TYPE_STRUCT_FIELD_ANONYMOUS_TYPE = new GoElementTypeImpl("struct field anonymous stype");

    GoElementType FUNCTION_DECLARATION = new GoElementTypeImpl("FunctionDeclaration(f)");
    GoElementType METHOD_DECLARATION = new GoElementTypeImpl("MethodDeclaration");
    GoElementType METHOD_RECEIVER = new GoElementTypeImpl("MethodReceiver");
    GoElementType FUNCTION_PARAMETER_LIST = new GoElementTypeImpl("FunctionParameterList");
    GoElementType FUNCTION_PARAMETER = new GoElementTypeImpl("FunctionParameter");
    GoElementType FUNCTION_PARAMETER_VARIADIC = new GoElementTypeImpl("FunctionParameterVariadic");
    GoElementType FUNCTION_RESULT= new GoElementTypeImpl("FunctionResult");

    GoElementType REFERENCE_BASE_TYPE_NAME = new GoElementTypeImpl("reference base type name");
    GoElementType BASE_TYPE_NAME = new GoElementTypeImpl("base type name");
    
    GoElementType STATEMENT = new GoElementTypeImpl("statement");

    GoElementType BLOCK_STATEMENT = new GoElementTypeImpl("BlockStmt");

    //Branch statements
    GoElementType IF_STATEMENT = new GoElementTypeImpl("IfStmt");
    GoElementType FOR_STATEMENT = new GoElementTypeImpl("ForStmt");

    GoElementType FOR_STATEMENT_CONDITION_CLAUSE = new GoElementTypeImpl("ForConditionClause");
    GoElementType FOR_STATEMENT_FOR_CLAUSE = new GoElementTypeImpl("ForForClause");
    GoElementType FOR_STATEMENT_RANGE_CLAUSE = new GoElementTypeImpl("ForRangeClause");
   
    GoElementType SWITCH_TYPE_STATEMENT = new GoElementTypeImpl("SwitchTypeStmt");
    GoElementType SWITCH_TYPE_GUARD = new GoElementTypeImpl("SwitchTypeGuard");
    GoElementType SWITCH_TYPE_CASE = new GoElementTypeImpl("SwitchTypeCase");
    GoElementType SWITCH_EXPR_STATEMENT = new GoElementTypeImpl("SwitchExprStmt");
    GoElementType SWITCH_EXPR_CASE = new GoElementTypeImpl("SwitchExprCase");

    GoElementType SELECT_STATEMENT = new GoElementTypeImpl("SelectStmt");
    GoElementType SELECT_CASE = new GoElementTypeImpl("SelectCase");
    GoElementType SELECT_CASE_RECV_EXPRESSION = new GoElementTypeImpl("SelectCaseRecvExpr");
    GoElementType SELECT_CASE_SEND_EXPRESSION = new GoElementTypeImpl("SelectCaseSendExpr");

    GoElementType BREAK_STATEMENT = new GoElementTypeImpl("BreakStmt");
    GoElementType CONTINUE_STATEMENT = new GoElementTypeImpl("ContinueStmt");
    GoElementType GOTO_STATEMENT = new GoElementTypeImpl("Goto statement");
    GoElementType FALLTHROUGH_STATEMENT = new GoElementTypeImpl("FallthroughStmt");

    GoElementType ASSIGN_STATEMENT = new GoElementTypeImpl("AssignStmt");
    GoElementType SHORT_VAR_STATEMENT = new GoElementTypeImpl("ShortVarStmt");
    GoElementType INC_DEC_STATEMENT = new GoElementTypeImpl("IncDecStmt");
    GoElementType EXPRESSION_STATEMENT = new GoElementTypeImpl("ExpressionStmt");

    GoElementType RETURN_STATEMENT = new GoElementTypeImpl("ReturnStmt");
    GoElementType GO_STATEMENT = new GoElementTypeImpl("GoStmt");
    GoElementType DEFER_STATEMENT = new GoElementTypeImpl("DeferStmt");
    GoElementType EMPTY_STATEMENT = new GoElementTypeImpl("EmptyStmt");

    
    // Expressions statements
    GoElementType LABELED_STATEMENT = new GoElementTypeImpl("LabeledStmt");

}
