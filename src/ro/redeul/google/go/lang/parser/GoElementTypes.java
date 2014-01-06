package ro.redeul.google.go.lang.parser;

import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.lang.lexer.GoElementType;
import ro.redeul.google.go.lang.lexer.GoElementTypeImpl;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.stubs.GoTypeNameDeclarationStub;
import ro.redeul.google.go.lang.psi.stubs.elements.GoStubElementType;
import ro.redeul.google.go.lang.psi.stubs.elements.GoTypeNameDeclarationType;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;

public interface GoElementTypes extends GoTokenTypes {

    GoElementType IDENTIFIER = new GoElementTypeImpl("Identifier");
    GoElementType IDENTIFIERS = new GoElementTypeImpl("Identifiers");

    // others
    GoElementType PACKAGE_REFERENCE = new GoElementTypeImpl("PackageReference");
    GoElementType BUILTIN_FUNCTION = new GoElementTypeImpl("BuiltinCall");

    //Packaging
    GoElementType PACKAGE_NAME = new GoElementTypeImpl("PackageName");
    GoElementType PACKAGE_DECLARATION = new GoElementTypeImpl("PackageDeclaration");

    // declarations
    GoElementType IMPORT_DECLARATIONS = new GoElementTypeImpl("ImportDeclarations");
    GoElementType IMPORT_DECLARATION = new GoElementTypeImpl("ImportSpec");

    GoElementType CONST_DECLARATIONS = new GoElementTypeImpl("ConstDeclarations");
    GoElementType CONST_DECLARATION = new GoElementTypeImpl("ConstSpec");

    GoElementType VAR_DECLARATIONS = new GoElementTypeImpl("VarDeclarations");
    GoElementType VAR_DECLARATION = new GoElementTypeImpl("VarDeclaration");

    GoElementType TYPE_DECLARATIONS = new GoElementTypeImpl("TypeDeclarations");
    GoElementType TYPE_DECLARATION = new GoElementTypeImpl("TypeSpec");

    // literals
    GoElementType LITERAL_IDENTIFIER = new GoElementTypeImpl("LiteralIdentifier");
    GoElementType LITERAL_IOTA = new GoElementTypeImpl("LiteralIota");
    GoElementType LITERAL_BOOL = new GoElementTypeImpl("LiteralBool");
    GoElementType LITERAL_STRING = new GoElementTypeImpl("LiteralString");
    GoElementType LITERAL_CHAR = new GoElementTypeImpl("LiteralChar");
    GoElementType LITERAL_IMAGINARY = new GoElementTypeImpl("LiteralImaginary");
    GoElementType LITERAL_INTEGER = new GoElementTypeImpl("LiteralInteger");
    GoElementType LITERAL_FLOAT = new GoElementTypeImpl("LiteralFloat");
    GoElementType LITERAL_FUNCTION = new GoElementTypeImpl("LiteralFunction");

    GoElementType LITERAL_COMPOSITE = new GoElementTypeImpl("LiteralComposite");
    GoElementType LITERAL_COMPOSITE_VALUE = new GoElementTypeImpl("LiteralCompositeValue");
    GoElementType LITERAL_COMPOSITE_ELEMENT = new GoElementTypeImpl("LiteralCompositeElement");
    GoElementType LITERAL_COMPOSITE_ELEMENT_KEY = new GoElementTypeImpl("CompositeLiteralElementKey");
    GoElementType LITERAL_COMPOSITE_ELEMENT_VALUE = new GoElementTypeImpl("LiteralCompositeElementValue");

    // expressions
    GoElementType LITERAL_EXPRESSION = new GoElementTypeImpl("LiteralExpression");
    GoElementType PARENTHESISED_EXPRESSION = new GoElementTypeImpl("ParenthesisedExpression");

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

    GoElementType UNARY_EXPRESSION = new GoElementTypeImpl("UnaryExpression");
    GoElementType UNARY_OPERATOR = new GoElementTypeImpl("UnaryOp");

    GoElementType PRIMARY_EXPRESSION = new GoElementTypeImpl("primary expression");
    GoElementType SELECTOR_EXPRESSION = new GoElementTypeImpl("SelectorExpression");
    GoElementType BUILTIN_CALL_EXPRESSION = new GoElementTypeImpl("BuiltInCallExpression");

    GoElementType METHOD_EXPRESSION = new GoElementTypeImpl("MethodExpression");
    GoElementType EXPRESSION_LIST = new GoElementTypeImpl("ExpressionList");

    // types
    GoElementType TYPE_NAME = new GoElementTypeImpl("TypeName");

    GoStubElementType<GoTypeNameDeclarationStub, GoTypeNameDeclaration>
        TYPE_NAME_DECLARATION = new GoTypeNameDeclarationType();

    GoElementType TYPE_ARRAY = new GoElementTypeImpl("TypeArray");
    GoElementType TYPE_MAP = new GoElementTypeImpl("TypeMap");

    GoElementType TYPE_CHAN_SENDING = new GoElementTypeImpl("TypeChanSend");
    GoElementType TYPE_CHAN_RECEIVING = new GoElementTypeImpl("TypeChanRecv");
    GoElementType TYPE_CHAN_BIDIRECTIONAL = new GoElementTypeImpl("TypeChanBidi");

    GoElementType TYPE_SLICE = new GoElementTypeImpl("TypeSlice");
    GoElementType TYPE_POINTER = new GoElementTypeImpl("TypePointer");
    GoElementType TYPE_INTERFACE = new GoElementTypeImpl("TypeInterface");
    GoElementType TYPE_FUNCTION = new GoElementTypeImpl("TypeFunction");

    GoElementType INTERFACE_REFERENCE = new GoElementTypeImpl("InterfaceReference");

    GoElementType TYPE_STRUCT = new GoElementTypeImpl("TypeStruct");
    GoElementType TYPE_STRUCT_FIELD = new GoElementTypeImpl("TypeStructField");
    GoElementType TYPE_STRUCT_FIELD_ANONYMOUS = new GoElementTypeImpl("TypeStructFieldAnonymous");

    GoElementType TYPE_PARENTHESIZED = new GoElementTypeImpl("TypeParenthesized");
    GoElementType TYPE_LIST = new GoElementTypeImpl("TypeList");

    GoElementType FUNCTION_DECLARATION = new GoElementTypeImpl("FunctionDeclaration(f)");
    GoElementType FUNCTION_PARAMETER_LIST = new GoElementTypeImpl("FunctionParameterList");
    GoElementType FUNCTION_PARAMETER = new GoElementTypeImpl("FunctionParameter");
    GoElementType FUNCTION_PARAMETER_VARIADIC = new GoElementTypeImpl("FunctionParameterVariadic");
    GoElementType FUNCTION_RESULT = new GoElementTypeImpl("FunctionResult");
    GoElementType METHOD_DECLARATION = new GoElementTypeImpl("MethodDeclaration");
    GoElementType METHOD_RECEIVER = new GoElementTypeImpl("MethodReceiver");


    GoElementType REFERENCE_BASE_TYPE_NAME = new GoElementTypeImpl("ReferenceBaseTypeName");
    GoElementType BASE_TYPE_NAME = new GoElementTypeImpl("BaseTypeName");

    // statements
    GoElementType BLOCK_STATEMENT = new GoElementTypeImpl("BlockStmt");

    GoElementType FOR_WITH_CLAUSES_STATEMENT = new GoElementTypeImpl("ForWithClausesStmt");
    GoElementType FOR_WITH_CONDITION_STATEMENT = new GoElementTypeImpl("ForWithConditionStmt");
    GoElementType FOR_WITH_RANGE_STATEMENT = new GoElementTypeImpl("ForWithRangeStmt");
    GoElementType FOR_WITH_RANGE_AND_VARS_STATEMENT = new GoElementTypeImpl("ForWithRangeAndVarDeclarationsStmt");

    GoElementType SWITCH_TYPE_STATEMENT = new GoElementTypeImpl("SwitchTypeStmt");
    GoElementType SWITCH_TYPE_GUARD = new GoElementTypeImpl("SwitchTypeGuard");
    GoElementType SWITCH_TYPE_CASE = new GoElementTypeImpl("SwitchTypeCase");

    GoElementType SWITCH_EXPR_STATEMENT = new GoElementTypeImpl("SwitchExprStmt");
    GoElementType SWITCH_EXPR_CASE = new GoElementTypeImpl("SwitchExprCase");

    GoElementType SELECT_STATEMENT = new GoElementTypeImpl("SelectStmt");
    GoElementType SELECT_COMM_CLAUSE_RECV = new GoElementTypeImpl("SelectCommClauseRecv");
    GoElementType SELECT_COMM_CLAUSE_RECV_EXPR = new GoElementTypeImpl("SelectCommClauseRecvExpr");
    GoElementType SELECT_COMM_CLAUSE_SEND = new GoElementTypeImpl("SelectCommClauseSend");
    GoElementType SELECT_COMM_CLAUSE_DEFAULT = new GoElementTypeImpl("SelectCommClauseDefault");

    GoElementType IF_STATEMENT = new GoElementTypeImpl("IfStmt");

    GoElementType BREAK_STATEMENT = new GoElementTypeImpl("BreakStmt");
    GoElementType CONTINUE_STATEMENT = new GoElementTypeImpl("ContinueStmt");
    GoElementType GOTO_STATEMENT = new GoElementTypeImpl("GotoStmt");
    GoElementType FALLTHROUGH_STATEMENT = new GoElementTypeImpl("FallthroughStmt");
    GoElementType LABELED_STATEMENT = new GoElementTypeImpl("LabeledStmt");

    GoElementType ASSIGN_STATEMENT = new GoElementTypeImpl("AssignStmt");
    GoElementType SHORT_VAR_STATEMENT = new GoElementTypeImpl("ShortVarStmt");
    GoElementType SEND_STATEMENT = new GoElementTypeImpl("SendStmt");
    GoElementType INC_DEC_STATEMENT = new GoElementTypeImpl("IncDecStmt");
    GoElementType EXPRESSION_STATEMENT = new GoElementTypeImpl("ExpressionStmt");

    GoElementType RETURN_STATEMENT = new GoElementTypeImpl("ReturnStmt");
    GoElementType GO_STATEMENT = new GoElementTypeImpl("GoStmt");
    GoElementType DEFER_STATEMENT = new GoElementTypeImpl("DeferStmt");
    GoElementType EMPTY_STATEMENT = new GoElementTypeImpl("EmptyStmt");

    // sets
    public final TokenSet STMTS = TokenSet.create(
        ASSIGN_STATEMENT,
        BREAK_STATEMENT,
        CONTINUE_STATEMENT,
        CONST_DECLARATIONS,
        DEFER_STATEMENT,
        EMPTY_STATEMENT,
        EXPRESSION_STATEMENT,
        FALLTHROUGH_STATEMENT,
        FOR_WITH_CLAUSES_STATEMENT,
        FOR_WITH_CONDITION_STATEMENT,
        FOR_WITH_RANGE_STATEMENT,
        FOR_WITH_RANGE_AND_VARS_STATEMENT,
        GO_STATEMENT,
        GOTO_STATEMENT,
        IF_STATEMENT,
        INC_DEC_STATEMENT,
        LABELED_STATEMENT,
        RETURN_STATEMENT,
        SEND_STATEMENT,
        SELECT_STATEMENT,
        SHORT_VAR_STATEMENT,
        SWITCH_EXPR_STATEMENT,
        SWITCH_EXPR_STATEMENT,
        TYPE_DECLARATIONS,
        VAR_DECLARATIONS
    );

    public final TokenSet COMMENTS = TokenSet.create(
        mSL_COMMENT,
        mML_COMMENT
    );

    public final TokenSet LITERALS_INT = TokenSet.create(
        litHEX, litINT, litOCT
    );

    public final TokenSet LITERALS_IMAGINARY = TokenSet.create(
        litFLOAT_I, litDECIMAL_I
    );

    public final TokenSet LITERALS_FLOAT = TokenSet.create(
        litFLOAT
    );

    public final TokenSet SELECT_CLAUSES = TokenSet.create(
        SELECT_COMM_CLAUSE_DEFAULT,
        SELECT_COMM_CLAUSE_RECV,
        SELECT_COMM_CLAUSE_SEND
    );

    public final TokenSet OPS_BINARY = TokenSet.create(
        oPLUS, oPLUS_ASSIGN, oMINUS, oMINUS_ASSIGN,
        oMUL, oMUL_ASSIGN, oQUOTIENT, oQUOTIENT_ASSIGN,
        oREMAINDER, oREMAINDER_ASSIGN,

        oBIT_AND, oBIT_AND_ASSIGN, oBIT_OR, oBIT_OR_ASSIGN,
        oBIT_XOR, oBIT_XOR_ASSIGN,
        oSHIFT_LEFT, oSHIFT_LEFT_ASSIGN, oSHIFT_RIGHT, oSHIFT_RIGHT_ASSIGN,
        oBIT_CLEAR, oBIT_CLEAR_ASSIGN,

        oCOND_AND, oCOND_OR
    );

    public final TokenSet OPS_RELATIONAL = TokenSet.create(
        oEQ, oNOT_EQ, oLESS, oLESS_OR_EQUAL, oGREATER, oGREATER_OR_EQUAL
    );

    public final TokenSet FUNCTION_CALLS = TokenSet.create(
        CALL_OR_CONVERSION_EXPRESSION,
        BUILTIN_CALL_EXPRESSION
    );

    public final TokenSet STMTS_FOR = TokenSet.create(
        FOR_WITH_CLAUSES_STATEMENT,
        FOR_WITH_CONDITION_STATEMENT,
        FOR_WITH_RANGE_STATEMENT,
        FOR_WITH_RANGE_AND_VARS_STATEMENT
    );

    public final TokenSet STMTS_SWITCH = TokenSet.create(
        SWITCH_EXPR_STATEMENT,
        SWITCH_TYPE_STATEMENT
    );

    public final TokenSet TYPE_STRUCT_FIELDS = TokenSet.create(
        TYPE_STRUCT_FIELD,
        TYPE_STRUCT_FIELD_ANONYMOUS
    );

    public final TokenSet EXPRESSIONS = TokenSet.create(
        LITERAL_EXPRESSION,
        ADD_EXPRESSION, MUL_EXPRESSION, REL_EXPRESSION, COM_EXPRESSION,
        LOG_OR_EXPRESSION, LOG_AND_EXPRESSION,
        PRIMARY_EXPRESSION,
        INDEX_EXPRESSION, SLICE_EXPRESSION, SELECTOR_EXPRESSION, TYPE_ASSERTION_EXPRESSION,
        BUILTIN_CALL_EXPRESSION, CALL_OR_CONVERSION_EXPRESSION,
        PARENTHESISED_EXPRESSION,
        UNARY_EXPRESSION
    );

    public final TokenSet STMTS_OR_COMMENTS = TokenSet.orSet(STMTS, COMMENTS);
}
