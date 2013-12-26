// This is a generated file. Not intended for manual editing.
package com.goide.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.lang.PsiParser;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.tree.IElementType;

import static com.goide.GoTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class GoParser implements PsiParser {

  public static final Logger LOG_ = Logger.getInstance("com.goide.parser.GoParser");

  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, null);
    Marker marker_ = enter_section_(builder_, 0, _COLLAPSE_, null);
    if (root_ == GO_ANONYMOUS_FIELD) {
      result_ = AnonymousField(builder_, 0);
    }
    else if (root_ == GO_ARGUMENT_LIST) {
      result_ = ArgumentList(builder_, 0);
    }
    else if (root_ == GO_ARRAY_LENGTH) {
      result_ = ArrayLength(builder_, 0);
    }
    else if (root_ == GO_ARRAY_TYPE) {
      result_ = ArrayType(builder_, 0);
    }
    else if (root_ == GO_ASSIGNMENT) {
      result_ = Assignment(builder_, 0);
    }
    else if (root_ == GO_BASE_TYPE) {
      result_ = BaseType(builder_, 0);
    }
    else if (root_ == GO_BASE_TYPE_NAME) {
      result_ = BaseTypeName(builder_, 0);
    }
    else if (root_ == GO_BASIC_LIT) {
      result_ = BasicLit(builder_, 0);
    }
    else if (root_ == GO_BLOCK) {
      result_ = Block(builder_, 0);
    }
    else if (root_ == GO_BREAK_STATEMENT) {
      result_ = BreakStatement(builder_, 0);
    }
    else if (root_ == GO_BUILTIN_ARGS) {
      result_ = BuiltinArgs(builder_, 0);
    }
    else if (root_ == GO_BUILTIN_CALL) {
      result_ = BuiltinCall(builder_, 0);
    }
    else if (root_ == GO_CALL) {
      result_ = Call(builder_, 0);
    }
    else if (root_ == GO_CHANNEL) {
      result_ = Channel(builder_, 0);
    }
    else if (root_ == GO_CHANNEL_TYPE) {
      result_ = ChannelType(builder_, 0);
    }
    else if (root_ == GO_COMM_CASE) {
      result_ = CommCase(builder_, 0);
    }
    else if (root_ == GO_COMM_CLAUSE) {
      result_ = CommClause(builder_, 0);
    }
    else if (root_ == GO_COMPOSITE_LIT) {
      result_ = CompositeLit(builder_, 0);
    }
    else if (root_ == GO_CONST_DECL) {
      result_ = ConstDecl(builder_, 0);
    }
    else if (root_ == GO_CONST_SPEC) {
      result_ = ConstSpec(builder_, 0);
    }
    else if (root_ == GO_CONTINUE_STATEMENT) {
      result_ = ContinueStatement(builder_, 0);
    }
    else if (root_ == GO_CONVERSION) {
      result_ = Conversion(builder_, 0);
    }
    else if (root_ == GO_DECLARATION_STATEMENT) {
      result_ = DeclarationStatement(builder_, 0);
    }
    else if (root_ == GO_DEFER_STATEMENT) {
      result_ = DeferStatement(builder_, 0);
    }
    else if (root_ == GO_ELEMENT) {
      result_ = Element(builder_, 0);
    }
    else if (root_ == GO_ELEMENT_INDEX) {
      result_ = ElementIndex(builder_, 0);
    }
    else if (root_ == GO_ELEMENT_LIST) {
      result_ = ElementList(builder_, 0);
    }
    else if (root_ == GO_ELEMENT_TYPE) {
      result_ = ElementType(builder_, 0);
    }
    else if (root_ == GO_EXPR_CASE_CLAUSE) {
      result_ = ExprCaseClause(builder_, 0);
    }
    else if (root_ == GO_EXPR_SWITCH_CASE) {
      result_ = ExprSwitchCase(builder_, 0);
    }
    else if (root_ == GO_EXPR_SWITCH_STATEMENT) {
      result_ = ExprSwitchStatement(builder_, 0);
    }
    else if (root_ == GO_EXPRESSION) {
      result_ = Expression(builder_, 0);
    }
    else if (root_ == GO_EXPRESSION_LIST) {
      result_ = ExpressionList(builder_, 0);
    }
    else if (root_ == GO_EXPRESSION_STATEMENT) {
      result_ = ExpressionStatement(builder_, 0);
    }
    else if (root_ == GO_FALLTHROUGH_STATEMENT) {
      result_ = FallthroughStatement(builder_, 0);
    }
    else if (root_ == GO_FIELD_DECL) {
      result_ = FieldDecl(builder_, 0);
    }
    else if (root_ == GO_FIELD_NAME) {
      result_ = FieldName(builder_, 0);
    }
    else if (root_ == GO_FOR_CLAUSE) {
      result_ = ForClause(builder_, 0);
    }
    else if (root_ == GO_FOR_STATEMENT) {
      result_ = ForStatement(builder_, 0);
    }
    else if (root_ == GO_FUNCTION) {
      result_ = Function(builder_, 0);
    }
    else if (root_ == GO_FUNCTION_BODY) {
      result_ = FunctionBody(builder_, 0);
    }
    else if (root_ == GO_FUNCTION_DECL) {
      result_ = FunctionDecl(builder_, 0);
    }
    else if (root_ == GO_FUNCTION_LIT) {
      result_ = FunctionLit(builder_, 0);
    }
    else if (root_ == GO_FUNCTION_NAME) {
      result_ = FunctionName(builder_, 0);
    }
    else if (root_ == GO_FUNCTION_TYPE) {
      result_ = FunctionType(builder_, 0);
    }
    else if (root_ == GO_GO_STATEMENT) {
      result_ = GoStatement(builder_, 0);
    }
    else if (root_ == GO_GO_TYPE) {
      result_ = GoType(builder_, 0);
    }
    else if (root_ == GO_GOTO_STATEMENT) {
      result_ = GotoStatement(builder_, 0);
    }
    else if (root_ == GO_IDENTIFIER_LIST) {
      result_ = IdentifierList(builder_, 0);
    }
    else if (root_ == GO_IF_STATEMENT) {
      result_ = IfStatement(builder_, 0);
    }
    else if (root_ == GO_IMPORT_DECL) {
      result_ = ImportDecl(builder_, 0);
    }
    else if (root_ == GO_IMPORT_PATH) {
      result_ = ImportPath(builder_, 0);
    }
    else if (root_ == GO_IMPORT_SPEC) {
      result_ = ImportSpec(builder_, 0);
    }
    else if (root_ == GO_INC_DEC_STATEMENT) {
      result_ = IncDecStatement(builder_, 0);
    }
    else if (root_ == GO_INDEX) {
      result_ = Index(builder_, 0);
    }
    else if (root_ == GO_INTERFACE_TYPE) {
      result_ = InterfaceType(builder_, 0);
    }
    else if (root_ == GO_INTERFACE_TYPE_NAME) {
      result_ = InterfaceTypeName(builder_, 0);
    }
    else if (root_ == GO_KEY) {
      result_ = Key(builder_, 0);
    }
    else if (root_ == GO_KEY_TYPE) {
      result_ = KeyType(builder_, 0);
    }
    else if (root_ == GO_LABEL) {
      result_ = Label(builder_, 0);
    }
    else if (root_ == GO_LABELED_STATEMENT) {
      result_ = LabeledStatement(builder_, 0);
    }
    else if (root_ == GO_LITERAL) {
      result_ = Literal(builder_, 0);
    }
    else if (root_ == GO_LITERAL_TYPE) {
      result_ = LiteralType(builder_, 0);
    }
    else if (root_ == GO_LITERAL_VALUE) {
      result_ = LiteralValue(builder_, 0);
    }
    else if (root_ == GO_MAP_TYPE) {
      result_ = MapType(builder_, 0);
    }
    else if (root_ == GO_METHOD_DECL) {
      result_ = MethodDecl(builder_, 0);
    }
    else if (root_ == GO_METHOD_EXPR) {
      result_ = MethodExpr(builder_, 0);
    }
    else if (root_ == GO_METHOD_NAME) {
      result_ = MethodName(builder_, 0);
    }
    else if (root_ == GO_METHOD_SPEC) {
      result_ = MethodSpec(builder_, 0);
    }
    else if (root_ == GO_OPERAND) {
      result_ = Operand(builder_, 0);
    }
    else if (root_ == GO_OPERAND_NAME) {
      result_ = OperandName(builder_, 0);
    }
    else if (root_ == GO_PACKAGE_CLAUSE) {
      result_ = PackageClause(builder_, 0);
    }
    else if (root_ == GO_PARAMETER_DECL) {
      result_ = ParameterDecl(builder_, 0);
    }
    else if (root_ == GO_PARAMETER_LIST) {
      result_ = ParameterList(builder_, 0);
    }
    else if (root_ == GO_PARAMETERS) {
      result_ = Parameters(builder_, 0);
    }
    else if (root_ == GO_POINTER_TYPE) {
      result_ = PointerType(builder_, 0);
    }
    else if (root_ == GO_PRIMARY_EXPR) {
      result_ = PrimaryExpr(builder_, 0);
    }
    else if (root_ == GO_QUALIFIED_IDENT) {
      result_ = QualifiedIdent(builder_, 0);
    }
    else if (root_ == GO_RANGE_CLAUSE) {
      result_ = RangeClause(builder_, 0);
    }
    else if (root_ == GO_RECEIVER) {
      result_ = Receiver(builder_, 0);
    }
    else if (root_ == GO_RECEIVER_TYPE) {
      result_ = ReceiverType(builder_, 0);
    }
    else if (root_ == GO_RECV_STATEMENT) {
      result_ = RecvStatement(builder_, 0);
    }
    else if (root_ == GO_RESULT) {
      result_ = Result(builder_, 0);
    }
    else if (root_ == GO_RETURN_STATEMENT) {
      result_ = ReturnStatement(builder_, 0);
    }
    else if (root_ == GO_SELECT_STATEMENT) {
      result_ = SelectStatement(builder_, 0);
    }
    else if (root_ == GO_SELECTOR) {
      result_ = Selector(builder_, 0);
    }
    else if (root_ == GO_SEND_STATEMENT) {
      result_ = SendStatement(builder_, 0);
    }
    else if (root_ == GO_SHORT_VAR_DECL) {
      result_ = ShortVarDecl(builder_, 0);
    }
    else if (root_ == GO_SIGNATURE) {
      result_ = Signature(builder_, 0);
    }
    else if (root_ == GO_SIMPLE_STATEMENT) {
      result_ = SimpleStatement(builder_, 0);
    }
    else if (root_ == GO_SLICE) {
      result_ = Slice(builder_, 0);
    }
    else if (root_ == GO_SLICE_TYPE) {
      result_ = SliceType(builder_, 0);
    }
    else if (root_ == GO_STATEMENT) {
      result_ = Statement(builder_, 0);
    }
    else if (root_ == GO_STATEMENT_LIST) {
      result_ = StatementList(builder_, 0);
    }
    else if (root_ == GO_STRUCT_TYPE) {
      result_ = StructType(builder_, 0);
    }
    else if (root_ == GO_SWITCH_STATEMENT) {
      result_ = SwitchStatement(builder_, 0);
    }
    else if (root_ == GO_TAG) {
      result_ = Tag(builder_, 0);
    }
    else if (root_ == GO_TOP_LEVEL_DECL) {
      result_ = TopLevelDecl(builder_, 0);
    }
    else if (root_ == GO_TYPE_ASSERTION) {
      result_ = TypeAssertion(builder_, 0);
    }
    else if (root_ == GO_TYPE_CASE_CLAUSE) {
      result_ = TypeCaseClause(builder_, 0);
    }
    else if (root_ == GO_TYPE_DECL) {
      result_ = TypeDecl(builder_, 0);
    }
    else if (root_ == GO_TYPE_LIST) {
      result_ = TypeList(builder_, 0);
    }
    else if (root_ == GO_TYPE_LIT) {
      result_ = TypeLit(builder_, 0);
    }
    else if (root_ == GO_TYPE_NAME) {
      result_ = TypeName(builder_, 0);
    }
    else if (root_ == GO_TYPE_SPEC) {
      result_ = TypeSpec(builder_, 0);
    }
    else if (root_ == GO_TYPE_SWITCH_CASE) {
      result_ = TypeSwitchCase(builder_, 0);
    }
    else if (root_ == GO_TYPE_SWITCH_GUARD) {
      result_ = TypeSwitchGuard(builder_, 0);
    }
    else if (root_ == GO_TYPE_SWITCH_STATEMENT) {
      result_ = TypeSwitchStatement(builder_, 0);
    }
    else if (root_ == GO_UNARY_EXPR) {
      result_ = UnaryExpr(builder_, 0);
    }
    else if (root_ == GO_VALUE) {
      result_ = Value(builder_, 0);
    }
    else if (root_ == GO_VAR_DECL) {
      result_ = VarDecl(builder_, 0);
    }
    else if (root_ == GO_VAR_SPEC) {
      result_ = VarSpec(builder_, 0);
    }
    else if (root_ == GO_ADD_OP) {
      result_ = add_op(builder_, 0);
    }
    else if (root_ == GO_ASSIGN_OP) {
      result_ = assign_op(builder_, 0);
    }
    else if (root_ == GO_BINARY_OP) {
      result_ = binary_op(builder_, 0);
    }
    else if (root_ == GO_DUMMY) {
      result_ = dummy(builder_, 0);
    }
    else if (root_ == GO_MUL_OP) {
      result_ = mul_op(builder_, 0);
    }
    else if (root_ == GO_REL_OP) {
      result_ = rel_op(builder_, 0);
    }
    else if (root_ == GO_UNARY_OP) {
      result_ = unary_op(builder_, 0);
    }
    else {
      result_ = parse_root_(root_, builder_, 0);
    }
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION);
    return builder_.getTreeBuilt();
  }

  protected boolean parse_root_(final IElementType root_, final PsiBuilder builder_, final int level_) {
    return SourceFile(builder_, level_ + 1);
  }

  /* ********************************************************** */
  // [ '*' ] TypeName
  public static boolean AnonymousField(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "AnonymousField")) return false;
    if (!nextTokenIs(builder_, "<anonymous field>", GO_MUL, GO_IDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<anonymous field>");
    result_ = AnonymousField_0(builder_, level_ + 1);
    result_ = result_ && TypeName(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_ANONYMOUS_FIELD, result_, false, null);
    return result_;
  }

  // [ '*' ]
  private static boolean AnonymousField_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "AnonymousField_0")) return false;
    consumeToken(builder_, GO_MUL);
    return true;
  }

  /* ********************************************************** */
  // ExpressionList [ '...' ]
  public static boolean ArgumentList(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ArgumentList")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<argument list>");
    result_ = ExpressionList(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && ArgumentList_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_ARGUMENT_LIST, result_, pinned_, null);
    return result_ || pinned_;
  }

  // [ '...' ]
  private static boolean ArgumentList_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ArgumentList_1")) return false;
    consumeToken(builder_, GO_TRIPLE_DOT);
    return true;
  }

  /* ********************************************************** */
  // Expression
  public static boolean ArrayLength(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ArrayLength")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<array length>");
    result_ = Expression(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_ARRAY_LENGTH, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // '[' ArrayLength ']' ElementType
  public static boolean ArrayType(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ArrayType")) return false;
    if (!nextTokenIs(builder_, GO_LBRACK)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_LBRACK);
    result_ = result_ && ArrayLength(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_RBRACK);
    result_ = result_ && ElementType(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_ARRAY_TYPE, result_);
    return result_;
  }

  /* ********************************************************** */
  // ExpressionList assign_op ExpressionList
  public static boolean Assignment(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Assignment")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<assignment>");
    result_ = ExpressionList(builder_, level_ + 1);
    result_ = result_ && assign_op(builder_, level_ + 1);
    result_ = result_ && ExpressionList(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_ASSIGNMENT, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // GoType
  public static boolean BaseType(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "BaseType")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<base type>");
    result_ = GoType(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_BASE_TYPE, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // identifier
  public static boolean BaseTypeName(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "BaseTypeName")) return false;
    if (!nextTokenIs(builder_, GO_IDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_IDENTIFIER);
    exit_section_(builder_, marker_, GO_BASE_TYPE_NAME, result_);
    return result_;
  }

  /* ********************************************************** */
  // int
  //   | float
  //   | float_i
  //   | decimal_i
  //   | imaginary
  //   | rune
  //   | string
  public static boolean BasicLit(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "BasicLit")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<basic lit>");
    result_ = consumeToken(builder_, GO_INT);
    if (!result_) result_ = consumeToken(builder_, GO_FLOAT);
    if (!result_) result_ = consumeToken(builder_, GO_FLOAT_I);
    if (!result_) result_ = consumeToken(builder_, GO_DECIMAL_I);
    if (!result_) result_ = consumeToken(builder_, GO_IMAGINARY);
    if (!result_) result_ = consumeToken(builder_, GO_RUNE);
    if (!result_) result_ = consumeToken(builder_, GO_STRING);
    exit_section_(builder_, level_, marker_, GO_BASIC_LIT, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // '{' StatementList '}'
  public static boolean Block(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Block")) return false;
    if (!nextTokenIs(builder_, GO_LBRACE)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_LBRACE);
    result_ = result_ && StatementList(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_RBRACE);
    exit_section_(builder_, marker_, GO_BLOCK, result_);
    return result_;
  }

  /* ********************************************************** */
  // break Label?
  public static boolean BreakStatement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "BreakStatement")) return false;
    if (!nextTokenIs(builder_, GO_BREAK)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_BREAK);
    result_ = result_ && BreakStatement_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_BREAK_STATEMENT, result_);
    return result_;
  }

  // Label?
  private static boolean BreakStatement_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "BreakStatement_1")) return false;
    Label(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // GoType [ ',' ArgumentList ] | ArgumentList
  public static boolean BuiltinArgs(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "BuiltinArgs")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<builtin args>");
    result_ = BuiltinArgs_0(builder_, level_ + 1);
    if (!result_) result_ = ArgumentList(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_BUILTIN_ARGS, result_, false, null);
    return result_;
  }

  // GoType [ ',' ArgumentList ]
  private static boolean BuiltinArgs_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "BuiltinArgs_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = GoType(builder_, level_ + 1);
    result_ = result_ && BuiltinArgs_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // [ ',' ArgumentList ]
  private static boolean BuiltinArgs_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "BuiltinArgs_0_1")) return false;
    BuiltinArgs_0_1_0(builder_, level_ + 1);
    return true;
  }

  // ',' ArgumentList
  private static boolean BuiltinArgs_0_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "BuiltinArgs_0_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_COMMA);
    result_ = result_ && ArgumentList(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // identifier '(' [ BuiltinArgs [ ',' ] ] ')'
  public static boolean BuiltinCall(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "BuiltinCall")) return false;
    if (!nextTokenIs(builder_, GO_IDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_IDENTIFIER);
    result_ = result_ && consumeToken(builder_, GO_LPAREN);
    result_ = result_ && BuiltinCall_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_RPAREN);
    exit_section_(builder_, marker_, GO_BUILTIN_CALL, result_);
    return result_;
  }

  // [ BuiltinArgs [ ',' ] ]
  private static boolean BuiltinCall_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "BuiltinCall_2")) return false;
    BuiltinCall_2_0(builder_, level_ + 1);
    return true;
  }

  // BuiltinArgs [ ',' ]
  private static boolean BuiltinCall_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "BuiltinCall_2_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = BuiltinArgs(builder_, level_ + 1);
    result_ = result_ && BuiltinCall_2_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // [ ',' ]
  private static boolean BuiltinCall_2_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "BuiltinCall_2_0_1")) return false;
    consumeToken(builder_, GO_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '(' [ ArgumentList [ ',' ] ] ')'
  public static boolean Call(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Call")) return false;
    if (!nextTokenIs(builder_, GO_LPAREN)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_LPAREN);
    result_ = result_ && Call_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_RPAREN);
    exit_section_(builder_, marker_, GO_CALL, result_);
    return result_;
  }

  // [ ArgumentList [ ',' ] ]
  private static boolean Call_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Call_1")) return false;
    Call_1_0(builder_, level_ + 1);
    return true;
  }

  // ArgumentList [ ',' ]
  private static boolean Call_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Call_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = ArgumentList(builder_, level_ + 1);
    result_ = result_ && Call_1_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // [ ',' ]
  private static boolean Call_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Call_1_0_1")) return false;
    consumeToken(builder_, GO_COMMA);
    return true;
  }

  /* ********************************************************** */
  // Expression
  public static boolean Channel(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Channel")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<channel>");
    result_ = Expression(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_CHANNEL, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // ( chan [ '<-' ] | '<-' chan ) ElementType
  public static boolean ChannelType(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ChannelType")) return false;
    if (!nextTokenIs(builder_, "<channel type>", GO_SEND_CHANNEL, GO_CHAN)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<channel type>");
    result_ = ChannelType_0(builder_, level_ + 1);
    result_ = result_ && ElementType(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_CHANNEL_TYPE, result_, false, null);
    return result_;
  }

  // chan [ '<-' ] | '<-' chan
  private static boolean ChannelType_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ChannelType_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = ChannelType_0_0(builder_, level_ + 1);
    if (!result_) result_ = ChannelType_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // chan [ '<-' ]
  private static boolean ChannelType_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ChannelType_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_CHAN);
    result_ = result_ && ChannelType_0_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // [ '<-' ]
  private static boolean ChannelType_0_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ChannelType_0_0_1")) return false;
    consumeToken(builder_, GO_SEND_CHANNEL);
    return true;
  }

  // '<-' chan
  private static boolean ChannelType_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ChannelType_0_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_SEND_CHANNEL);
    result_ = result_ && consumeToken(builder_, GO_CHAN);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // case ( SendStatement | RecvStatement ) | default
  public static boolean CommCase(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "CommCase")) return false;
    if (!nextTokenIs(builder_, "<comm case>", GO_CASE, GO_DEFAULT)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<comm case>");
    result_ = CommCase_0(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, GO_DEFAULT);
    exit_section_(builder_, level_, marker_, GO_COMM_CASE, result_, false, null);
    return result_;
  }

  // case ( SendStatement | RecvStatement )
  private static boolean CommCase_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "CommCase_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_CASE);
    result_ = result_ && CommCase_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // SendStatement | RecvStatement
  private static boolean CommCase_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "CommCase_0_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = SendStatement(builder_, level_ + 1);
    if (!result_) result_ = RecvStatement(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // CommCase ':' StatementList
  public static boolean CommClause(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "CommClause")) return false;
    if (!nextTokenIs(builder_, "<comm clause>", GO_CASE, GO_DEFAULT)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<comm clause>");
    result_ = CommCase(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_COLON);
    result_ = result_ && StatementList(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_COMM_CLAUSE, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // LiteralType LiteralValue
  public static boolean CompositeLit(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "CompositeLit")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<composite lit>");
    result_ = LiteralType(builder_, level_ + 1);
    result_ = result_ && LiteralValue(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_COMPOSITE_LIT, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // const ( ConstSpec | '(' ( ConstSpec semi )* ')' )
  public static boolean ConstDecl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ConstDecl")) return false;
    if (!nextTokenIs(builder_, GO_CONST)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_CONST);
    result_ = result_ && ConstDecl_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_CONST_DECL, result_);
    return result_;
  }

  // ConstSpec | '(' ( ConstSpec semi )* ')'
  private static boolean ConstDecl_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ConstDecl_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = ConstSpec(builder_, level_ + 1);
    if (!result_) result_ = ConstDecl_1_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // '(' ( ConstSpec semi )* ')'
  private static boolean ConstDecl_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ConstDecl_1_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_LPAREN);
    result_ = result_ && ConstDecl_1_1_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_RPAREN);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // ( ConstSpec semi )*
  private static boolean ConstDecl_1_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ConstDecl_1_1_1")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!ConstDecl_1_1_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "ConstDecl_1_1_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // ConstSpec semi
  private static boolean ConstDecl_1_1_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ConstDecl_1_1_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = ConstSpec(builder_, level_ + 1);
    result_ = result_ && semi(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // IdentifierList [ [ GoType ] '=' ExpressionList ]
  public static boolean ConstSpec(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ConstSpec")) return false;
    if (!nextTokenIs(builder_, GO_IDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = IdentifierList(builder_, level_ + 1);
    result_ = result_ && ConstSpec_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_CONST_SPEC, result_);
    return result_;
  }

  // [ [ GoType ] '=' ExpressionList ]
  private static boolean ConstSpec_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ConstSpec_1")) return false;
    ConstSpec_1_0(builder_, level_ + 1);
    return true;
  }

  // [ GoType ] '=' ExpressionList
  private static boolean ConstSpec_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ConstSpec_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = ConstSpec_1_0_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_ASSIGN);
    result_ = result_ && ExpressionList(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // [ GoType ]
  private static boolean ConstSpec_1_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ConstSpec_1_0_0")) return false;
    GoType(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // continue Label?
  public static boolean ContinueStatement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ContinueStatement")) return false;
    if (!nextTokenIs(builder_, GO_CONTINUE)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_CONTINUE);
    result_ = result_ && ContinueStatement_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_CONTINUE_STATEMENT, result_);
    return result_;
  }

  // Label?
  private static boolean ContinueStatement_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ContinueStatement_1")) return false;
    Label(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // GoType '(' Expression [ ',' ] ')'
  public static boolean Conversion(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Conversion")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<conversion>");
    result_ = GoType(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_LPAREN);
    result_ = result_ && Expression(builder_, level_ + 1);
    result_ = result_ && Conversion_3(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_RPAREN);
    exit_section_(builder_, level_, marker_, GO_CONVERSION, result_, false, null);
    return result_;
  }

  // [ ',' ]
  private static boolean Conversion_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Conversion_3")) return false;
    consumeToken(builder_, GO_COMMA);
    return true;
  }

  /* ********************************************************** */
  // ConstDecl | TypeDecl | VarDecl
  public static boolean DeclarationStatement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "DeclarationStatement")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<declaration statement>");
    result_ = ConstDecl(builder_, level_ + 1);
    if (!result_) result_ = TypeDecl(builder_, level_ + 1);
    if (!result_) result_ = VarDecl(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_DECLARATION_STATEMENT, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // defer Expression
  public static boolean DeferStatement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "DeferStatement")) return false;
    if (!nextTokenIs(builder_, GO_DEFER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_DEFER);
    result_ = result_ && Expression(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_DEFER_STATEMENT, result_);
    return result_;
  }

  /* ********************************************************** */
  // [ Key ':' ] Value
  public static boolean Element(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Element")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<element>");
    result_ = Element_0(builder_, level_ + 1);
    result_ = result_ && Value(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_ELEMENT, result_, false, null);
    return result_;
  }

  // [ Key ':' ]
  private static boolean Element_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Element_0")) return false;
    Element_0_0(builder_, level_ + 1);
    return true;
  }

  // Key ':'
  private static boolean Element_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Element_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = Key(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_COLON);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // Expression
  public static boolean ElementIndex(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ElementIndex")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<element index>");
    result_ = Expression(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_ELEMENT_INDEX, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // Element ( ',' Element )*
  public static boolean ElementList(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ElementList")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<element list>");
    result_ = Element(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && ElementList_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_ELEMENT_LIST, result_, pinned_, null);
    return result_ || pinned_;
  }

  // ( ',' Element )*
  private static boolean ElementList_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ElementList_1")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!ElementList_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "ElementList_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // ',' Element
  private static boolean ElementList_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ElementList_1_0")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, GO_COMMA);
    pinned_ = result_; // pin = 1
    result_ = result_ && Element(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // GoType
  public static boolean ElementType(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ElementType")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<element type>");
    result_ = GoType(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_ELEMENT_TYPE, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // ExprSwitchCase ':' StatementList
  public static boolean ExprCaseClause(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ExprCaseClause")) return false;
    if (!nextTokenIs(builder_, "<expr case clause>", GO_CASE, GO_DEFAULT)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<expr case clause>");
    result_ = ExprSwitchCase(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_COLON);
    result_ = result_ && StatementList(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_EXPR_CASE_CLAUSE, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // case ExpressionList | default
  public static boolean ExprSwitchCase(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ExprSwitchCase")) return false;
    if (!nextTokenIs(builder_, "<expr switch case>", GO_CASE, GO_DEFAULT)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<expr switch case>");
    result_ = ExprSwitchCase_0(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, GO_DEFAULT);
    exit_section_(builder_, level_, marker_, GO_EXPR_SWITCH_CASE, result_, false, null);
    return result_;
  }

  // case ExpressionList
  private static boolean ExprSwitchCase_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ExprSwitchCase_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_CASE);
    result_ = result_ && ExpressionList(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // switch [ SimpleStatement semi ] [ Expression ] '{' ( ExprCaseClause )* '}'
  public static boolean ExprSwitchStatement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ExprSwitchStatement")) return false;
    if (!nextTokenIs(builder_, GO_SWITCH)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_SWITCH);
    result_ = result_ && ExprSwitchStatement_1(builder_, level_ + 1);
    result_ = result_ && ExprSwitchStatement_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_LBRACE);
    result_ = result_ && ExprSwitchStatement_4(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_RBRACE);
    exit_section_(builder_, marker_, GO_EXPR_SWITCH_STATEMENT, result_);
    return result_;
  }

  // [ SimpleStatement semi ]
  private static boolean ExprSwitchStatement_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ExprSwitchStatement_1")) return false;
    ExprSwitchStatement_1_0(builder_, level_ + 1);
    return true;
  }

  // SimpleStatement semi
  private static boolean ExprSwitchStatement_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ExprSwitchStatement_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = SimpleStatement(builder_, level_ + 1);
    result_ = result_ && semi(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // [ Expression ]
  private static boolean ExprSwitchStatement_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ExprSwitchStatement_2")) return false;
    Expression(builder_, level_ + 1);
    return true;
  }

  // ( ExprCaseClause )*
  private static boolean ExprSwitchStatement_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ExprSwitchStatement_4")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!ExprSwitchStatement_4_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "ExprSwitchStatement_4", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // ( ExprCaseClause )
  private static boolean ExprSwitchStatement_4_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ExprSwitchStatement_4_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = ExprCaseClause(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // UnaryExpr | Expression binary_op UnaryExpr
  public static boolean Expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Expression")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<expression>");
    result_ = UnaryExpr(builder_, level_ + 1);
    if (!result_) result_ = Expression_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_EXPRESSION, result_, false, null);
    return result_;
  }

  // Expression binary_op UnaryExpr
  private static boolean Expression_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Expression_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = Expression(builder_, level_ + 1);
    result_ = result_ && binary_op(builder_, level_ + 1);
    result_ = result_ && UnaryExpr(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // Expression ( ',' Expression )*
  public static boolean ExpressionList(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ExpressionList")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<expression list>");
    result_ = Expression(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && ExpressionList_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_EXPRESSION_LIST, result_, pinned_, null);
    return result_ || pinned_;
  }

  // ( ',' Expression )*
  private static boolean ExpressionList_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ExpressionList_1")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!ExpressionList_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "ExpressionList_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // ',' Expression
  private static boolean ExpressionList_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ExpressionList_1_0")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, GO_COMMA);
    pinned_ = result_; // pin = 1
    result_ = result_ && Expression(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // Expression
  public static boolean ExpressionStatement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ExpressionStatement")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<expression statement>");
    result_ = Expression(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_EXPRESSION_STATEMENT, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // fallthrough
  public static boolean FallthroughStatement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "FallthroughStatement")) return false;
    if (!nextTokenIs(builder_, GO_FALLTHROUGH)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_FALLTHROUGH);
    exit_section_(builder_, marker_, GO_FALLTHROUGH_STATEMENT, result_);
    return result_;
  }

  /* ********************************************************** */
  // (IdentifierList GoType | AnonymousField) [ Tag ]
  public static boolean FieldDecl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "FieldDecl")) return false;
    if (!nextTokenIs(builder_, "<field decl>", GO_MUL, GO_IDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<field decl>");
    result_ = FieldDecl_0(builder_, level_ + 1);
    result_ = result_ && FieldDecl_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_FIELD_DECL, result_, false, null);
    return result_;
  }

  // IdentifierList GoType | AnonymousField
  private static boolean FieldDecl_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "FieldDecl_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = FieldDecl_0_0(builder_, level_ + 1);
    if (!result_) result_ = AnonymousField(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // IdentifierList GoType
  private static boolean FieldDecl_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "FieldDecl_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = IdentifierList(builder_, level_ + 1);
    result_ = result_ && GoType(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // [ Tag ]
  private static boolean FieldDecl_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "FieldDecl_1")) return false;
    Tag(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // identifier
  public static boolean FieldName(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "FieldName")) return false;
    if (!nextTokenIs(builder_, GO_IDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_IDENTIFIER);
    exit_section_(builder_, marker_, GO_FIELD_NAME, result_);
    return result_;
  }

  /* ********************************************************** */
  // SimpleStatement? semi Expression? semi SimpleStatement?
  public static boolean ForClause(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ForClause")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<for clause>");
    result_ = ForClause_0(builder_, level_ + 1);
    result_ = result_ && semi(builder_, level_ + 1);
    result_ = result_ && ForClause_2(builder_, level_ + 1);
    result_ = result_ && semi(builder_, level_ + 1);
    result_ = result_ && ForClause_4(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_FOR_CLAUSE, result_, false, null);
    return result_;
  }

  // SimpleStatement?
  private static boolean ForClause_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ForClause_0")) return false;
    SimpleStatement(builder_, level_ + 1);
    return true;
  }

  // Expression?
  private static boolean ForClause_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ForClause_2")) return false;
    Expression(builder_, level_ + 1);
    return true;
  }

  // SimpleStatement?
  private static boolean ForClause_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ForClause_4")) return false;
    SimpleStatement(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // for [ Expression | RangeClause | ForClause ] Block
  public static boolean ForStatement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ForStatement")) return false;
    if (!nextTokenIs(builder_, GO_FOR)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_FOR);
    result_ = result_ && ForStatement_1(builder_, level_ + 1);
    result_ = result_ && Block(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_FOR_STATEMENT, result_);
    return result_;
  }

  // [ Expression | RangeClause | ForClause ]
  private static boolean ForStatement_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ForStatement_1")) return false;
    ForStatement_1_0(builder_, level_ + 1);
    return true;
  }

  // Expression | RangeClause | ForClause
  private static boolean ForStatement_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ForStatement_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = Expression(builder_, level_ + 1);
    if (!result_) result_ = RangeClause(builder_, level_ + 1);
    if (!result_) result_ = ForClause(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // Signature FunctionBody
  public static boolean Function(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Function")) return false;
    if (!nextTokenIs(builder_, GO_LPAREN)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = Signature(builder_, level_ + 1);
    result_ = result_ && FunctionBody(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_FUNCTION, result_);
    return result_;
  }

  /* ********************************************************** */
  // Block
  public static boolean FunctionBody(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "FunctionBody")) return false;
    if (!nextTokenIs(builder_, GO_LBRACE)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = Block(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_FUNCTION_BODY, result_);
    return result_;
  }

  /* ********************************************************** */
  // func FunctionName ( Function | Signature )
  public static boolean FunctionDecl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "FunctionDecl")) return false;
    if (!nextTokenIs(builder_, GO_FUNC)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_FUNC);
    result_ = result_ && FunctionName(builder_, level_ + 1);
    result_ = result_ && FunctionDecl_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_FUNCTION_DECL, result_);
    return result_;
  }

  // Function | Signature
  private static boolean FunctionDecl_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "FunctionDecl_2")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = Function(builder_, level_ + 1);
    if (!result_) result_ = Signature(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // func Function
  public static boolean FunctionLit(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "FunctionLit")) return false;
    if (!nextTokenIs(builder_, GO_FUNC)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_FUNC);
    result_ = result_ && Function(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_FUNCTION_LIT, result_);
    return result_;
  }

  /* ********************************************************** */
  // identifier
  public static boolean FunctionName(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "FunctionName")) return false;
    if (!nextTokenIs(builder_, GO_IDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_IDENTIFIER);
    exit_section_(builder_, marker_, GO_FUNCTION_NAME, result_);
    return result_;
  }

  /* ********************************************************** */
  // func Signature
  public static boolean FunctionType(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "FunctionType")) return false;
    if (!nextTokenIs(builder_, GO_FUNC)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_FUNC);
    result_ = result_ && Signature(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_FUNCTION_TYPE, result_);
    return result_;
  }

  /* ********************************************************** */
  // go Expression
  public static boolean GoStatement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "GoStatement")) return false;
    if (!nextTokenIs(builder_, GO_GO)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_GO);
    result_ = result_ && Expression(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_GO_STATEMENT, result_);
    return result_;
  }

  /* ********************************************************** */
  // TypeName | TypeLit | '(' GoType ')'
  public static boolean GoType(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "GoType")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<go type>");
    result_ = TypeName(builder_, level_ + 1);
    if (!result_) result_ = TypeLit(builder_, level_ + 1);
    if (!result_) result_ = GoType_2(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_GO_TYPE, result_, false, null);
    return result_;
  }

  // '(' GoType ')'
  private static boolean GoType_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "GoType_2")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_LPAREN);
    result_ = result_ && GoType(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_RPAREN);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // goto Label
  public static boolean GotoStatement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "GotoStatement")) return false;
    if (!nextTokenIs(builder_, GO_GOTO)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_GOTO);
    result_ = result_ && Label(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_GOTO_STATEMENT, result_);
    return result_;
  }

  /* ********************************************************** */
  // identifier ( ',' identifier )*
  public static boolean IdentifierList(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "IdentifierList")) return false;
    if (!nextTokenIs(builder_, GO_IDENTIFIER)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, GO_IDENTIFIER);
    pinned_ = result_; // pin = 1
    result_ = result_ && IdentifierList_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_IDENTIFIER_LIST, result_, pinned_, null);
    return result_ || pinned_;
  }

  // ( ',' identifier )*
  private static boolean IdentifierList_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "IdentifierList_1")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!IdentifierList_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "IdentifierList_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // ',' identifier
  private static boolean IdentifierList_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "IdentifierList_1_0")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, GO_COMMA);
    pinned_ = result_; // pin = 1
    result_ = result_ && consumeToken(builder_, GO_IDENTIFIER);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // if [ SimpleStatement semi ] Expression Block [ else ( IfStatement | Block ) ]
  public static boolean IfStatement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "IfStatement")) return false;
    if (!nextTokenIs(builder_, GO_IF)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_IF);
    result_ = result_ && IfStatement_1(builder_, level_ + 1);
    result_ = result_ && Expression(builder_, level_ + 1);
    result_ = result_ && Block(builder_, level_ + 1);
    result_ = result_ && IfStatement_4(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_IF_STATEMENT, result_);
    return result_;
  }

  // [ SimpleStatement semi ]
  private static boolean IfStatement_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "IfStatement_1")) return false;
    IfStatement_1_0(builder_, level_ + 1);
    return true;
  }

  // SimpleStatement semi
  private static boolean IfStatement_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "IfStatement_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = SimpleStatement(builder_, level_ + 1);
    result_ = result_ && semi(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // [ else ( IfStatement | Block ) ]
  private static boolean IfStatement_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "IfStatement_4")) return false;
    IfStatement_4_0(builder_, level_ + 1);
    return true;
  }

  // else ( IfStatement | Block )
  private static boolean IfStatement_4_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "IfStatement_4_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_ELSE);
    result_ = result_ && IfStatement_4_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // IfStatement | Block
  private static boolean IfStatement_4_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "IfStatement_4_0_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = IfStatement(builder_, level_ + 1);
    if (!result_) result_ = Block(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // import ( ImportSpec | '(' ( ImportSpec semi )* ')' )
  public static boolean ImportDecl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ImportDecl")) return false;
    if (!nextTokenIs(builder_, GO_IMPORT)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, GO_IMPORT);
    pinned_ = result_; // pin = 1
    result_ = result_ && ImportDecl_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_IMPORT_DECL, result_, pinned_, null);
    return result_ || pinned_;
  }

  // ImportSpec | '(' ( ImportSpec semi )* ')'
  private static boolean ImportDecl_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ImportDecl_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = ImportSpec(builder_, level_ + 1);
    if (!result_) result_ = ImportDecl_1_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // '(' ( ImportSpec semi )* ')'
  private static boolean ImportDecl_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ImportDecl_1_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_LPAREN);
    result_ = result_ && ImportDecl_1_1_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_RPAREN);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // ( ImportSpec semi )*
  private static boolean ImportDecl_1_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ImportDecl_1_1_1")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!ImportDecl_1_1_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "ImportDecl_1_1_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // ImportSpec semi
  private static boolean ImportDecl_1_1_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ImportDecl_1_1_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = ImportSpec(builder_, level_ + 1);
    result_ = result_ && semi(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // string
  public static boolean ImportPath(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ImportPath")) return false;
    if (!nextTokenIs(builder_, GO_STRING)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_STRING);
    exit_section_(builder_, marker_, GO_IMPORT_PATH, result_);
    return result_;
  }

  /* ********************************************************** */
  // [ '.' | PackageName ] ImportPath
  public static boolean ImportSpec(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ImportSpec")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<import spec>");
    result_ = ImportSpec_0(builder_, level_ + 1);
    result_ = result_ && ImportPath(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_IMPORT_SPEC, result_, false, null);
    return result_;
  }

  // [ '.' | PackageName ]
  private static boolean ImportSpec_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ImportSpec_0")) return false;
    ImportSpec_0_0(builder_, level_ + 1);
    return true;
  }

  // '.' | PackageName
  private static boolean ImportSpec_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ImportSpec_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_DOT);
    if (!result_) result_ = PackageName(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // Expression ( '++' | '--' )
  public static boolean IncDecStatement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "IncDecStatement")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<inc dec statement>");
    result_ = Expression(builder_, level_ + 1);
    result_ = result_ && IncDecStatement_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_INC_DEC_STATEMENT, result_, false, null);
    return result_;
  }

  // '++' | '--'
  private static boolean IncDecStatement_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "IncDecStatement_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_PLUS_PLUS);
    if (!result_) result_ = consumeToken(builder_, GO_MINUS_MINUS);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // '[' Expression ']'
  public static boolean Index(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Index")) return false;
    if (!nextTokenIs(builder_, GO_LBRACK)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_LBRACK);
    result_ = result_ && Expression(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_RBRACK);
    exit_section_(builder_, marker_, GO_INDEX, result_);
    return result_;
  }

  /* ********************************************************** */
  // interface '{' ( MethodSpec semi )* '}'
  public static boolean InterfaceType(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "InterfaceType")) return false;
    if (!nextTokenIs(builder_, GO_INTERFACE)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_INTERFACE);
    result_ = result_ && consumeToken(builder_, GO_LBRACE);
    result_ = result_ && InterfaceType_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_RBRACE);
    exit_section_(builder_, marker_, GO_INTERFACE_TYPE, result_);
    return result_;
  }

  // ( MethodSpec semi )*
  private static boolean InterfaceType_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "InterfaceType_2")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!InterfaceType_2_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "InterfaceType_2", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // MethodSpec semi
  private static boolean InterfaceType_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "InterfaceType_2_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = MethodSpec(builder_, level_ + 1);
    result_ = result_ && semi(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // TypeName
  public static boolean InterfaceTypeName(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "InterfaceTypeName")) return false;
    if (!nextTokenIs(builder_, GO_IDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = TypeName(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_INTERFACE_TYPE_NAME, result_);
    return result_;
  }

  /* ********************************************************** */
  // FieldName | ElementIndex
  public static boolean Key(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Key")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<key>");
    result_ = FieldName(builder_, level_ + 1);
    if (!result_) result_ = ElementIndex(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_KEY, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // GoType
  public static boolean KeyType(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "KeyType")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<key type>");
    result_ = GoType(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_KEY_TYPE, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // identifier
  public static boolean Label(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Label")) return false;
    if (!nextTokenIs(builder_, GO_IDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_IDENTIFIER);
    exit_section_(builder_, marker_, GO_LABEL, result_);
    return result_;
  }

  /* ********************************************************** */
  // Label ':' Statement
  public static boolean LabeledStatement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "LabeledStatement")) return false;
    if (!nextTokenIs(builder_, GO_IDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = Label(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_COLON);
    result_ = result_ && Statement(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_LABELED_STATEMENT, result_);
    return result_;
  }

  /* ********************************************************** */
  // BasicLit | CompositeLit | FunctionLit
  public static boolean Literal(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Literal")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<literal>");
    result_ = BasicLit(builder_, level_ + 1);
    if (!result_) result_ = CompositeLit(builder_, level_ + 1);
    if (!result_) result_ = FunctionLit(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_LITERAL, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // StructType
  //   | ArrayType
  //   | '[' '...' ']' ElementType
  //   | SliceType
  //   | MapType
  //   | TypeName
  public static boolean LiteralType(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "LiteralType")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<literal type>");
    result_ = StructType(builder_, level_ + 1);
    if (!result_) result_ = ArrayType(builder_, level_ + 1);
    if (!result_) result_ = LiteralType_2(builder_, level_ + 1);
    if (!result_) result_ = SliceType(builder_, level_ + 1);
    if (!result_) result_ = MapType(builder_, level_ + 1);
    if (!result_) result_ = TypeName(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_LITERAL_TYPE, result_, false, null);
    return result_;
  }

  // '[' '...' ']' ElementType
  private static boolean LiteralType_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "LiteralType_2")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_LBRACK);
    result_ = result_ && consumeToken(builder_, GO_TRIPLE_DOT);
    result_ = result_ && consumeToken(builder_, GO_RBRACK);
    result_ = result_ && ElementType(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // '{' [ ElementList [ ',' ] ] '}'
  public static boolean LiteralValue(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "LiteralValue")) return false;
    if (!nextTokenIs(builder_, GO_LBRACE)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_LBRACE);
    result_ = result_ && LiteralValue_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_RBRACE);
    exit_section_(builder_, marker_, GO_LITERAL_VALUE, result_);
    return result_;
  }

  // [ ElementList [ ',' ] ]
  private static boolean LiteralValue_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "LiteralValue_1")) return false;
    LiteralValue_1_0(builder_, level_ + 1);
    return true;
  }

  // ElementList [ ',' ]
  private static boolean LiteralValue_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "LiteralValue_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = ElementList(builder_, level_ + 1);
    result_ = result_ && LiteralValue_1_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // [ ',' ]
  private static boolean LiteralValue_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "LiteralValue_1_0_1")) return false;
    consumeToken(builder_, GO_COMMA);
    return true;
  }

  /* ********************************************************** */
  // map '[' KeyType ']' ElementType
  public static boolean MapType(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "MapType")) return false;
    if (!nextTokenIs(builder_, GO_MAP)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_MAP);
    result_ = result_ && consumeToken(builder_, GO_LBRACK);
    result_ = result_ && KeyType(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_RBRACK);
    result_ = result_ && ElementType(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_MAP_TYPE, result_);
    return result_;
  }

  /* ********************************************************** */
  // func Receiver MethodName ( Function | Signature )
  public static boolean MethodDecl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "MethodDecl")) return false;
    if (!nextTokenIs(builder_, GO_FUNC)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_FUNC);
    result_ = result_ && Receiver(builder_, level_ + 1);
    result_ = result_ && MethodName(builder_, level_ + 1);
    result_ = result_ && MethodDecl_3(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_METHOD_DECL, result_);
    return result_;
  }

  // Function | Signature
  private static boolean MethodDecl_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "MethodDecl_3")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = Function(builder_, level_ + 1);
    if (!result_) result_ = Signature(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // ReceiverType '.' MethodName
  public static boolean MethodExpr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "MethodExpr")) return false;
    if (!nextTokenIs(builder_, "<method expr>", GO_LPAREN, GO_IDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<method expr>");
    result_ = ReceiverType(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_DOT);
    result_ = result_ && MethodName(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_METHOD_EXPR, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // identifier
  public static boolean MethodName(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "MethodName")) return false;
    if (!nextTokenIs(builder_, GO_IDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_IDENTIFIER);
    exit_section_(builder_, marker_, GO_METHOD_NAME, result_);
    return result_;
  }

  /* ********************************************************** */
  // MethodName Signature | InterfaceTypeName
  public static boolean MethodSpec(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "MethodSpec")) return false;
    if (!nextTokenIs(builder_, GO_IDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = MethodSpec_0(builder_, level_ + 1);
    if (!result_) result_ = InterfaceTypeName(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_METHOD_SPEC, result_);
    return result_;
  }

  // MethodName Signature
  private static boolean MethodSpec_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "MethodSpec_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = MethodName(builder_, level_ + 1);
    result_ = result_ && Signature(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // Literal | OperandName | MethodExpr | '(' Expression ')'
  public static boolean Operand(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Operand")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<operand>");
    result_ = Literal(builder_, level_ + 1);
    if (!result_) result_ = OperandName(builder_, level_ + 1);
    if (!result_) result_ = MethodExpr(builder_, level_ + 1);
    if (!result_) result_ = Operand_3(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_OPERAND, result_, false, null);
    return result_;
  }

  // '(' Expression ')'
  private static boolean Operand_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Operand_3")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_LPAREN);
    result_ = result_ && Expression(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_RPAREN);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // identifier | QualifiedIdent
  public static boolean OperandName(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "OperandName")) return false;
    if (!nextTokenIs(builder_, GO_IDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_IDENTIFIER);
    if (!result_) result_ = QualifiedIdent(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_OPERAND_NAME, result_);
    return result_;
  }

  /* ********************************************************** */
  // package PackageName
  public static boolean PackageClause(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "PackageClause")) return false;
    if (!nextTokenIs(builder_, GO_PACKAGE)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, GO_PACKAGE);
    pinned_ = result_; // pin = 1
    result_ = result_ && PackageName(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_PACKAGE_CLAUSE, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // identifier
  static boolean PackageName(PsiBuilder builder_, int level_) {
    return consumeToken(builder_, GO_IDENTIFIER);
  }

  /* ********************************************************** */
  // [ IdentifierList ] [ '...' ] GoType
  public static boolean ParameterDecl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ParameterDecl")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<parameter decl>");
    result_ = ParameterDecl_0(builder_, level_ + 1);
    result_ = result_ && ParameterDecl_1(builder_, level_ + 1);
    result_ = result_ && GoType(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_PARAMETER_DECL, result_, false, null);
    return result_;
  }

  // [ IdentifierList ]
  private static boolean ParameterDecl_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ParameterDecl_0")) return false;
    IdentifierList(builder_, level_ + 1);
    return true;
  }

  // [ '...' ]
  private static boolean ParameterDecl_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ParameterDecl_1")) return false;
    consumeToken(builder_, GO_TRIPLE_DOT);
    return true;
  }

  /* ********************************************************** */
  // ParameterDecl ( ',' ParameterDecl )*
  public static boolean ParameterList(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ParameterList")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<parameter list>");
    result_ = ParameterDecl(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && ParameterList_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_PARAMETER_LIST, result_, pinned_, null);
    return result_ || pinned_;
  }

  // ( ',' ParameterDecl )*
  private static boolean ParameterList_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ParameterList_1")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!ParameterList_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "ParameterList_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // ',' ParameterDecl
  private static boolean ParameterList_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ParameterList_1_0")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, GO_COMMA);
    pinned_ = result_; // pin = 1
    result_ = result_ && ParameterDecl(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // '(' [ ParameterList [ ',' ] ] ')'
  public static boolean Parameters(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Parameters")) return false;
    if (!nextTokenIs(builder_, GO_LPAREN)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_LPAREN);
    result_ = result_ && Parameters_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_RPAREN);
    exit_section_(builder_, marker_, GO_PARAMETERS, result_);
    return result_;
  }

  // [ ParameterList [ ',' ] ]
  private static boolean Parameters_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Parameters_1")) return false;
    Parameters_1_0(builder_, level_ + 1);
    return true;
  }

  // ParameterList [ ',' ]
  private static boolean Parameters_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Parameters_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = ParameterList(builder_, level_ + 1);
    result_ = result_ && Parameters_1_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // [ ',' ]
  private static boolean Parameters_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Parameters_1_0_1")) return false;
    consumeToken(builder_, GO_COMMA);
    return true;
  }

  /* ********************************************************** */
  // '*' BaseType
  public static boolean PointerType(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "PointerType")) return false;
    if (!nextTokenIs(builder_, GO_MUL)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_MUL);
    result_ = result_ && BaseType(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_POINTER_TYPE, result_);
    return result_;
  }

  /* ********************************************************** */
  // Operand |
  // 	Conversion |
  // 	BuiltinCall |
  // 	PrimaryExpr (Selector | Index | Slice | TypeAssertion | Call)
  public static boolean PrimaryExpr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "PrimaryExpr")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<primary expr>");
    result_ = Operand(builder_, level_ + 1);
    if (!result_) result_ = Conversion(builder_, level_ + 1);
    if (!result_) result_ = BuiltinCall(builder_, level_ + 1);
    if (!result_) result_ = PrimaryExpr_3(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_PRIMARY_EXPR, result_, false, null);
    return result_;
  }

  // PrimaryExpr (Selector | Index | Slice | TypeAssertion | Call)
  private static boolean PrimaryExpr_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "PrimaryExpr_3")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = PrimaryExpr(builder_, level_ + 1);
    result_ = result_ && PrimaryExpr_3_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // Selector | Index | Slice | TypeAssertion | Call
  private static boolean PrimaryExpr_3_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "PrimaryExpr_3_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = Selector(builder_, level_ + 1);
    if (!result_) result_ = Index(builder_, level_ + 1);
    if (!result_) result_ = Slice(builder_, level_ + 1);
    if (!result_) result_ = TypeAssertion(builder_, level_ + 1);
    if (!result_) result_ = Call(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // PackageName '.' identifier
  public static boolean QualifiedIdent(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "QualifiedIdent")) return false;
    if (!nextTokenIs(builder_, GO_IDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = PackageName(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_DOT);
    result_ = result_ && consumeToken(builder_, GO_IDENTIFIER);
    exit_section_(builder_, marker_, GO_QUALIFIED_IDENT, result_);
    return result_;
  }

  /* ********************************************************** */
  // ( ExpressionList '=' | IdentifierList ':=' ) range Expression
  public static boolean RangeClause(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "RangeClause")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<range clause>");
    result_ = RangeClause_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_RANGE);
    result_ = result_ && Expression(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_RANGE_CLAUSE, result_, false, null);
    return result_;
  }

  // ExpressionList '=' | IdentifierList ':='
  private static boolean RangeClause_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "RangeClause_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = RangeClause_0_0(builder_, level_ + 1);
    if (!result_) result_ = RangeClause_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // ExpressionList '='
  private static boolean RangeClause_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "RangeClause_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = ExpressionList(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_ASSIGN);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // IdentifierList ':='
  private static boolean RangeClause_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "RangeClause_0_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = IdentifierList(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_VAR_ASSIGN);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // '(' [ identifier ] [ '*' ] BaseTypeName ')'
  public static boolean Receiver(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Receiver")) return false;
    if (!nextTokenIs(builder_, GO_LPAREN)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_LPAREN);
    result_ = result_ && Receiver_1(builder_, level_ + 1);
    result_ = result_ && Receiver_2(builder_, level_ + 1);
    result_ = result_ && BaseTypeName(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_RPAREN);
    exit_section_(builder_, marker_, GO_RECEIVER, result_);
    return result_;
  }

  // [ identifier ]
  private static boolean Receiver_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Receiver_1")) return false;
    consumeToken(builder_, GO_IDENTIFIER);
    return true;
  }

  // [ '*' ]
  private static boolean Receiver_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Receiver_2")) return false;
    consumeToken(builder_, GO_MUL);
    return true;
  }

  /* ********************************************************** */
  // TypeName | '(' '*' TypeName ')' | '(' ReceiverType ')'
  public static boolean ReceiverType(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ReceiverType")) return false;
    if (!nextTokenIs(builder_, "<receiver type>", GO_LPAREN, GO_IDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<receiver type>");
    result_ = TypeName(builder_, level_ + 1);
    if (!result_) result_ = ReceiverType_1(builder_, level_ + 1);
    if (!result_) result_ = ReceiverType_2(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_RECEIVER_TYPE, result_, false, null);
    return result_;
  }

  // '(' '*' TypeName ')'
  private static boolean ReceiverType_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ReceiverType_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_LPAREN);
    result_ = result_ && consumeToken(builder_, GO_MUL);
    result_ = result_ && TypeName(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_RPAREN);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // '(' ReceiverType ')'
  private static boolean ReceiverType_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ReceiverType_2")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_LPAREN);
    result_ = result_ && ReceiverType(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_RPAREN);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // [ ExpressionList '=' | IdentifierList ':=' ] Expression
  public static boolean RecvStatement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "RecvStatement")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<recv statement>");
    result_ = RecvStatement_0(builder_, level_ + 1);
    result_ = result_ && Expression(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_RECV_STATEMENT, result_, false, null);
    return result_;
  }

  // [ ExpressionList '=' | IdentifierList ':=' ]
  private static boolean RecvStatement_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "RecvStatement_0")) return false;
    RecvStatement_0_0(builder_, level_ + 1);
    return true;
  }

  // ExpressionList '=' | IdentifierList ':='
  private static boolean RecvStatement_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "RecvStatement_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = RecvStatement_0_0_0(builder_, level_ + 1);
    if (!result_) result_ = RecvStatement_0_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // ExpressionList '='
  private static boolean RecvStatement_0_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "RecvStatement_0_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = ExpressionList(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_ASSIGN);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // IdentifierList ':='
  private static boolean RecvStatement_0_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "RecvStatement_0_0_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = IdentifierList(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_VAR_ASSIGN);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // Parameters | GoType
  public static boolean Result(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Result")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<result>");
    result_ = Parameters(builder_, level_ + 1);
    if (!result_) result_ = GoType(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_RESULT, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // return [ ExpressionList ]
  public static boolean ReturnStatement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ReturnStatement")) return false;
    if (!nextTokenIs(builder_, GO_RETURN)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_RETURN);
    result_ = result_ && ReturnStatement_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_RETURN_STATEMENT, result_);
    return result_;
  }

  // [ ExpressionList ]
  private static boolean ReturnStatement_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ReturnStatement_1")) return false;
    ExpressionList(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // select '{' ( CommClause )* '}'
  public static boolean SelectStatement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "SelectStatement")) return false;
    if (!nextTokenIs(builder_, GO_SELECT)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_SELECT);
    result_ = result_ && consumeToken(builder_, GO_LBRACE);
    result_ = result_ && SelectStatement_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_RBRACE);
    exit_section_(builder_, marker_, GO_SELECT_STATEMENT, result_);
    return result_;
  }

  // ( CommClause )*
  private static boolean SelectStatement_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "SelectStatement_2")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!SelectStatement_2_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "SelectStatement_2", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // ( CommClause )
  private static boolean SelectStatement_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "SelectStatement_2_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = CommClause(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // '.' identifier
  public static boolean Selector(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Selector")) return false;
    if (!nextTokenIs(builder_, GO_DOT)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_DOT);
    result_ = result_ && consumeToken(builder_, GO_IDENTIFIER);
    exit_section_(builder_, marker_, GO_SELECTOR, result_);
    return result_;
  }

  /* ********************************************************** */
  // Channel '<-' Expression
  public static boolean SendStatement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "SendStatement")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<send statement>");
    result_ = Channel(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_SEND_CHANNEL);
    result_ = result_ && Expression(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_SEND_STATEMENT, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // IdentifierList ':=' ExpressionList
  public static boolean ShortVarDecl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ShortVarDecl")) return false;
    if (!nextTokenIs(builder_, GO_IDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = IdentifierList(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_VAR_ASSIGN);
    result_ = result_ && ExpressionList(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_SHORT_VAR_DECL, result_);
    return result_;
  }

  /* ********************************************************** */
  // Parameters [ Result ]
  public static boolean Signature(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Signature")) return false;
    if (!nextTokenIs(builder_, GO_LPAREN)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = Parameters(builder_, level_ + 1);
    result_ = result_ && Signature_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_SIGNATURE, result_);
    return result_;
  }

  // [ Result ]
  private static boolean Signature_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Signature_1")) return false;
    Result(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // ExpressionStatement
  //   | SendStatement
  //   | IncDecStatement
  //   | Assignment
  //   | ShortVarDecl
  public static boolean SimpleStatement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "SimpleStatement")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<simple statement>");
    result_ = ExpressionStatement(builder_, level_ + 1);
    if (!result_) result_ = SendStatement(builder_, level_ + 1);
    if (!result_) result_ = IncDecStatement(builder_, level_ + 1);
    if (!result_) result_ = Assignment(builder_, level_ + 1);
    if (!result_) result_ = ShortVarDecl(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_SIMPLE_STATEMENT, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // '['
  //   ( [ Expression ] ':' [ Expression ] )
  //   | ( [ Expression ] ':' Expression ':' Expression ) ']'
  public static boolean Slice(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Slice")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<slice>");
    result_ = Slice_0(builder_, level_ + 1);
    if (!result_) result_ = Slice_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_SLICE, result_, false, null);
    return result_;
  }

  // '['
  //   ( [ Expression ] ':' [ Expression ] )
  private static boolean Slice_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Slice_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_LBRACK);
    result_ = result_ && Slice_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // [ Expression ] ':' [ Expression ]
  private static boolean Slice_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Slice_0_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = Slice_0_1_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_COLON);
    result_ = result_ && Slice_0_1_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // [ Expression ]
  private static boolean Slice_0_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Slice_0_1_0")) return false;
    Expression(builder_, level_ + 1);
    return true;
  }

  // [ Expression ]
  private static boolean Slice_0_1_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Slice_0_1_2")) return false;
    Expression(builder_, level_ + 1);
    return true;
  }

  // ( [ Expression ] ':' Expression ':' Expression ) ']'
  private static boolean Slice_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Slice_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = Slice_1_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_RBRACK);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // [ Expression ] ':' Expression ':' Expression
  private static boolean Slice_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Slice_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = Slice_1_0_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_COLON);
    result_ = result_ && Expression(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_COLON);
    result_ = result_ && Expression(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // [ Expression ]
  private static boolean Slice_1_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Slice_1_0_0")) return false;
    Expression(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // '[' ']' ElementType
  public static boolean SliceType(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "SliceType")) return false;
    if (!nextTokenIs(builder_, GO_LBRACK)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_LBRACK);
    result_ = result_ && consumeToken(builder_, GO_RBRACK);
    result_ = result_ && ElementType(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_SLICE_TYPE, result_);
    return result_;
  }

  /* ********************************************************** */
  // PackageClause semi ( ImportDecl semi )* ( TopLevelDecl semi )*
  static boolean SourceFile(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "SourceFile")) return false;
    if (!nextTokenIs(builder_, GO_PACKAGE)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = PackageClause(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, semi(builder_, level_ + 1));
    result_ = pinned_ && report_error_(builder_, SourceFile_2(builder_, level_ + 1)) && result_;
    result_ = pinned_ && SourceFile_3(builder_, level_ + 1) && result_;
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  // ( ImportDecl semi )*
  private static boolean SourceFile_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "SourceFile_2")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!SourceFile_2_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "SourceFile_2", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // ImportDecl semi
  private static boolean SourceFile_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "SourceFile_2_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = ImportDecl(builder_, level_ + 1);
    result_ = result_ && semi(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // ( TopLevelDecl semi )*
  private static boolean SourceFile_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "SourceFile_3")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!SourceFile_3_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "SourceFile_3", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // TopLevelDecl semi
  private static boolean SourceFile_3_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "SourceFile_3_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = TopLevelDecl(builder_, level_ + 1);
    result_ = result_ && semi(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // DeclarationStatement
  // 	| LabeledStatement
  // 	| SimpleStatement
  // 	| GoStatement
  // 	| ReturnStatement
  // 	| BreakStatement
  // 	| ContinueStatement
  // 	| GotoStatement
  // 	| FallthroughStatement
  // 	| Block
  // 	| IfStatement
  // 	| SwitchStatement
  // 	| SelectStatement
  // 	| ForStatement
  // 	| DeferStatement
  public static boolean Statement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Statement")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<statement>");
    result_ = DeclarationStatement(builder_, level_ + 1);
    if (!result_) result_ = LabeledStatement(builder_, level_ + 1);
    if (!result_) result_ = SimpleStatement(builder_, level_ + 1);
    if (!result_) result_ = GoStatement(builder_, level_ + 1);
    if (!result_) result_ = ReturnStatement(builder_, level_ + 1);
    if (!result_) result_ = BreakStatement(builder_, level_ + 1);
    if (!result_) result_ = ContinueStatement(builder_, level_ + 1);
    if (!result_) result_ = GotoStatement(builder_, level_ + 1);
    if (!result_) result_ = FallthroughStatement(builder_, level_ + 1);
    if (!result_) result_ = Block(builder_, level_ + 1);
    if (!result_) result_ = IfStatement(builder_, level_ + 1);
    if (!result_) result_ = SwitchStatement(builder_, level_ + 1);
    if (!result_) result_ = SelectStatement(builder_, level_ + 1);
    if (!result_) result_ = ForStatement(builder_, level_ + 1);
    if (!result_) result_ = DeferStatement(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_STATEMENT, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // ( Statement semi )*
  public static boolean StatementList(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "StatementList")) return false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<statement list>");
    int pos_ = current_position_(builder_);
    while (true) {
      if (!StatementList_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "StatementList", pos_)) break;
      pos_ = current_position_(builder_);
    }
    exit_section_(builder_, level_, marker_, GO_STATEMENT_LIST, true, false, null);
    return true;
  }

  // Statement semi
  private static boolean StatementList_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "StatementList_0")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = Statement(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && semi(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // struct '{' ( FieldDecl semi )* '}'
  public static boolean StructType(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "StructType")) return false;
    if (!nextTokenIs(builder_, GO_STRUCT)) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, GO_STRUCT);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, consumeToken(builder_, GO_LBRACE));
    result_ = pinned_ && report_error_(builder_, StructType_2(builder_, level_ + 1)) && result_;
    result_ = pinned_ && consumeToken(builder_, GO_RBRACE) && result_;
    exit_section_(builder_, level_, marker_, GO_STRUCT_TYPE, result_, pinned_, null);
    return result_ || pinned_;
  }

  // ( FieldDecl semi )*
  private static boolean StructType_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "StructType_2")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!StructType_2_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "StructType_2", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // FieldDecl semi
  private static boolean StructType_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "StructType_2_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = FieldDecl(builder_, level_ + 1);
    result_ = result_ && semi(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // ExprSwitchStatement | TypeSwitchStatement
  public static boolean SwitchStatement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "SwitchStatement")) return false;
    if (!nextTokenIs(builder_, GO_SWITCH)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = ExprSwitchStatement(builder_, level_ + 1);
    if (!result_) result_ = TypeSwitchStatement(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_SWITCH_STATEMENT, result_);
    return result_;
  }

  /* ********************************************************** */
  // string
  public static boolean Tag(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Tag")) return false;
    if (!nextTokenIs(builder_, GO_STRING)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_STRING);
    exit_section_(builder_, marker_, GO_TAG, result_);
    return result_;
  }

  /* ********************************************************** */
  // DeclarationStatement | FunctionDecl | MethodDecl
  public static boolean TopLevelDecl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "TopLevelDecl")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<top level decl>");
    result_ = DeclarationStatement(builder_, level_ + 1);
    if (!result_) result_ = FunctionDecl(builder_, level_ + 1);
    if (!result_) result_ = MethodDecl(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_TOP_LEVEL_DECL, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // '.' '(' GoType ')'
  public static boolean TypeAssertion(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "TypeAssertion")) return false;
    if (!nextTokenIs(builder_, GO_DOT)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_DOT);
    result_ = result_ && consumeToken(builder_, GO_LPAREN);
    result_ = result_ && GoType(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_RPAREN);
    exit_section_(builder_, marker_, GO_TYPE_ASSERTION, result_);
    return result_;
  }

  /* ********************************************************** */
  // TypeSwitchCase ':' StatementList
  public static boolean TypeCaseClause(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "TypeCaseClause")) return false;
    if (!nextTokenIs(builder_, "<type case clause>", GO_CASE, GO_DEFAULT)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<type case clause>");
    result_ = TypeSwitchCase(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_COLON);
    result_ = result_ && StatementList(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_TYPE_CASE_CLAUSE, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // type ( TypeSpec | '(' ( TypeSpec semi )* ')' )
  public static boolean TypeDecl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "TypeDecl")) return false;
    if (!nextTokenIs(builder_, GO_TYPE)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_TYPE);
    result_ = result_ && TypeDecl_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_TYPE_DECL, result_);
    return result_;
  }

  // TypeSpec | '(' ( TypeSpec semi )* ')'
  private static boolean TypeDecl_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "TypeDecl_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = TypeSpec(builder_, level_ + 1);
    if (!result_) result_ = TypeDecl_1_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // '(' ( TypeSpec semi )* ')'
  private static boolean TypeDecl_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "TypeDecl_1_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_LPAREN);
    result_ = result_ && TypeDecl_1_1_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_RPAREN);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // ( TypeSpec semi )*
  private static boolean TypeDecl_1_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "TypeDecl_1_1_1")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!TypeDecl_1_1_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "TypeDecl_1_1_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // TypeSpec semi
  private static boolean TypeDecl_1_1_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "TypeDecl_1_1_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = TypeSpec(builder_, level_ + 1);
    result_ = result_ && semi(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // GoType ( ',' GoType )*
  public static boolean TypeList(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "TypeList")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<type list>");
    result_ = GoType(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && TypeList_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_TYPE_LIST, result_, pinned_, null);
    return result_ || pinned_;
  }

  // ( ',' GoType )*
  private static boolean TypeList_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "TypeList_1")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!TypeList_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "TypeList_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // ',' GoType
  private static boolean TypeList_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "TypeList_1_0")) return false;
    boolean result_ = false;
    boolean pinned_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = consumeToken(builder_, GO_COMMA);
    pinned_ = result_; // pin = 1
    result_ = result_ && GoType(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  /* ********************************************************** */
  // ArrayType
  //   | StructType
  //   | PointerType
  //   | FunctionType
  //   | InterfaceType
  //   | SliceType
  //   | MapType
  //   | ChannelType
  public static boolean TypeLit(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "TypeLit")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<type lit>");
    result_ = ArrayType(builder_, level_ + 1);
    if (!result_) result_ = StructType(builder_, level_ + 1);
    if (!result_) result_ = PointerType(builder_, level_ + 1);
    if (!result_) result_ = FunctionType(builder_, level_ + 1);
    if (!result_) result_ = InterfaceType(builder_, level_ + 1);
    if (!result_) result_ = SliceType(builder_, level_ + 1);
    if (!result_) result_ = MapType(builder_, level_ + 1);
    if (!result_) result_ = ChannelType(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_TYPE_LIT, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // identifier | QualifiedIdent
  public static boolean TypeName(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "TypeName")) return false;
    if (!nextTokenIs(builder_, GO_IDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_IDENTIFIER);
    if (!result_) result_ = QualifiedIdent(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_TYPE_NAME, result_);
    return result_;
  }

  /* ********************************************************** */
  // identifier GoType
  public static boolean TypeSpec(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "TypeSpec")) return false;
    if (!nextTokenIs(builder_, GO_IDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_IDENTIFIER);
    result_ = result_ && GoType(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_TYPE_SPEC, result_);
    return result_;
  }

  /* ********************************************************** */
  // case TypeList | default
  public static boolean TypeSwitchCase(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "TypeSwitchCase")) return false;
    if (!nextTokenIs(builder_, "<type switch case>", GO_CASE, GO_DEFAULT)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<type switch case>");
    result_ = TypeSwitchCase_0(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, GO_DEFAULT);
    exit_section_(builder_, level_, marker_, GO_TYPE_SWITCH_CASE, result_, false, null);
    return result_;
  }

  // case TypeList
  private static boolean TypeSwitchCase_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "TypeSwitchCase_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_CASE);
    result_ = result_ && TypeList(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // [ identifier ':=' ] PrimaryExpr '.' '(' type ')'
  public static boolean TypeSwitchGuard(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "TypeSwitchGuard")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<type switch guard>");
    result_ = TypeSwitchGuard_0(builder_, level_ + 1);
    result_ = result_ && PrimaryExpr(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_DOT);
    result_ = result_ && consumeToken(builder_, GO_LPAREN);
    result_ = result_ && consumeToken(builder_, GO_TYPE);
    result_ = result_ && consumeToken(builder_, GO_RPAREN);
    exit_section_(builder_, level_, marker_, GO_TYPE_SWITCH_GUARD, result_, false, null);
    return result_;
  }

  // [ identifier ':=' ]
  private static boolean TypeSwitchGuard_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "TypeSwitchGuard_0")) return false;
    TypeSwitchGuard_0_0(builder_, level_ + 1);
    return true;
  }

  // identifier ':='
  private static boolean TypeSwitchGuard_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "TypeSwitchGuard_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_IDENTIFIER);
    result_ = result_ && consumeToken(builder_, GO_VAR_ASSIGN);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // switch [ SimpleStatement semi ] TypeSwitchGuard '{' ( TypeCaseClause )* '}'
  public static boolean TypeSwitchStatement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "TypeSwitchStatement")) return false;
    if (!nextTokenIs(builder_, GO_SWITCH)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_SWITCH);
    result_ = result_ && TypeSwitchStatement_1(builder_, level_ + 1);
    result_ = result_ && TypeSwitchGuard(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_LBRACE);
    result_ = result_ && TypeSwitchStatement_4(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_RBRACE);
    exit_section_(builder_, marker_, GO_TYPE_SWITCH_STATEMENT, result_);
    return result_;
  }

  // [ SimpleStatement semi ]
  private static boolean TypeSwitchStatement_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "TypeSwitchStatement_1")) return false;
    TypeSwitchStatement_1_0(builder_, level_ + 1);
    return true;
  }

  // SimpleStatement semi
  private static boolean TypeSwitchStatement_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "TypeSwitchStatement_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = SimpleStatement(builder_, level_ + 1);
    result_ = result_ && semi(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // ( TypeCaseClause )*
  private static boolean TypeSwitchStatement_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "TypeSwitchStatement_4")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!TypeSwitchStatement_4_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "TypeSwitchStatement_4", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // ( TypeCaseClause )
  private static boolean TypeSwitchStatement_4_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "TypeSwitchStatement_4_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = TypeCaseClause(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // PrimaryExpr | unary_op UnaryExpr
  public static boolean UnaryExpr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "UnaryExpr")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<unary expr>");
    result_ = PrimaryExpr(builder_, level_ + 1);
    if (!result_) result_ = UnaryExpr_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_UNARY_EXPR, result_, false, null);
    return result_;
  }

  // unary_op UnaryExpr
  private static boolean UnaryExpr_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "UnaryExpr_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = unary_op(builder_, level_ + 1);
    result_ = result_ && UnaryExpr(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // Expression | LiteralValue
  public static boolean Value(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Value")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<value>");
    result_ = Expression(builder_, level_ + 1);
    if (!result_) result_ = LiteralValue(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_VALUE, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // var ( VarSpec | '(' ( VarSpec semi )* ')' )
  public static boolean VarDecl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "VarDecl")) return false;
    if (!nextTokenIs(builder_, GO_VAR)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_VAR);
    result_ = result_ && VarDecl_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_VAR_DECL, result_);
    return result_;
  }

  // VarSpec | '(' ( VarSpec semi )* ')'
  private static boolean VarDecl_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "VarDecl_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = VarSpec(builder_, level_ + 1);
    if (!result_) result_ = VarDecl_1_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // '(' ( VarSpec semi )* ')'
  private static boolean VarDecl_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "VarDecl_1_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_LPAREN);
    result_ = result_ && VarDecl_1_1_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GO_RPAREN);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // ( VarSpec semi )*
  private static boolean VarDecl_1_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "VarDecl_1_1_1")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!VarDecl_1_1_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "VarDecl_1_1_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // VarSpec semi
  private static boolean VarDecl_1_1_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "VarDecl_1_1_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = VarSpec(builder_, level_ + 1);
    result_ = result_ && semi(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // IdentifierList ( GoType [ '=' ExpressionList ] | '=' ExpressionList )
  public static boolean VarSpec(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "VarSpec")) return false;
    if (!nextTokenIs(builder_, GO_IDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = IdentifierList(builder_, level_ + 1);
    result_ = result_ && VarSpec_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, GO_VAR_SPEC, result_);
    return result_;
  }

  // GoType [ '=' ExpressionList ] | '=' ExpressionList
  private static boolean VarSpec_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "VarSpec_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = VarSpec_1_0(builder_, level_ + 1);
    if (!result_) result_ = VarSpec_1_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // GoType [ '=' ExpressionList ]
  private static boolean VarSpec_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "VarSpec_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = GoType(builder_, level_ + 1);
    result_ = result_ && VarSpec_1_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // [ '=' ExpressionList ]
  private static boolean VarSpec_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "VarSpec_1_0_1")) return false;
    VarSpec_1_0_1_0(builder_, level_ + 1);
    return true;
  }

  // '=' ExpressionList
  private static boolean VarSpec_1_0_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "VarSpec_1_0_1_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_ASSIGN);
    result_ = result_ && ExpressionList(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // '=' ExpressionList
  private static boolean VarSpec_1_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "VarSpec_1_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_ASSIGN);
    result_ = result_ && ExpressionList(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // '+' | '-' | '|' | '^'
  public static boolean add_op(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "add_op")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<add op>");
    result_ = consumeToken(builder_, GO_PLUS);
    if (!result_) result_ = consumeToken(builder_, GO_MINUS);
    if (!result_) result_ = consumeToken(builder_, GO_BIT_OR);
    if (!result_) result_ = consumeToken(builder_, GO_BIT_XOR);
    exit_section_(builder_, level_, marker_, GO_ADD_OP, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // '=' | '+=' | '-=' | '|=' | '^=' | '*=' | '/=' | '%=' | '<<=' | '>>=' | '&=' | '&^='
  public static boolean assign_op(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "assign_op")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<assign op>");
    result_ = consumeToken(builder_, GO_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, GO_PLUS_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, GO_MINUS_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, GO_BIT_OR_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, GO_BIT_XOR_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, GO_MUL_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, GO_QUOTIENT_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, GO_REMAINDER_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, GO_SHIFT_LEFT_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, GO_SHIFT_RIGHT_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, GO_BIT_AND_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, GO_BIT_CLEAR_ASSIGN);
    exit_section_(builder_, level_, marker_, GO_ASSIGN_OP, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // '||' | '&&' | rel_op | add_op | mul_op
  public static boolean binary_op(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "binary_op")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<binary op>");
    result_ = consumeToken(builder_, GO_COND_OR);
    if (!result_) result_ = consumeToken(builder_, GO_COND_AND);
    if (!result_) result_ = rel_op(builder_, level_ + 1);
    if (!result_) result_ = add_op(builder_, level_ + 1);
    if (!result_) result_ = mul_op(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, GO_BINARY_OP, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // char | oct | hex
  public static boolean dummy(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "dummy")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<dummy>");
    result_ = consumeToken(builder_, GO_CHAR);
    if (!result_) result_ = consumeToken(builder_, GO_OCT);
    if (!result_) result_ = consumeToken(builder_, GO_HEX);
    exit_section_(builder_, level_, marker_, GO_DUMMY, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // '*' | '/' | '%' | '<<' | '>>' | '&' | '&^'
  public static boolean mul_op(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "mul_op")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<mul op>");
    result_ = consumeToken(builder_, GO_MUL);
    if (!result_) result_ = consumeToken(builder_, GO_QUOTIENT);
    if (!result_) result_ = consumeToken(builder_, GO_REMAINDER);
    if (!result_) result_ = consumeToken(builder_, GO_SHIFT_LEFT);
    if (!result_) result_ = consumeToken(builder_, GO_SHIFT_RIGHT);
    if (!result_) result_ = consumeToken(builder_, GO_BIT_AND);
    if (!result_) result_ = consumeToken(builder_, GO_BIT_CLEAR);
    exit_section_(builder_, level_, marker_, GO_MUL_OP, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // '==' | '!=' | '<' | '<=' | '>' | '>='
  public static boolean rel_op(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "rel_op")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<rel op>");
    result_ = consumeToken(builder_, GO_EQ);
    if (!result_) result_ = consumeToken(builder_, GO_NOT_EQ);
    if (!result_) result_ = consumeToken(builder_, GO_LESS);
    if (!result_) result_ = consumeToken(builder_, GO_LESS_OR_EQUAL);
    if (!result_) result_ = consumeToken(builder_, GO_GREATER);
    if (!result_) result_ = consumeToken(builder_, GO_GREATER_OR_EQUAL);
    exit_section_(builder_, level_, marker_, GO_REL_OP, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // '<NL>' | ';'?
  static boolean semi(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "semi")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, GO_SEMICOLON_SYNTHETIC);
    if (!result_) result_ = semi_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // ';'?
  private static boolean semi_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "semi_1")) return false;
    consumeToken(builder_, GO_SEMICOLON);
    return true;
  }

  /* ********************************************************** */
  // '+' | '-' | '!' | '^' | '*' | '&' | '<-'
  public static boolean unary_op(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unary_op")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<unary op>");
    result_ = consumeToken(builder_, GO_PLUS);
    if (!result_) result_ = consumeToken(builder_, GO_MINUS);
    if (!result_) result_ = consumeToken(builder_, GO_NOT);
    if (!result_) result_ = consumeToken(builder_, GO_BIT_XOR);
    if (!result_) result_ = consumeToken(builder_, GO_MUL);
    if (!result_) result_ = consumeToken(builder_, GO_BIT_AND);
    if (!result_) result_ = consumeToken(builder_, GO_SEND_CHANNEL);
    exit_section_(builder_, level_, marker_, GO_UNARY_OP, result_, false, null);
    return result_;
  }

}
