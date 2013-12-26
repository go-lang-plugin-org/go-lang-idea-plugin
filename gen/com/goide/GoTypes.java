// This is a generated file. Not intended for manual editing.
package com.goide;

import com.goide.psi.GoCompositeElementType;
import com.goide.psi.GoTokenType;
import com.goide.psi.impl.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;

public interface GoTypes {

  IElementType ADD_OP = new GoCompositeElementType("ADD_OP");
  IElementType ANONYMOUS_FIELD = new GoCompositeElementType("ANONYMOUS_FIELD");
  IElementType ARGUMENT_LIST = new GoCompositeElementType("ARGUMENT_LIST");
  IElementType ARRAY_LENGTH = new GoCompositeElementType("ARRAY_LENGTH");
  IElementType ARRAY_TYPE = new GoCompositeElementType("ARRAY_TYPE");
  IElementType ASSIGNMENT = new GoCompositeElementType("ASSIGNMENT");
  IElementType ASSIGN_OP = new GoCompositeElementType("ASSIGN_OP");
  IElementType BASE_TYPE = new GoCompositeElementType("BASE_TYPE");
  IElementType BASE_TYPE_NAME = new GoCompositeElementType("BASE_TYPE_NAME");
  IElementType BASIC_LIT = new GoCompositeElementType("BASIC_LIT");
  IElementType BINARY_OP = new GoCompositeElementType("BINARY_OP");
  IElementType BLOCK = new GoCompositeElementType("BLOCK");
  IElementType BREAK_STATEMENT = new GoCompositeElementType("BREAK_STATEMENT");
  IElementType BUILTIN_ARGS = new GoCompositeElementType("BUILTIN_ARGS");
  IElementType BUILTIN_CALL = new GoCompositeElementType("BUILTIN_CALL");
  IElementType CALL = new GoCompositeElementType("CALL");
  IElementType CHANNEL = new GoCompositeElementType("CHANNEL");
  IElementType CHANNEL_TYPE = new GoCompositeElementType("CHANNEL_TYPE");
  IElementType COMM_CASE = new GoCompositeElementType("COMM_CASE");
  IElementType COMM_CLAUSE = new GoCompositeElementType("COMM_CLAUSE");
  IElementType COMPOSITE_LIT = new GoCompositeElementType("COMPOSITE_LIT");
  IElementType CONST_DECL = new GoCompositeElementType("CONST_DECL");
  IElementType CONST_SPEC = new GoCompositeElementType("CONST_SPEC");
  IElementType CONTINUE_STATEMENT = new GoCompositeElementType("CONTINUE_STATEMENT");
  IElementType CONVERSION = new GoCompositeElementType("CONVERSION");
  IElementType DECLARATION_STATEMENT = new GoCompositeElementType("DECLARATION_STATEMENT");
  IElementType DEFER_STATEMENT = new GoCompositeElementType("DEFER_STATEMENT");
  IElementType DUMMY = new GoCompositeElementType("DUMMY");
  IElementType ELEMENT = new GoCompositeElementType("ELEMENT");
  IElementType ELEMENT_INDEX = new GoCompositeElementType("ELEMENT_INDEX");
  IElementType ELEMENT_LIST = new GoCompositeElementType("ELEMENT_LIST");
  IElementType ELEMENT_TYPE = new GoCompositeElementType("ELEMENT_TYPE");
  IElementType EXPRESSION = new GoCompositeElementType("EXPRESSION");
  IElementType EXPRESSION_LIST = new GoCompositeElementType("EXPRESSION_LIST");
  IElementType EXPRESSION_STATEMENT = new GoCompositeElementType("EXPRESSION_STATEMENT");
  IElementType EXPR_CASE_CLAUSE = new GoCompositeElementType("EXPR_CASE_CLAUSE");
  IElementType EXPR_SWITCH_CASE = new GoCompositeElementType("EXPR_SWITCH_CASE");
  IElementType EXPR_SWITCH_STATEMENT = new GoCompositeElementType("EXPR_SWITCH_STATEMENT");
  IElementType FALLTHROUGH_STATEMENT = new GoCompositeElementType("FALLTHROUGH_STATEMENT");
  IElementType FIELD_DECL = new GoCompositeElementType("FIELD_DECL");
  IElementType FIELD_NAME = new GoCompositeElementType("FIELD_NAME");
  IElementType FOR_CLAUSE = new GoCompositeElementType("FOR_CLAUSE");
  IElementType FOR_STATEMENT = new GoCompositeElementType("FOR_STATEMENT");
  IElementType FUNCTION = new GoCompositeElementType("FUNCTION");
  IElementType FUNCTION_BODY = new GoCompositeElementType("FUNCTION_BODY");
  IElementType FUNCTION_DECL = new GoCompositeElementType("FUNCTION_DECL");
  IElementType FUNCTION_LIT = new GoCompositeElementType("FUNCTION_LIT");
  IElementType FUNCTION_NAME = new GoCompositeElementType("FUNCTION_NAME");
  IElementType FUNCTION_TYPE = new GoCompositeElementType("FUNCTION_TYPE");
  IElementType GOTO_STATEMENT = new GoCompositeElementType("GOTO_STATEMENT");
  IElementType GO_STATEMENT = new GoCompositeElementType("GO_STATEMENT");
  IElementType GO_TYPE = new GoCompositeElementType("GO_TYPE");
  IElementType IDENTIFIER_LIST = new GoCompositeElementType("IDENTIFIER_LIST");
  IElementType IF_STATEMENT = new GoCompositeElementType("IF_STATEMENT");
  IElementType IMPORT_DECL = new GoCompositeElementType("IMPORT_DECL");
  IElementType IMPORT_PATH = new GoCompositeElementType("IMPORT_PATH");
  IElementType IMPORT_SPEC = new GoCompositeElementType("IMPORT_SPEC");
  IElementType INC_DEC_STATEMENT = new GoCompositeElementType("INC_DEC_STATEMENT");
  IElementType INDEX = new GoCompositeElementType("INDEX");
  IElementType INTERFACE_TYPE = new GoCompositeElementType("INTERFACE_TYPE");
  IElementType INTERFACE_TYPE_NAME = new GoCompositeElementType("INTERFACE_TYPE_NAME");
  IElementType KEY = new GoCompositeElementType("KEY");
  IElementType KEY_TYPE = new GoCompositeElementType("KEY_TYPE");
  IElementType LABEL = new GoCompositeElementType("LABEL");
  IElementType LABELED_STATEMENT = new GoCompositeElementType("LABELED_STATEMENT");
  IElementType LITERAL = new GoCompositeElementType("LITERAL");
  IElementType LITERAL_TYPE = new GoCompositeElementType("LITERAL_TYPE");
  IElementType LITERAL_VALUE = new GoCompositeElementType("LITERAL_VALUE");
  IElementType MAP_TYPE = new GoCompositeElementType("MAP_TYPE");
  IElementType METHOD_DECL = new GoCompositeElementType("METHOD_DECL");
  IElementType METHOD_EXPR = new GoCompositeElementType("METHOD_EXPR");
  IElementType METHOD_NAME = new GoCompositeElementType("METHOD_NAME");
  IElementType METHOD_SPEC = new GoCompositeElementType("METHOD_SPEC");
  IElementType MUL_OP = new GoCompositeElementType("MUL_OP");
  IElementType OPERAND = new GoCompositeElementType("OPERAND");
  IElementType OPERAND_NAME = new GoCompositeElementType("OPERAND_NAME");
  IElementType PACKAGE_CLAUSE = new GoCompositeElementType("PACKAGE_CLAUSE");
  IElementType PARAMETERS = new GoCompositeElementType("PARAMETERS");
  IElementType PARAMETER_DECL = new GoCompositeElementType("PARAMETER_DECL");
  IElementType PARAMETER_LIST = new GoCompositeElementType("PARAMETER_LIST");
  IElementType POINTER_TYPE = new GoCompositeElementType("POINTER_TYPE");
  IElementType PRIMARY_EXPR = new GoCompositeElementType("PRIMARY_EXPR");
  IElementType QUALIFIED_IDENT = new GoCompositeElementType("QUALIFIED_IDENT");
  IElementType RANGE_CLAUSE = new GoCompositeElementType("RANGE_CLAUSE");
  IElementType RECEIVER = new GoCompositeElementType("RECEIVER");
  IElementType RECEIVER_TYPE = new GoCompositeElementType("RECEIVER_TYPE");
  IElementType RECV_STATEMENT = new GoCompositeElementType("RECV_STATEMENT");
  IElementType REL_OP = new GoCompositeElementType("REL_OP");
  IElementType RESULT = new GoCompositeElementType("RESULT");
  IElementType RETURN_STATEMENT = new GoCompositeElementType("RETURN_STATEMENT");
  IElementType SELECTOR = new GoCompositeElementType("SELECTOR");
  IElementType SELECT_STATEMENT = new GoCompositeElementType("SELECT_STATEMENT");
  IElementType SEND_STATEMENT = new GoCompositeElementType("SEND_STATEMENT");
  IElementType SHORT_VAR_DECL = new GoCompositeElementType("SHORT_VAR_DECL");
  IElementType SIGNATURE = new GoCompositeElementType("SIGNATURE");
  IElementType SIMPLE_STATEMENT = new GoCompositeElementType("SIMPLE_STATEMENT");
  IElementType SLICE = new GoCompositeElementType("SLICE");
  IElementType SLICE_TYPE = new GoCompositeElementType("SLICE_TYPE");
  IElementType STATEMENT = new GoCompositeElementType("STATEMENT");
  IElementType STATEMENT_LIST = new GoCompositeElementType("STATEMENT_LIST");
  IElementType STRUCT_TYPE = new GoCompositeElementType("STRUCT_TYPE");
  IElementType SWITCH_STATEMENT = new GoCompositeElementType("SWITCH_STATEMENT");
  IElementType TAG = new GoCompositeElementType("TAG");
  IElementType TOP_LEVEL_DECL = new GoCompositeElementType("TOP_LEVEL_DECL");
  IElementType TYPE_ASSERTION = new GoCompositeElementType("TYPE_ASSERTION");
  IElementType TYPE_CASE_CLAUSE = new GoCompositeElementType("TYPE_CASE_CLAUSE");
  IElementType TYPE_DECL = new GoCompositeElementType("TYPE_DECL");
  IElementType TYPE_LIST = new GoCompositeElementType("TYPE_LIST");
  IElementType TYPE_LIT = new GoCompositeElementType("TYPE_LIT");
  IElementType TYPE_NAME = new GoCompositeElementType("TYPE_NAME");
  IElementType TYPE_SPEC = new GoCompositeElementType("TYPE_SPEC");
  IElementType TYPE_SWITCH_CASE = new GoCompositeElementType("TYPE_SWITCH_CASE");
  IElementType TYPE_SWITCH_GUARD = new GoCompositeElementType("TYPE_SWITCH_GUARD");
  IElementType TYPE_SWITCH_STATEMENT = new GoCompositeElementType("TYPE_SWITCH_STATEMENT");
  IElementType UNARY_EXPR = new GoCompositeElementType("UNARY_EXPR");
  IElementType UNARY_OP = new GoCompositeElementType("UNARY_OP");
  IElementType VALUE = new GoCompositeElementType("VALUE");
  IElementType VAR_DECL = new GoCompositeElementType("VAR_DECL");
  IElementType VAR_SPEC = new GoCompositeElementType("VAR_SPEC");

  IElementType ASSIGN = new GoTokenType("=");
  IElementType BIT_AND = new GoTokenType("&");
  IElementType BIT_AND_ASSIGN = new GoTokenType("&=");
  IElementType BIT_CLEAR = new GoTokenType("&^");
  IElementType BIT_CLEAR_ASSIGN = new GoTokenType("&^=");
  IElementType BIT_OR = new GoTokenType("|");
  IElementType BIT_OR_ASSIGN = new GoTokenType("|=");
  IElementType BIT_XOR = new GoTokenType("^");
  IElementType BIT_XOR_ASSIGN = new GoTokenType("^=");
  IElementType BREAK = new GoTokenType("break");
  IElementType CASE = new GoTokenType("case");
  IElementType CHAN = new GoTokenType("chan");
  IElementType CHAR = new GoTokenType("char");
  IElementType COLON = new GoTokenType(":");
  IElementType COMMA = new GoTokenType(",");
  IElementType COND_AND = new GoTokenType("&&");
  IElementType COND_OR = new GoTokenType("||");
  IElementType CONST = new GoTokenType("const");
  IElementType CONTINUE = new GoTokenType("continue");
  IElementType DECIMAL_I = new GoTokenType("decimal_i");
  IElementType DEFAULT = new GoTokenType("default");
  IElementType DEFER = new GoTokenType("defer");
  IElementType DOT = new GoTokenType(".");
  IElementType ELSE = new GoTokenType("else");
  IElementType EQ = new GoTokenType("==");
  IElementType FALLTHROUGH = new GoTokenType("fallthrough");
  IElementType FLOAT = new GoTokenType("float");
  IElementType FLOAT_I = new GoTokenType("float_i");
  IElementType FOR = new GoTokenType("for");
  IElementType FUNC = new GoTokenType("func");
  IElementType GO = new GoTokenType("go");
  IElementType GOTO = new GoTokenType("goto");
  IElementType GREATER = new GoTokenType(">");
  IElementType GREATER_OR_EQUAL = new GoTokenType(">=");
  IElementType HEX = new GoTokenType("hex");
  IElementType IDENTIFIER = new GoTokenType("identifier");
  IElementType IF = new GoTokenType("if");
  IElementType IMAGINARY = new GoTokenType("imaginary");
  IElementType IMPORT = new GoTokenType("import");
  IElementType INT = new GoTokenType("int");
  IElementType INTERFACE = new GoTokenType("interface");
  IElementType LBRACE = new GoTokenType("{");
  IElementType LBRACK = new GoTokenType("[");
  IElementType LESS = new GoTokenType("<");
  IElementType LESS_OR_EQUAL = new GoTokenType("<=");
  IElementType LPAREN = new GoTokenType("(");
  IElementType MAP = new GoTokenType("map");
  IElementType MINUS = new GoTokenType("-");
  IElementType MINUS_ASSIGN = new GoTokenType("-=");
  IElementType MINUS_MINUS = new GoTokenType("--");
  IElementType MUL = new GoTokenType("*");
  IElementType MUL_ASSIGN = new GoTokenType("*=");
  IElementType NOT = new GoTokenType("!");
  IElementType NOT_EQ = new GoTokenType("!=");
  IElementType OCT = new GoTokenType("oct");
  IElementType PACKAGE = new GoTokenType("package");
  IElementType PLUS = new GoTokenType("+");
  IElementType PLUS_ASSIGN = new GoTokenType("+=");
  IElementType PLUS_PLUS = new GoTokenType("++");
  IElementType QUOTIENT = new GoTokenType("/");
  IElementType QUOTIENT_ASSIGN = new GoTokenType("/=");
  IElementType RANGE = new GoTokenType("range");
  IElementType RBRACE = new GoTokenType("}");
  IElementType RBRACK = new GoTokenType("]");
  IElementType REMAINDER = new GoTokenType("%");
  IElementType REMAINDER_ASSIGN = new GoTokenType("%=");
  IElementType RETURN = new GoTokenType("return");
  IElementType RPAREN = new GoTokenType(")");
  IElementType RUNE = new GoTokenType("rune");
  IElementType SELECT = new GoTokenType("select");
  IElementType SEMICOLON = new GoTokenType(";");
  IElementType SEMICOLON_SYNTHETIC = new GoTokenType("<NL>");
  IElementType SEND_CHANNEL = new GoTokenType("<-");
  IElementType SHIFT_LEFT = new GoTokenType("<<");
  IElementType SHIFT_LEFT_ASSIGN = new GoTokenType("<<=");
  IElementType SHIFT_RIGHT = new GoTokenType(">>");
  IElementType SHIFT_RIGHT_ASSIGN = new GoTokenType(">>=");
  IElementType STRING = new GoTokenType("string");
  IElementType STRUCT = new GoTokenType("struct");
  IElementType SWITCH = new GoTokenType("switch");
  IElementType TRIPLE_DOT = new GoTokenType("...");
  IElementType TYPE = new GoTokenType("type");
  IElementType VAR = new GoTokenType("var");
  IElementType VAR_ASSIGN = new GoTokenType(":=");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == ADD_OP) {
        return new GoAddOpImpl(node);
      }
      else if (type == ANONYMOUS_FIELD) {
        return new GoAnonymousFieldImpl(node);
      }
      else if (type == ARGUMENT_LIST) {
        return new GoArgumentListImpl(node);
      }
      else if (type == ARRAY_LENGTH) {
        return new GoArrayLengthImpl(node);
      }
      else if (type == ARRAY_TYPE) {
        return new GoArrayTypeImpl(node);
      }
      else if (type == ASSIGNMENT) {
        return new GoAssignmentImpl(node);
      }
      else if (type == ASSIGN_OP) {
        return new GoAssignOpImpl(node);
      }
      else if (type == BASE_TYPE) {
        return new GoBaseTypeImpl(node);
      }
      else if (type == BASE_TYPE_NAME) {
        return new GoBaseTypeNameImpl(node);
      }
      else if (type == BASIC_LIT) {
        return new GoBasicLitImpl(node);
      }
      else if (type == BINARY_OP) {
        return new GoBinaryOpImpl(node);
      }
      else if (type == BLOCK) {
        return new GoBlockImpl(node);
      }
      else if (type == BREAK_STATEMENT) {
        return new GoBreakStatementImpl(node);
      }
      else if (type == BUILTIN_ARGS) {
        return new GoBuiltinArgsImpl(node);
      }
      else if (type == BUILTIN_CALL) {
        return new GoBuiltinCallImpl(node);
      }
      else if (type == CALL) {
        return new GoCallImpl(node);
      }
      else if (type == CHANNEL) {
        return new GoChannelImpl(node);
      }
      else if (type == CHANNEL_TYPE) {
        return new GoChannelTypeImpl(node);
      }
      else if (type == COMM_CASE) {
        return new GoCommCaseImpl(node);
      }
      else if (type == COMM_CLAUSE) {
        return new GoCommClauseImpl(node);
      }
      else if (type == COMPOSITE_LIT) {
        return new GoCompositeLitImpl(node);
      }
      else if (type == CONST_DECL) {
        return new GoConstDeclImpl(node);
      }
      else if (type == CONST_SPEC) {
        return new GoConstSpecImpl(node);
      }
      else if (type == CONTINUE_STATEMENT) {
        return new GoContinueStatementImpl(node);
      }
      else if (type == CONVERSION) {
        return new GoConversionImpl(node);
      }
      else if (type == DECLARATION_STATEMENT) {
        return new GoDeclarationStatementImpl(node);
      }
      else if (type == DEFER_STATEMENT) {
        return new GoDeferStatementImpl(node);
      }
      else if (type == DUMMY) {
        return new GoDummyImpl(node);
      }
      else if (type == ELEMENT) {
        return new GoElementImpl(node);
      }
      else if (type == ELEMENT_INDEX) {
        return new GoElementIndexImpl(node);
      }
      else if (type == ELEMENT_LIST) {
        return new GoElementListImpl(node);
      }
      else if (type == ELEMENT_TYPE) {
        return new GoElementTypeImpl(node);
      }
      else if (type == EXPRESSION) {
        return new GoExpressionImpl(node);
      }
      else if (type == EXPRESSION_LIST) {
        return new GoExpressionListImpl(node);
      }
      else if (type == EXPRESSION_STATEMENT) {
        return new GoExpressionStatementImpl(node);
      }
      else if (type == EXPR_CASE_CLAUSE) {
        return new GoExprCaseClauseImpl(node);
      }
      else if (type == EXPR_SWITCH_CASE) {
        return new GoExprSwitchCaseImpl(node);
      }
      else if (type == EXPR_SWITCH_STATEMENT) {
        return new GoExprSwitchStatementImpl(node);
      }
      else if (type == FALLTHROUGH_STATEMENT) {
        return new GoFallthroughStatementImpl(node);
      }
      else if (type == FIELD_DECL) {
        return new GoFieldDeclImpl(node);
      }
      else if (type == FIELD_NAME) {
        return new GoFieldNameImpl(node);
      }
      else if (type == FOR_CLAUSE) {
        return new GoForClauseImpl(node);
      }
      else if (type == FOR_STATEMENT) {
        return new GoForStatementImpl(node);
      }
      else if (type == FUNCTION) {
        return new GoFunctionImpl(node);
      }
      else if (type == FUNCTION_BODY) {
        return new GoFunctionBodyImpl(node);
      }
      else if (type == FUNCTION_DECL) {
        return new GoFunctionDeclImpl(node);
      }
      else if (type == FUNCTION_LIT) {
        return new GoFunctionLitImpl(node);
      }
      else if (type == FUNCTION_NAME) {
        return new GoFunctionNameImpl(node);
      }
      else if (type == FUNCTION_TYPE) {
        return new GoFunctionTypeImpl(node);
      }
      else if (type == GOTO_STATEMENT) {
        return new GoGotoStatementImpl(node);
      }
      else if (type == GO_STATEMENT) {
        return new GoGoStatementImpl(node);
      }
      else if (type == GO_TYPE) {
        return new GoGoTypeImpl(node);
      }
      else if (type == IDENTIFIER_LIST) {
        return new GoIdentifierListImpl(node);
      }
      else if (type == IF_STATEMENT) {
        return new GoIfStatementImpl(node);
      }
      else if (type == IMPORT_DECL) {
        return new GoImportDeclImpl(node);
      }
      else if (type == IMPORT_PATH) {
        return new GoImportPathImpl(node);
      }
      else if (type == IMPORT_SPEC) {
        return new GoImportSpecImpl(node);
      }
      else if (type == INC_DEC_STATEMENT) {
        return new GoIncDecStatementImpl(node);
      }
      else if (type == INDEX) {
        return new GoIndexImpl(node);
      }
      else if (type == INTERFACE_TYPE) {
        return new GoInterfaceTypeImpl(node);
      }
      else if (type == INTERFACE_TYPE_NAME) {
        return new GoInterfaceTypeNameImpl(node);
      }
      else if (type == KEY) {
        return new GoKeyImpl(node);
      }
      else if (type == KEY_TYPE) {
        return new GoKeyTypeImpl(node);
      }
      else if (type == LABEL) {
        return new GoLabelImpl(node);
      }
      else if (type == LABELED_STATEMENT) {
        return new GoLabeledStatementImpl(node);
      }
      else if (type == LITERAL) {
        return new GoLiteralImpl(node);
      }
      else if (type == LITERAL_TYPE) {
        return new GoLiteralTypeImpl(node);
      }
      else if (type == LITERAL_VALUE) {
        return new GoLiteralValueImpl(node);
      }
      else if (type == MAP_TYPE) {
        return new GoMapTypeImpl(node);
      }
      else if (type == METHOD_DECL) {
        return new GoMethodDeclImpl(node);
      }
      else if (type == METHOD_EXPR) {
        return new GoMethodExprImpl(node);
      }
      else if (type == METHOD_NAME) {
        return new GoMethodNameImpl(node);
      }
      else if (type == METHOD_SPEC) {
        return new GoMethodSpecImpl(node);
      }
      else if (type == MUL_OP) {
        return new GoMulOpImpl(node);
      }
      else if (type == OPERAND) {
        return new GoOperandImpl(node);
      }
      else if (type == OPERAND_NAME) {
        return new GoOperandNameImpl(node);
      }
      else if (type == PACKAGE_CLAUSE) {
        return new GoPackageClauseImpl(node);
      }
      else if (type == PARAMETERS) {
        return new GoParametersImpl(node);
      }
      else if (type == PARAMETER_DECL) {
        return new GoParameterDeclImpl(node);
      }
      else if (type == PARAMETER_LIST) {
        return new GoParameterListImpl(node);
      }
      else if (type == POINTER_TYPE) {
        return new GoPointerTypeImpl(node);
      }
      else if (type == PRIMARY_EXPR) {
        return new GoPrimaryExprImpl(node);
      }
      else if (type == QUALIFIED_IDENT) {
        return new GoQualifiedIdentImpl(node);
      }
      else if (type == RANGE_CLAUSE) {
        return new GoRangeClauseImpl(node);
      }
      else if (type == RECEIVER) {
        return new GoReceiverImpl(node);
      }
      else if (type == RECEIVER_TYPE) {
        return new GoReceiverTypeImpl(node);
      }
      else if (type == RECV_STATEMENT) {
        return new GoRecvStatementImpl(node);
      }
      else if (type == REL_OP) {
        return new GoRelOpImpl(node);
      }
      else if (type == RESULT) {
        return new GoResultImpl(node);
      }
      else if (type == RETURN_STATEMENT) {
        return new GoReturnStatementImpl(node);
      }
      else if (type == SELECTOR) {
        return new GoSelectorImpl(node);
      }
      else if (type == SELECT_STATEMENT) {
        return new GoSelectStatementImpl(node);
      }
      else if (type == SEND_STATEMENT) {
        return new GoSendStatementImpl(node);
      }
      else if (type == SHORT_VAR_DECL) {
        return new GoShortVarDeclImpl(node);
      }
      else if (type == SIGNATURE) {
        return new GoSignatureImpl(node);
      }
      else if (type == SIMPLE_STATEMENT) {
        return new GoSimpleStatementImpl(node);
      }
      else if (type == SLICE) {
        return new GoSliceImpl(node);
      }
      else if (type == SLICE_TYPE) {
        return new GoSliceTypeImpl(node);
      }
      else if (type == STATEMENT) {
        return new GoStatementImpl(node);
      }
      else if (type == STATEMENT_LIST) {
        return new GoStatementListImpl(node);
      }
      else if (type == STRUCT_TYPE) {
        return new GoStructTypeImpl(node);
      }
      else if (type == SWITCH_STATEMENT) {
        return new GoSwitchStatementImpl(node);
      }
      else if (type == TAG) {
        return new GoTagImpl(node);
      }
      else if (type == TOP_LEVEL_DECL) {
        return new GoTopLevelDeclImpl(node);
      }
      else if (type == TYPE_ASSERTION) {
        return new GoTypeAssertionImpl(node);
      }
      else if (type == TYPE_CASE_CLAUSE) {
        return new GoTypeCaseClauseImpl(node);
      }
      else if (type == TYPE_DECL) {
        return new GoTypeDeclImpl(node);
      }
      else if (type == TYPE_LIST) {
        return new GoTypeListImpl(node);
      }
      else if (type == TYPE_LIT) {
        return new GoTypeLitImpl(node);
      }
      else if (type == TYPE_NAME) {
        return new GoTypeNameImpl(node);
      }
      else if (type == TYPE_SPEC) {
        return new GoTypeSpecImpl(node);
      }
      else if (type == TYPE_SWITCH_CASE) {
        return new GoTypeSwitchCaseImpl(node);
      }
      else if (type == TYPE_SWITCH_GUARD) {
        return new GoTypeSwitchGuardImpl(node);
      }
      else if (type == TYPE_SWITCH_STATEMENT) {
        return new GoTypeSwitchStatementImpl(node);
      }
      else if (type == UNARY_EXPR) {
        return new GoUnaryExprImpl(node);
      }
      else if (type == UNARY_OP) {
        return new GoUnaryOpImpl(node);
      }
      else if (type == VALUE) {
        return new GoValueImpl(node);
      }
      else if (type == VAR_DECL) {
        return new GoVarDeclImpl(node);
      }
      else if (type == VAR_SPEC) {
        return new GoVarSpecImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
