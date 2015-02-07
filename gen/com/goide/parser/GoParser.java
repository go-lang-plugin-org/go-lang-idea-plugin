// This is a generated file. Not intended for manual editing.
package com.goide.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.goide.GoTypes.*;
import static com.goide.parser.GoParserUtil.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class GoParser implements PsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, EXTENDS_SETS_);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    if (t == ADD_EXPR) {
      r = Expression(b, 0, 2);
    }
    else if (t == AND_EXPR) {
      r = Expression(b, 0, 0);
    }
    else if (t == ANONYMOUS_FIELD_DEFINITION) {
      r = AnonymousFieldDefinition(b, 0);
    }
    else if (t == ARGUMENT_LIST) {
      r = ArgumentList(b, 0);
    }
    else if (t == ARRAY_OR_SLICE_TYPE) {
      r = ArrayOrSliceType(b, 0);
    }
    else if (t == ASSIGNMENT_STATEMENT) {
      r = AssignmentStatement(b, 0);
    }
    else if (t == BLOCK) {
      r = Block(b, 0);
    }
    else if (t == BREAK_STATEMENT) {
      r = BreakStatement(b, 0);
    }
    else if (t == BUILTIN_ARGS) {
      r = BuiltinArgs(b, 0);
    }
    else if (t == BUILTIN_CALL_EXPR) {
      r = Expression(b, 0, 6);
    }
    else if (t == CALL_EXPR) {
      r = Expression(b, 0, 6);
    }
    else if (t == CHANNEL_TYPE) {
      r = ChannelType(b, 0);
    }
    else if (t == COMM_CASE) {
      r = CommCase(b, 0);
    }
    else if (t == COMM_CLAUSE) {
      r = CommClause(b, 0);
    }
    else if (t == COMPOSITE_LIT) {
      r = CompositeLit(b, 0);
    }
    else if (t == CONDITIONAL_EXPR) {
      r = Expression(b, 0, 1);
    }
    else if (t == CONST_DECLARATION) {
      r = ConstDeclaration(b, 0);
    }
    else if (t == CONST_DEFINITION) {
      r = ConstDefinition(b, 0);
    }
    else if (t == CONST_SPEC) {
      r = ConstSpec(b, 0);
    }
    else if (t == CONTINUE_STATEMENT) {
      r = ContinueStatement(b, 0);
    }
    else if (t == CONVERSION_EXPR) {
      r = ConversionExpr(b, 0);
    }
    else if (t == DEFER_STATEMENT) {
      r = DeferStatement(b, 0);
    }
    else if (t == ELEMENT) {
      r = Element(b, 0);
    }
    else if (t == ELEMENT_INDEX) {
      r = ElementIndex(b, 0);
    }
    else if (t == ELSE_STATEMENT) {
      r = ElseStatement(b, 0);
    }
    else if (t == EXPR_CASE_CLAUSE) {
      r = ExprCaseClause(b, 0);
    }
    else if (t == EXPR_SWITCH_CASE) {
      r = ExprSwitchCase(b, 0);
    }
    else if (t == EXPR_SWITCH_STATEMENT) {
      r = ExprSwitchStatement(b, 0);
    }
    else if (t == EXPRESSION) {
      r = Expression(b, 0, -1);
    }
    else if (t == FALLTHROUGH_STATEMENT) {
      r = FallthroughStatement(b, 0);
    }
    else if (t == FIELD_DECLARATION) {
      r = FieldDeclaration(b, 0);
    }
    else if (t == FIELD_DEFINITION) {
      r = FieldDefinition(b, 0);
    }
    else if (t == FIELD_NAME) {
      r = FieldName(b, 0);
    }
    else if (t == FOR_CLAUSE) {
      r = ForClause(b, 0);
    }
    else if (t == FOR_STATEMENT) {
      r = ForStatement(b, 0);
    }
    else if (t == FUNCTION_DECLARATION) {
      r = FunctionDeclaration(b, 0);
    }
    else if (t == FUNCTION_LIT) {
      r = FunctionLit(b, 0);
    }
    else if (t == FUNCTION_TYPE) {
      r = FunctionType(b, 0);
    }
    else if (t == GO_STATEMENT) {
      r = GoStatement(b, 0);
    }
    else if (t == GOTO_STATEMENT) {
      r = GotoStatement(b, 0);
    }
    else if (t == IF_STATEMENT) {
      r = IfStatement(b, 0);
    }
    else if (t == IMPORT_DECLARATION) {
      r = ImportDeclaration(b, 0);
    }
    else if (t == IMPORT_LIST) {
      r = ImportList(b, 0);
    }
    else if (t == IMPORT_SPEC) {
      r = ImportSpec(b, 0);
    }
    else if (t == IMPORT_STRING) {
      r = ImportString(b, 0);
    }
    else if (t == INDEX_EXPR) {
      r = Expression(b, 0, 6);
    }
    else if (t == INTERFACE_TYPE) {
      r = InterfaceType(b, 0);
    }
    else if (t == KEY) {
      r = Key(b, 0);
    }
    else if (t == LABEL_DEFINITION) {
      r = LabelDefinition(b, 0);
    }
    else if (t == LABEL_REF) {
      r = LabelRef(b, 0);
    }
    else if (t == LABELED_STATEMENT) {
      r = LabeledStatement(b, 0);
    }
    else if (t == LITERAL) {
      r = Literal(b, 0);
    }
    else if (t == LITERAL_TYPE_EXPR) {
      r = LiteralTypeExpr(b, 0);
    }
    else if (t == LITERAL_VALUE) {
      r = LiteralValue(b, 0);
    }
    else if (t == MAP_TYPE) {
      r = MapType(b, 0);
    }
    else if (t == METHOD_DECLARATION) {
      r = MethodDeclaration(b, 0);
    }
    else if (t == METHOD_EXPR) {
      r = MethodExpr(b, 0);
    }
    else if (t == METHOD_SPEC) {
      r = MethodSpec(b, 0);
    }
    else if (t == MUL_EXPR) {
      r = Expression(b, 0, 3);
    }
    else if (t == OR_EXPR) {
      r = Expression(b, 0, -1);
    }
    else if (t == PACKAGE_CLAUSE) {
      r = PackageClause(b, 0);
    }
    else if (t == PARAM_DEFINITION) {
      r = ParamDefinition(b, 0);
    }
    else if (t == PARAMETER_DECLARATION) {
      r = ParameterDeclaration(b, 0);
    }
    else if (t == PARAMETERS) {
      r = Parameters(b, 0);
    }
    else if (t == PARENTHESES_EXPR) {
      r = ParenthesesExpr(b, 0);
    }
    else if (t == POINTER_TYPE) {
      r = PointerType(b, 0);
    }
    else if (t == RANGE_CLAUSE) {
      r = RangeClause(b, 0);
    }
    else if (t == RECEIVER) {
      r = Receiver(b, 0);
    }
    else if (t == RECEIVER_TYPE) {
      r = ReceiverType(b, 0);
    }
    else if (t == RECV_STATEMENT) {
      r = RecvStatement(b, 0);
    }
    else if (t == REFERENCE_EXPRESSION) {
      r = ReferenceExpression(b, 0);
    }
    else if (t == RESULT) {
      r = Result(b, 0);
    }
    else if (t == RETURN_STATEMENT) {
      r = ReturnStatement(b, 0);
    }
    else if (t == SELECT_STATEMENT) {
      r = SelectStatement(b, 0);
    }
    else if (t == SELECTOR_EXPR) {
      r = Expression(b, 0, 6);
    }
    else if (t == SEND_STATEMENT) {
      r = SendStatement(b, 0);
    }
    else if (t == SHORT_VAR_DECLARATION) {
      r = ShortVarDeclaration(b, 0);
    }
    else if (t == SIGNATURE) {
      r = Signature(b, 0);
    }
    else if (t == SIMPLE_STATEMENT) {
      r = SimpleStatement(b, 0);
    }
    else if (t == SLICE_EXPR) {
      r = Expression(b, 0, 6);
    }
    else if (t == STATEMENT) {
      r = Statement(b, 0);
    }
    else if (t == STRUCT_TYPE) {
      r = StructType(b, 0);
    }
    else if (t == SWITCH_START) {
      r = SwitchStart(b, 0);
    }
    else if (t == SWITCH_STATEMENT) {
      r = SwitchStatement(b, 0);
    }
    else if (t == TAG) {
      r = Tag(b, 0);
    }
    else if (t == TYPE) {
      r = Type(b, 0);
    }
    else if (t == TYPE_ASSERTION_EXPR) {
      r = Expression(b, 0, 6);
    }
    else if (t == TYPE_CASE_CLAUSE) {
      r = TypeCaseClause(b, 0);
    }
    else if (t == TYPE_DECLARATION) {
      r = TypeDeclaration(b, 0);
    }
    else if (t == TYPE_GUARD) {
      r = TypeGuard(b, 0);
    }
    else if (t == TYPE_LIST) {
      r = TypeList(b, 0);
    }
    else if (t == TYPE_REFERENCE_EXPRESSION) {
      r = TypeReferenceExpression(b, 0);
    }
    else if (t == TYPE_SPEC) {
      r = TypeSpec(b, 0);
    }
    else if (t == TYPE_SWITCH_CASE) {
      r = TypeSwitchCase(b, 0);
    }
    else if (t == TYPE_SWITCH_GUARD) {
      r = TypeSwitchGuard(b, 0);
    }
    else if (t == TYPE_SWITCH_STATEMENT) {
      r = TypeSwitchStatement(b, 0);
    }
    else if (t == UNARY_EXPR) {
      r = UnaryExpr(b, 0);
    }
    else if (t == VALUE) {
      r = Value(b, 0);
    }
    else if (t == VAR_DECLARATION) {
      r = VarDeclaration(b, 0);
    }
    else if (t == VAR_DEFINITION) {
      r = VarDefinition(b, 0);
    }
    else if (t == VAR_SPEC) {
      r = VarSpec(b, 0);
    }
    else if (t == ASSIGN_OP) {
      r = assign_op(b, 0);
    }
    else {
      r = parse_root_(t, b, 0);
    }
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return File(b, l + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(RECV_STATEMENT, SHORT_VAR_DECLARATION, VAR_SPEC),
    create_token_set_(EXPR_SWITCH_STATEMENT, SWITCH_STATEMENT, TYPE_SWITCH_STATEMENT),
    create_token_set_(ADD_EXPR, CONVERSION_EXPR, MUL_EXPR, OR_EXPR),
    create_token_set_(ARRAY_OR_SLICE_TYPE, CHANNEL_TYPE, FUNCTION_TYPE, INTERFACE_TYPE,
      MAP_TYPE, POINTER_TYPE, RECEIVER_TYPE, STRUCT_TYPE,
      TYPE, TYPE_LIST),
    create_token_set_(ASSIGNMENT_STATEMENT, BREAK_STATEMENT, CONTINUE_STATEMENT, DEFER_STATEMENT,
      ELSE_STATEMENT, EXPR_SWITCH_STATEMENT, FALLTHROUGH_STATEMENT, FOR_STATEMENT,
      GOTO_STATEMENT, GO_STATEMENT, IF_STATEMENT, LABELED_STATEMENT,
      RETURN_STATEMENT, SELECT_STATEMENT, SEND_STATEMENT, SIMPLE_STATEMENT,
      STATEMENT, SWITCH_STATEMENT, TYPE_SWITCH_STATEMENT),
    create_token_set_(ADD_EXPR, AND_EXPR, BUILTIN_CALL_EXPR, CALL_EXPR,
      COMPOSITE_LIT, CONDITIONAL_EXPR, CONVERSION_EXPR, EXPRESSION,
      FUNCTION_LIT, INDEX_EXPR, LITERAL, LITERAL_TYPE_EXPR,
      METHOD_EXPR, MUL_EXPR, OR_EXPR, PARENTHESES_EXPR,
      REFERENCE_EXPRESSION, SELECTOR_EXPR, SLICE_EXPR, TYPE_ASSERTION_EXPR,
      UNARY_EXPR),
  };

  /* ********************************************************** */
  // '*'? TypeName
  public static boolean AnonymousFieldDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AnonymousFieldDefinition")) return false;
    if (!nextTokenIs(b, "<anonymous field definition>", MUL, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<anonymous field definition>");
    r = AnonymousFieldDefinition_0(b, l + 1);
    r = r && TypeName(b, l + 1);
    exit_section_(b, l, m, ANONYMOUS_FIELD_DEFINITION, r, false, null);
    return r;
  }

  // '*'?
  private static boolean AnonymousFieldDefinition_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AnonymousFieldDefinition_0")) return false;
    consumeToken(b, MUL);
    return true;
  }

  /* ********************************************************** */
  // '(' [ ExpressionList '...'? ] ')'
  public static boolean ArgumentList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArgumentList")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, ArgumentList_1(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, ARGUMENT_LIST, r, p, null);
    return r || p;
  }

  // [ ExpressionList '...'? ]
  private static boolean ArgumentList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArgumentList_1")) return false;
    ArgumentList_1_0(b, l + 1);
    return true;
  }

  // ExpressionList '...'?
  private static boolean ArgumentList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArgumentList_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ExpressionList(b, l + 1);
    r = r && ArgumentList_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '...'?
  private static boolean ArgumentList_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArgumentList_1_0_1")) return false;
    consumeToken(b, TRIPLE_DOT);
    return true;
  }

  /* ********************************************************** */
  // '[' ('...'|Expression?) ']' <<exitModeSafe "NO_EMPTY_LITERAL">> Type
  public static boolean ArrayOrSliceType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArrayOrSliceType")) return false;
    if (!nextTokenIs(b, LBRACK)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, LBRACK);
    p = r; // pin = 1
    r = r && report_error_(b, ArrayOrSliceType_1(b, l + 1));
    r = p && report_error_(b, consumeToken(b, RBRACK)) && r;
    r = p && report_error_(b, exitModeSafe(b, l + 1, "NO_EMPTY_LITERAL")) && r;
    r = p && Type(b, l + 1) && r;
    exit_section_(b, l, m, ARRAY_OR_SLICE_TYPE, r, p, null);
    return r || p;
  }

  // '...'|Expression?
  private static boolean ArrayOrSliceType_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArrayOrSliceType_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, TRIPLE_DOT);
    if (!r) r = ArrayOrSliceType_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // Expression?
  private static boolean ArrayOrSliceType_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArrayOrSliceType_1_1")) return false;
    Expression(b, l + 1, -1);
    return true;
  }

  /* ********************************************************** */
  // ExpressionList assign_op ExpressionList
  public static boolean AssignmentStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AssignmentStatement")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, "<assignment statement>");
    r = ExpressionList(b, l + 1);
    r = r && assign_op(b, l + 1);
    p = r; // pin = 2
    r = r && ExpressionList(b, l + 1);
    exit_section_(b, l, m, ASSIGNMENT_STATEMENT, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '{' ('}' | Statements '}')
  public static boolean Block(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Block")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && Block_1(b, l + 1);
    exit_section_(b, l, m, BLOCK, r, p, null);
    return r || p;
  }

  // '}' | Statements '}'
  private static boolean Block_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Block_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, RBRACE);
    if (!r) r = Block_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // Statements '}'
  private static boolean Block_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Block_1_1")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = Statements(b, l + 1);
    p = r; // pin = 1
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // break LabelRef?
  public static boolean BreakStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BreakStatement")) return false;
    if (!nextTokenIs(b, BREAK)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, BREAK);
    p = r; // pin = 1
    r = r && BreakStatement_1(b, l + 1);
    exit_section_(b, l, m, BREAK_STATEMENT, r, p, null);
    return r || p;
  }

  // LabelRef?
  private static boolean BreakStatement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BreakStatement_1")) return false;
    LabelRef(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // Type [ ',' ExpressionList '...'? ] | ExpressionList '...'?
  public static boolean BuiltinArgs(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BuiltinArgs")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<builtin args>");
    r = BuiltinArgs_0(b, l + 1);
    if (!r) r = BuiltinArgs_1(b, l + 1);
    exit_section_(b, l, m, BUILTIN_ARGS, r, false, null);
    return r;
  }

  // Type [ ',' ExpressionList '...'? ]
  private static boolean BuiltinArgs_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BuiltinArgs_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Type(b, l + 1);
    r = r && BuiltinArgs_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // [ ',' ExpressionList '...'? ]
  private static boolean BuiltinArgs_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BuiltinArgs_0_1")) return false;
    BuiltinArgs_0_1_0(b, l + 1);
    return true;
  }

  // ',' ExpressionList '...'?
  private static boolean BuiltinArgs_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BuiltinArgs_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && ExpressionList(b, l + 1);
    r = r && BuiltinArgs_0_1_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '...'?
  private static boolean BuiltinArgs_0_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BuiltinArgs_0_1_0_2")) return false;
    consumeToken(b, TRIPLE_DOT);
    return true;
  }

  // ExpressionList '...'?
  private static boolean BuiltinArgs_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BuiltinArgs_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ExpressionList(b, l + 1);
    r = r && BuiltinArgs_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '...'?
  private static boolean BuiltinArgs_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BuiltinArgs_1_1")) return false;
    consumeToken(b, TRIPLE_DOT);
    return true;
  }

  /* ********************************************************** */
  // chan '<-'? | '<-' chan
  static boolean ChanTypePrefix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ChanTypePrefix")) return false;
    if (!nextTokenIs(b, "", SEND_CHANNEL, CHAN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ChanTypePrefix_0(b, l + 1);
    if (!r) r = ChanTypePrefix_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // chan '<-'?
  private static boolean ChanTypePrefix_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ChanTypePrefix_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, CHAN);
    p = r; // pin = 1
    r = r && ChanTypePrefix_0_1(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // '<-'?
  private static boolean ChanTypePrefix_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ChanTypePrefix_0_1")) return false;
    consumeToken(b, SEND_CHANNEL);
    return true;
  }

  // '<-' chan
  private static boolean ChanTypePrefix_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ChanTypePrefix_1")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, SEND_CHANNEL);
    p = r; // pin = 1
    r = r && consumeToken(b, CHAN);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ChanTypePrefix Type
  public static boolean ChannelType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ChannelType")) return false;
    if (!nextTokenIs(b, "<channel type>", SEND_CHANNEL, CHAN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _COLLAPSE_, "<channel type>");
    r = ChanTypePrefix(b, l + 1);
    p = r; // pin = 1
    r = r && Type(b, l + 1);
    exit_section_(b, l, m, CHANNEL_TYPE, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // case ( SendStatement | RecvStatement ) | default
  public static boolean CommCase(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CommCase")) return false;
    if (!nextTokenIs(b, "<comm case>", CASE, DEFAULT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<comm case>");
    r = CommCase_0(b, l + 1);
    if (!r) r = consumeToken(b, DEFAULT);
    exit_section_(b, l, m, COMM_CASE, r, false, null);
    return r;
  }

  // case ( SendStatement | RecvStatement )
  private static boolean CommCase_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CommCase_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, CASE);
    p = r; // pin = 1
    r = r && CommCase_0_1(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // SendStatement | RecvStatement
  private static boolean CommCase_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CommCase_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = SendStatement(b, l + 1);
    if (!r) r = RecvStatement(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // CommCase ':' Statements?
  public static boolean CommClause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CommClause")) return false;
    if (!nextTokenIs(b, "<comm clause>", CASE, DEFAULT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, "<comm clause>");
    r = CommCase(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, COLON));
    r = p && CommClause_2(b, l + 1) && r;
    exit_section_(b, l, m, COMM_CLAUSE, r, p, null);
    return r || p;
  }

  // Statements?
  private static boolean CommClause_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CommClause_2")) return false;
    Statements(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // <<enterMode "NO_EMPTY_LITERAL">> SimpleStatementOpt Expression? <<exitModeSafe "NO_EMPTY_LITERAL">>
  static boolean Condition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Condition")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = enterMode(b, l + 1, "NO_EMPTY_LITERAL");
    r = r && SimpleStatementOpt(b, l + 1);
    r = r && Condition_2(b, l + 1);
    r = r && exitModeSafe(b, l + 1, "NO_EMPTY_LITERAL");
    exit_section_(b, m, null, r);
    return r;
  }

  // Expression?
  private static boolean Condition_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Condition_2")) return false;
    Expression(b, l + 1, -1);
    return true;
  }

  /* ********************************************************** */
  // const ( ConstSpec | '(' ConstSpecs? ')' )
  public static boolean ConstDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConstDeclaration")) return false;
    if (!nextTokenIs(b, CONST)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, CONST);
    p = r; // pin = 1
    r = r && ConstDeclaration_1(b, l + 1);
    exit_section_(b, l, m, CONST_DECLARATION, r, p, null);
    return r || p;
  }

  // ConstSpec | '(' ConstSpecs? ')'
  private static boolean ConstDeclaration_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConstDeclaration_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ConstSpec(b, l + 1);
    if (!r) r = ConstDeclaration_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '(' ConstSpecs? ')'
  private static boolean ConstDeclaration_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConstDeclaration_1_1")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, ConstDeclaration_1_1_1(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // ConstSpecs?
  private static boolean ConstDeclaration_1_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConstDeclaration_1_1_1")) return false;
    ConstSpecs(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // identifier
  public static boolean ConstDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConstDefinition")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, CONST_DEFINITION, r);
    return r;
  }

  /* ********************************************************** */
  // ConstDefinition ( ',' ConstDefinition )*
  static boolean ConstDefinitionList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConstDefinitionList")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = ConstDefinition(b, l + 1);
    p = r; // pin = 1
    r = r && ConstDefinitionList_1(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // ( ',' ConstDefinition )*
  private static boolean ConstDefinitionList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConstDefinitionList_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!ConstDefinitionList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ConstDefinitionList_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // ',' ConstDefinition
  private static boolean ConstDefinitionList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConstDefinitionList_1_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && ConstDefinition(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ConstDefinitionList [ ('=' ExpressionList | Type '=' ExpressionList) ]
  public static boolean ConstSpec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConstSpec")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = ConstDefinitionList(b, l + 1);
    p = r; // pin = 1
    r = r && ConstSpec_1(b, l + 1);
    exit_section_(b, l, m, CONST_SPEC, r, p, null);
    return r || p;
  }

  // [ ('=' ExpressionList | Type '=' ExpressionList) ]
  private static boolean ConstSpec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConstSpec_1")) return false;
    ConstSpec_1_0(b, l + 1);
    return true;
  }

  // '=' ExpressionList | Type '=' ExpressionList
  private static boolean ConstSpec_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConstSpec_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ConstSpec_1_0_0(b, l + 1);
    if (!r) r = ConstSpec_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '=' ExpressionList
  private static boolean ConstSpec_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConstSpec_1_0_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, ASSIGN);
    p = r; // pin = 1
    r = r && ExpressionList(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // Type '=' ExpressionList
  private static boolean ConstSpec_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConstSpec_1_0_1")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = Type(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, ASSIGN));
    r = p && ExpressionList(b, l + 1) && r;
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ConstSpec (semi ConstSpec)* semi?
  static boolean ConstSpecs(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConstSpecs")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = ConstSpec(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, ConstSpecs_1(b, l + 1));
    r = p && ConstSpecs_2(b, l + 1) && r;
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (semi ConstSpec)*
  private static boolean ConstSpecs_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConstSpecs_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!ConstSpecs_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ConstSpecs_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // semi ConstSpec
  private static boolean ConstSpecs_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConstSpecs_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = semi(b, l + 1);
    r = r && ConstSpec(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // semi?
  private static boolean ConstSpecs_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConstSpecs_2")) return false;
    semi(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // continue LabelRef?
  public static boolean ContinueStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ContinueStatement")) return false;
    if (!nextTokenIs(b, CONTINUE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, CONTINUE);
    p = r; // pin = 1
    r = r && ContinueStatement_1(b, l + 1);
    exit_section_(b, l, m, CONTINUE_STATEMENT, r, p, null);
    return r || p;
  }

  // LabelRef?
  private static boolean ContinueStatement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ContinueStatement_1")) return false;
    LabelRef(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ConversionStart | '(' ConversionStart
  static boolean ConversionPredicate(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConversionPredicate")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ConversionStart(b, l + 1);
    if (!r) r = ConversionPredicate_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '(' ConversionStart
  private static boolean ConversionPredicate_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConversionPredicate_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && ConversionStart(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '*' | '<-' | '[' | chan | func | interface | map | struct
  static boolean ConversionStart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConversionStart")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, MUL);
    if (!r) r = consumeToken(b, SEND_CHANNEL);
    if (!r) r = consumeToken(b, LBRACK);
    if (!r) r = consumeToken(b, CHAN);
    if (!r) r = consumeToken(b, FUNC);
    if (!r) r = consumeToken(b, INTERFACE);
    if (!r) r = consumeToken(b, MAP);
    if (!r) r = consumeToken(b, STRUCT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '(' Expression ','? ')'
  static boolean ConversionTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConversionTail")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, Expression(b, l + 1, -1));
    r = p && report_error_(b, ConversionTail_2(b, l + 1)) && r;
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // ','?
  private static boolean ConversionTail_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConversionTail_2")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  /* ********************************************************** */
  // defer Expression
  public static boolean DeferStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DeferStatement")) return false;
    if (!nextTokenIs(b, DEFER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, DEFER);
    p = r; // pin = 1
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, l, m, DEFER_STATEMENT, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // [ Key ':' ] Value
  public static boolean Element(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Element")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<element>");
    r = Element_0(b, l + 1);
    r = r && Value(b, l + 1);
    exit_section_(b, l, m, ELEMENT, r, false, null);
    return r;
  }

  // [ Key ':' ]
  private static boolean Element_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Element_0")) return false;
    Element_0_0(b, l + 1);
    return true;
  }

  // Key ':'
  private static boolean Element_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Element_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Key(b, l + 1);
    r = r && consumeToken(b, COLON);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // Expression
  public static boolean ElementIndex(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ElementIndex")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<element index>");
    r = Expression(b, l + 1, -1);
    exit_section_(b, l, m, ELEMENT_INDEX, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // Element ( ',' Element? )*
  static boolean ElementList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ElementList")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = Element(b, l + 1);
    p = r; // pin = 1
    r = r && ElementList_1(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // ( ',' Element? )*
  private static boolean ElementList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ElementList_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!ElementList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ElementList_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // ',' Element?
  private static boolean ElementList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ElementList_1_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && ElementList_1_0_1(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // Element?
  private static boolean ElementList_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ElementList_1_0_1")) return false;
    Element(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // else (IfStatement | Block)
  public static boolean ElseStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ElseStatement")) return false;
    if (!nextTokenIs(b, ELSE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, ELSE);
    p = r; // pin = 1
    r = r && ElseStatement_1(b, l + 1);
    exit_section_(b, l, m, ELSE_STATEMENT, r, p, null);
    return r || p;
  }

  // IfStatement | Block
  private static boolean ElseStatement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ElseStatement_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = IfStatement(b, l + 1);
    if (!r) r = Block(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ExprSwitchCase ':' Statements?
  public static boolean ExprCaseClause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ExprCaseClause")) return false;
    if (!nextTokenIs(b, "<expr case clause>", CASE, DEFAULT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<expr case clause>");
    r = ExprSwitchCase(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && ExprCaseClause_2(b, l + 1);
    exit_section_(b, l, m, EXPR_CASE_CLAUSE, r, false, null);
    return r;
  }

  // Statements?
  private static boolean ExprCaseClause_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ExprCaseClause_2")) return false;
    Statements(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // case ExpressionList | default
  public static boolean ExprSwitchCase(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ExprSwitchCase")) return false;
    if (!nextTokenIs(b, "<expr switch case>", CASE, DEFAULT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<expr switch case>");
    r = ExprSwitchCase_0(b, l + 1);
    if (!r) r = consumeToken(b, DEFAULT);
    exit_section_(b, l, m, EXPR_SWITCH_CASE, r, false, null);
    return r;
  }

  // case ExpressionList
  private static boolean ExprSwitchCase_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ExprSwitchCase_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, CASE);
    p = r; // pin = 1
    r = r && ExpressionList(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // Condition '{' ( ExprCaseClause )* '}'
  public static boolean ExprSwitchStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ExprSwitchStatement")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _LEFT_, "<expr switch statement>");
    r = Condition(b, l + 1);
    r = r && consumeToken(b, LBRACE);
    p = r; // pin = 2
    r = r && report_error_(b, ExprSwitchStatement_2(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, EXPR_SWITCH_STATEMENT, r, p, null);
    return r || p;
  }

  // ( ExprCaseClause )*
  private static boolean ExprSwitchStatement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ExprSwitchStatement_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!ExprSwitchStatement_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ExprSwitchStatement_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // ( ExprCaseClause )
  private static boolean ExprSwitchStatement_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ExprSwitchStatement_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ExprCaseClause(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ExpressionWithRecover (',' (ExpressionWithRecover | &')'))*
  static boolean ExpressionList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ExpressionList")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = ExpressionWithRecover(b, l + 1);
    p = r; // pin = 1
    r = r && ExpressionList_1(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (',' (ExpressionWithRecover | &')'))*
  private static boolean ExpressionList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ExpressionList_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!ExpressionList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ExpressionList_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // ',' (ExpressionWithRecover | &')')
  private static boolean ExpressionList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ExpressionList_1_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && ExpressionList_1_0_1(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // ExpressionWithRecover | &')'
  private static boolean ExpressionList_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ExpressionList_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ExpressionWithRecover(b, l + 1);
    if (!r) r = ExpressionList_1_0_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // &')'
  private static boolean ExpressionList_1_0_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ExpressionList_1_0_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _AND_, null);
    r = consumeToken(b, RPAREN);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // !('!' | '!=' | '%' | '%=' | '&&' | '&' | '&=' | '&^' | '&^=' | '(' | ')' | '*' | '*=' | '+' | '++' | '+=' | ',' | '-' | '--' | '-=' | '.' | '...' | '/' | '/=' | ':' | ';' | '<' | '<-' | '<<' | '<<=' | '<=' | '<NL>' | '=' | '==' | '>' | '>=' | '>>' | '>>=' | '[' | ']' | '^' | '^=' | 'type' | '{' | '|' | '|=' | '||' | '}' | break | case | chan | char | const | continue | decimali | default | defer | else | fallthrough | float | floati | for | func | go | goto | hex | identifier | if | imaginary | int | interface | map | oct | return | rune | select | string | struct | switch | var)
  static boolean ExpressionListRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ExpressionListRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_, null);
    r = !ExpressionListRecover_0(b, l + 1);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  // '!' | '!=' | '%' | '%=' | '&&' | '&' | '&=' | '&^' | '&^=' | '(' | ')' | '*' | '*=' | '+' | '++' | '+=' | ',' | '-' | '--' | '-=' | '.' | '...' | '/' | '/=' | ':' | ';' | '<' | '<-' | '<<' | '<<=' | '<=' | '<NL>' | '=' | '==' | '>' | '>=' | '>>' | '>>=' | '[' | ']' | '^' | '^=' | 'type' | '{' | '|' | '|=' | '||' | '}' | break | case | chan | char | const | continue | decimali | default | defer | else | fallthrough | float | floati | for | func | go | goto | hex | identifier | if | imaginary | int | interface | map | oct | return | rune | select | string | struct | switch | var
  private static boolean ExpressionListRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ExpressionListRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NOT);
    if (!r) r = consumeToken(b, NOT_EQ);
    if (!r) r = consumeToken(b, REMAINDER);
    if (!r) r = consumeToken(b, REMAINDER_ASSIGN);
    if (!r) r = consumeToken(b, COND_AND);
    if (!r) r = consumeToken(b, BIT_AND);
    if (!r) r = consumeToken(b, BIT_AND_ASSIGN);
    if (!r) r = consumeToken(b, BIT_CLEAR);
    if (!r) r = consumeToken(b, BIT_CLEAR_ASSIGN);
    if (!r) r = consumeToken(b, LPAREN);
    if (!r) r = consumeToken(b, RPAREN);
    if (!r) r = consumeToken(b, MUL);
    if (!r) r = consumeToken(b, MUL_ASSIGN);
    if (!r) r = consumeToken(b, PLUS);
    if (!r) r = consumeToken(b, PLUS_PLUS);
    if (!r) r = consumeToken(b, PLUS_ASSIGN);
    if (!r) r = consumeToken(b, COMMA);
    if (!r) r = consumeToken(b, MINUS);
    if (!r) r = consumeToken(b, MINUS_MINUS);
    if (!r) r = consumeToken(b, MINUS_ASSIGN);
    if (!r) r = consumeToken(b, DOT);
    if (!r) r = consumeToken(b, TRIPLE_DOT);
    if (!r) r = consumeToken(b, QUOTIENT);
    if (!r) r = consumeToken(b, QUOTIENT_ASSIGN);
    if (!r) r = consumeToken(b, COLON);
    if (!r) r = consumeToken(b, SEMICOLON);
    if (!r) r = consumeToken(b, LESS);
    if (!r) r = consumeToken(b, SEND_CHANNEL);
    if (!r) r = consumeToken(b, SHIFT_LEFT);
    if (!r) r = consumeToken(b, SHIFT_LEFT_ASSIGN);
    if (!r) r = consumeToken(b, LESS_OR_EQUAL);
    if (!r) r = consumeToken(b, SEMICOLON_SYNTHETIC);
    if (!r) r = consumeToken(b, ASSIGN);
    if (!r) r = consumeToken(b, EQ);
    if (!r) r = consumeToken(b, GREATER);
    if (!r) r = consumeToken(b, GREATER_OR_EQUAL);
    if (!r) r = consumeToken(b, SHIFT_RIGHT);
    if (!r) r = consumeToken(b, SHIFT_RIGHT_ASSIGN);
    if (!r) r = consumeToken(b, LBRACK);
    if (!r) r = consumeToken(b, RBRACK);
    if (!r) r = consumeToken(b, BIT_XOR);
    if (!r) r = consumeToken(b, BIT_XOR_ASSIGN);
    if (!r) r = consumeToken(b, TYPE_);
    if (!r) r = consumeToken(b, LBRACE);
    if (!r) r = consumeToken(b, BIT_OR);
    if (!r) r = consumeToken(b, BIT_OR_ASSIGN);
    if (!r) r = consumeToken(b, COND_OR);
    if (!r) r = consumeToken(b, RBRACE);
    if (!r) r = consumeToken(b, BREAK);
    if (!r) r = consumeToken(b, CASE);
    if (!r) r = consumeToken(b, CHAN);
    if (!r) r = consumeToken(b, CHAR);
    if (!r) r = consumeToken(b, CONST);
    if (!r) r = consumeToken(b, CONTINUE);
    if (!r) r = consumeToken(b, DECIMALI);
    if (!r) r = consumeToken(b, DEFAULT);
    if (!r) r = consumeToken(b, DEFER);
    if (!r) r = consumeToken(b, ELSE);
    if (!r) r = consumeToken(b, FALLTHROUGH);
    if (!r) r = consumeToken(b, FLOAT);
    if (!r) r = consumeToken(b, FLOATI);
    if (!r) r = consumeToken(b, FOR);
    if (!r) r = consumeToken(b, FUNC);
    if (!r) r = consumeToken(b, GO);
    if (!r) r = consumeToken(b, GOTO);
    if (!r) r = consumeToken(b, HEX);
    if (!r) r = consumeToken(b, IDENTIFIER);
    if (!r) r = consumeToken(b, IF);
    if (!r) r = consumeToken(b, IMAGINARY);
    if (!r) r = consumeToken(b, INT);
    if (!r) r = consumeToken(b, INTERFACE);
    if (!r) r = consumeToken(b, MAP);
    if (!r) r = consumeToken(b, OCT);
    if (!r) r = consumeToken(b, RETURN);
    if (!r) r = consumeToken(b, RUNE);
    if (!r) r = consumeToken(b, SELECT);
    if (!r) r = consumeToken(b, STRING);
    if (!r) r = consumeToken(b, STRUCT);
    if (!r) r = consumeToken(b, SWITCH);
    if (!r) r = consumeToken(b, VAR);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // <<enterMode "NO_EMPTY_LITERAL">> (Expression <<exitModeSafe "NO_EMPTY_LITERAL">> | <<exitModeSafe "NO_EMPTY_LITERAL">>)
  static boolean ExpressionNoLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ExpressionNoLiteral")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = enterMode(b, l + 1, "NO_EMPTY_LITERAL");
    r = r && ExpressionNoLiteral_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // Expression <<exitModeSafe "NO_EMPTY_LITERAL">> | <<exitModeSafe "NO_EMPTY_LITERAL">>
  private static boolean ExpressionNoLiteral_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ExpressionNoLiteral_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ExpressionNoLiteral_1_0(b, l + 1);
    if (!r) r = exitModeSafe(b, l + 1, "NO_EMPTY_LITERAL");
    exit_section_(b, m, null, r);
    return r;
  }

  // Expression <<exitModeSafe "NO_EMPTY_LITERAL">>
  private static boolean ExpressionNoLiteral_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ExpressionNoLiteral_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Expression(b, l + 1, -1);
    r = r && exitModeSafe(b, l + 1, "NO_EMPTY_LITERAL");
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // Expression
  static boolean ExpressionWithRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ExpressionWithRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = Expression(b, l + 1, -1);
    exit_section_(b, l, m, null, r, false, ExpressionListRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // ExpressionList '=' | VarDefinitionList ':='
  static boolean ExpressionsOrVariables(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ExpressionsOrVariables")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ExpressionsOrVariables_0(b, l + 1);
    if (!r) r = ExpressionsOrVariables_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ExpressionList '='
  private static boolean ExpressionsOrVariables_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ExpressionsOrVariables_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ExpressionList(b, l + 1);
    r = r && consumeToken(b, ASSIGN);
    exit_section_(b, m, null, r);
    return r;
  }

  // VarDefinitionList ':='
  private static boolean ExpressionsOrVariables_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ExpressionsOrVariables_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = VarDefinitionList(b, l + 1);
    r = r && consumeToken(b, VAR_ASSIGN);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // fallthrough
  public static boolean FallthroughStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FallthroughStatement")) return false;
    if (!nextTokenIs(b, FALLTHROUGH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, FALLTHROUGH);
    exit_section_(b, m, FALLTHROUGH_STATEMENT, r);
    return r;
  }

  /* ********************************************************** */
  // (FieldDefinitionList Type | AnonymousFieldDefinition) Tag?
  public static boolean FieldDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FieldDeclaration")) return false;
    if (!nextTokenIs(b, "<field declaration>", MUL, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<field declaration>");
    r = FieldDeclaration_0(b, l + 1);
    r = r && FieldDeclaration_1(b, l + 1);
    exit_section_(b, l, m, FIELD_DECLARATION, r, false, null);
    return r;
  }

  // FieldDefinitionList Type | AnonymousFieldDefinition
  private static boolean FieldDeclaration_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FieldDeclaration_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = FieldDeclaration_0_0(b, l + 1);
    if (!r) r = AnonymousFieldDefinition(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // FieldDefinitionList Type
  private static boolean FieldDeclaration_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FieldDeclaration_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = FieldDefinitionList(b, l + 1);
    r = r && Type(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // Tag?
  private static boolean FieldDeclaration_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FieldDeclaration_1")) return false;
    Tag(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // identifier
  public static boolean FieldDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FieldDefinition")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, FIELD_DEFINITION, r);
    return r;
  }

  /* ********************************************************** */
  // FieldDefinition (',' FieldDefinition)*
  static boolean FieldDefinitionList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FieldDefinitionList")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = FieldDefinition(b, l + 1);
    p = r; // pin = 1
    r = r && FieldDefinitionList_1(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (',' FieldDefinition)*
  private static boolean FieldDefinitionList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FieldDefinitionList_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!FieldDefinitionList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "FieldDefinitionList_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // ',' FieldDefinition
  private static boolean FieldDefinitionList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FieldDefinitionList_1_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && FieldDefinition(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // identifier
  public static boolean FieldName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FieldName")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, FIELD_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // FieldDeclaration (semi FieldDeclaration)* semi?
  static boolean Fields(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Fields")) return false;
    if (!nextTokenIs(b, "", MUL, IDENTIFIER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = FieldDeclaration(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, Fields_1(b, l + 1));
    r = p && Fields_2(b, l + 1) && r;
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (semi FieldDeclaration)*
  private static boolean Fields_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Fields_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!Fields_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "Fields_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // semi FieldDeclaration
  private static boolean Fields_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Fields_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = semi(b, l + 1);
    r = r && FieldDeclaration(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // semi?
  private static boolean Fields_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Fields_2")) return false;
    semi(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // PackageClause semi ImportList TopLevelDeclaration*
  static boolean File(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "File")) return false;
    if (!nextTokenIs(b, PACKAGE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = PackageClause(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, semi(b, l + 1));
    r = p && report_error_(b, ImportList(b, l + 1)) && r;
    r = p && File_3(b, l + 1) && r;
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // TopLevelDeclaration*
  private static boolean File_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "File_3")) return false;
    int c = current_position_(b);
    while (true) {
      if (!TopLevelDeclaration(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "File_3", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // SimpleStatement? ';' Expression? ';' SimpleStatement?
  public static boolean ForClause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ForClause")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<for clause>");
    r = ForClause_0(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    r = r && ForClause_2(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    r = r && ForClause_4(b, l + 1);
    exit_section_(b, l, m, FOR_CLAUSE, r, false, null);
    return r;
  }

  // SimpleStatement?
  private static boolean ForClause_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ForClause_0")) return false;
    SimpleStatement(b, l + 1);
    return true;
  }

  // Expression?
  private static boolean ForClause_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ForClause_2")) return false;
    Expression(b, l + 1, -1);
    return true;
  }

  // SimpleStatement?
  private static boolean ForClause_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ForClause_4")) return false;
    SimpleStatement(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ForClause | RangeClause
  static boolean ForOrRangeClause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ForOrRangeClause")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ForClause(b, l + 1);
    if (!r) r = RangeClause(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // for (ForOrRangeClause Block | Block | ExpressionNoLiteral Block)
  public static boolean ForStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ForStatement")) return false;
    if (!nextTokenIs(b, FOR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, FOR);
    p = r; // pin = for|ForOrRangeClause
    r = r && ForStatement_1(b, l + 1);
    exit_section_(b, l, m, FOR_STATEMENT, r, p, null);
    return r || p;
  }

  // ForOrRangeClause Block | Block | ExpressionNoLiteral Block
  private static boolean ForStatement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ForStatement_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ForStatement_1_0(b, l + 1);
    if (!r) r = Block(b, l + 1);
    if (!r) r = ForStatement_1_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ForOrRangeClause Block
  private static boolean ForStatement_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ForStatement_1_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = ForOrRangeClause(b, l + 1);
    p = r; // pin = for|ForOrRangeClause
    r = r && Block(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // ExpressionNoLiteral Block
  private static boolean ForStatement_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ForStatement_1_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ExpressionNoLiteral(b, l + 1);
    r = r && Block(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // func identifier Signature Block?
  public static boolean FunctionDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionDeclaration")) return false;
    if (!nextTokenIs(b, FUNC)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokens(b, 2, FUNC, IDENTIFIER);
    p = r; // pin = 2
    r = r && report_error_(b, Signature(b, l + 1));
    r = p && FunctionDeclaration_3(b, l + 1) && r;
    exit_section_(b, l, m, FUNCTION_DECLARATION, r, p, null);
    return r || p;
  }

  // Block?
  private static boolean FunctionDeclaration_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionDeclaration_3")) return false;
    Block(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // func Signature
  public static boolean FunctionType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionType")) return false;
    if (!nextTokenIs(b, FUNC)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, FUNC);
    p = r; // pin = 1
    r = r && Signature(b, l + 1);
    exit_section_(b, l, m, FUNCTION_TYPE, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // go Expression
  public static boolean GoStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GoStatement")) return false;
    if (!nextTokenIs(b, GO)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, GO);
    p = r; // pin = 1
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, l, m, GO_STATEMENT, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // goto LabelRef
  public static boolean GotoStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GotoStatement")) return false;
    if (!nextTokenIs(b, GOTO)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, GOTO);
    p = r; // pin = 1
    r = r && LabelRef(b, l + 1);
    exit_section_(b, l, m, GOTO_STATEMENT, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // if Condition Block ElseStatement?
  public static boolean IfStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "IfStatement")) return false;
    if (!nextTokenIs(b, IF)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, IF);
    p = r; // pin = 1
    r = r && report_error_(b, Condition(b, l + 1));
    r = p && report_error_(b, Block(b, l + 1)) && r;
    r = p && IfStatement_3(b, l + 1) && r;
    exit_section_(b, l, m, IF_STATEMENT, r, p, null);
    return r || p;
  }

  // ElseStatement?
  private static boolean IfStatement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "IfStatement_3")) return false;
    ElseStatement(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // import ( ImportSpec | '(' ImportSpecs? ')' )
  public static boolean ImportDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportDeclaration")) return false;
    if (!nextTokenIs(b, IMPORT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, IMPORT);
    p = r; // pin = 1
    r = r && ImportDeclaration_1(b, l + 1);
    exit_section_(b, l, m, IMPORT_DECLARATION, r, p, null);
    return r || p;
  }

  // ImportSpec | '(' ImportSpecs? ')'
  private static boolean ImportDeclaration_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportDeclaration_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ImportSpec(b, l + 1);
    if (!r) r = ImportDeclaration_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '(' ImportSpecs? ')'
  private static boolean ImportDeclaration_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportDeclaration_1_1")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, ImportDeclaration_1_1_1(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // ImportSpecs?
  private static boolean ImportDeclaration_1_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportDeclaration_1_1_1")) return false;
    ImportSpecs(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // (ImportDeclaration semi)+|<<emptyImportList>>
  public static boolean ImportList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportList")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<import list>");
    r = ImportList_0(b, l + 1);
    if (!r) r = emptyImportList(b, l + 1);
    exit_section_(b, l, m, IMPORT_LIST, r, false, null);
    return r;
  }

  // (ImportDeclaration semi)+
  private static boolean ImportList_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportList_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ImportList_0_0(b, l + 1);
    int c = current_position_(b);
    while (r) {
      if (!ImportList_0_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ImportList_0", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // ImportDeclaration semi
  private static boolean ImportList_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportList_0_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = ImportDeclaration(b, l + 1);
    p = r; // pin = 1
    r = r && semi(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // [ '.' | identifier ] ImportString
  public static boolean ImportSpec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportSpec")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<import spec>");
    r = ImportSpec_0(b, l + 1);
    r = r && ImportString(b, l + 1);
    exit_section_(b, l, m, IMPORT_SPEC, r, false, null);
    return r;
  }

  // [ '.' | identifier ]
  private static boolean ImportSpec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportSpec_0")) return false;
    ImportSpec_0_0(b, l + 1);
    return true;
  }

  // '.' | identifier
  private static boolean ImportSpec_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportSpec_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOT);
    if (!r) r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ImportSpec (semi ImportSpec)* semi?
  static boolean ImportSpecs(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportSpecs")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = ImportSpec(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, ImportSpecs_1(b, l + 1));
    r = p && ImportSpecs_2(b, l + 1) && r;
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (semi ImportSpec)*
  private static boolean ImportSpecs_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportSpecs_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!ImportSpecs_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ImportSpecs_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // semi ImportSpec
  private static boolean ImportSpecs_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportSpecs_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = semi(b, l + 1);
    r = r && ImportSpec(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // semi?
  private static boolean ImportSpecs_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportSpecs_2")) return false;
    semi(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // string
  public static boolean ImportString(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ImportString")) return false;
    if (!nextTokenIs(b, STRING)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, STRING);
    exit_section_(b, m, IMPORT_STRING, r);
    return r;
  }

  /* ********************************************************** */
  // Expression
  static boolean IndexExprBody(PsiBuilder b, int l) {
    return Expression(b, l + 1, -1);
  }

  /* ********************************************************** */
  // interface '{' MethodSpecs? '}'
  public static boolean InterfaceType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "InterfaceType")) return false;
    if (!nextTokenIs(b, INTERFACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, INTERFACE);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, LBRACE));
    r = p && report_error_(b, InterfaceType_2(b, l + 1)) && r;
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, INTERFACE_TYPE, r, p, null);
    return r || p;
  }

  // MethodSpecs?
  private static boolean InterfaceType_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "InterfaceType_2")) return false;
    MethodSpecs(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // FieldName &':' | ElementIndex
  public static boolean Key(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Key")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<key>");
    r = Key_0(b, l + 1);
    if (!r) r = ElementIndex(b, l + 1);
    exit_section_(b, l, m, KEY, r, false, null);
    return r;
  }

  // FieldName &':'
  private static boolean Key_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Key_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = FieldName(b, l + 1);
    r = r && Key_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // &':'
  private static boolean Key_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Key_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _AND_, null);
    r = consumeToken(b, COLON);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // identifier
  public static boolean LabelDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LabelDefinition")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, LABEL_DEFINITION, r);
    return r;
  }

  /* ********************************************************** */
  // identifier
  public static boolean LabelRef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LabelRef")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, LABEL_REF, r);
    return r;
  }

  /* ********************************************************** */
  // LabelDefinition ':' Statement
  public static boolean LabeledStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LabeledStatement")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = LabelDefinition(b, l + 1);
    r = r && consumeToken(b, COLON);
    p = r; // pin = 2
    r = r && Statement(b, l + 1);
    exit_section_(b, l, m, LABELED_STATEMENT, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // <<isModeOff "NO_EMPTY_LITERAL">> '{' '}' | '{' ElementList '}'
  public static boolean LiteralValue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LiteralValue")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<literal value>");
    r = LiteralValue_0(b, l + 1);
    if (!r) r = LiteralValue_1(b, l + 1);
    exit_section_(b, l, m, LITERAL_VALUE, r, false, null);
    return r;
  }

  // <<isModeOff "NO_EMPTY_LITERAL">> '{' '}'
  private static boolean LiteralValue_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LiteralValue_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = isModeOff(b, l + 1, "NO_EMPTY_LITERAL");
    r = r && consumeToken(b, LBRACE);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, null, r);
    return r;
  }

  // '{' ElementList '}'
  private static boolean LiteralValue_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LiteralValue_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACE);
    r = r && ElementList(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // map '[' Type ']' Type
  public static boolean MapType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MapType")) return false;
    if (!nextTokenIs(b, MAP)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, MAP);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, LBRACK));
    r = p && report_error_(b, Type(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, RBRACK)) && r;
    r = p && Type(b, l + 1) && r;
    exit_section_(b, l, m, MAP_TYPE, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // func Receiver identifier Signature Block?
  public static boolean MethodDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodDeclaration")) return false;
    if (!nextTokenIs(b, FUNC)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, FUNC);
    r = r && Receiver(b, l + 1);
    p = r; // pin = 2
    r = r && report_error_(b, consumeToken(b, IDENTIFIER));
    r = p && report_error_(b, Signature(b, l + 1)) && r;
    r = p && MethodDeclaration_4(b, l + 1) && r;
    exit_section_(b, l, m, METHOD_DECLARATION, r, p, null);
    return r || p;
  }

  // Block?
  private static boolean MethodDeclaration_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodDeclaration_4")) return false;
    Block(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // TypeName &(!'(') | identifier Signature
  public static boolean MethodSpec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodSpec")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = MethodSpec_0(b, l + 1);
    if (!r) r = MethodSpec_1(b, l + 1);
    exit_section_(b, m, METHOD_SPEC, r);
    return r;
  }

  // TypeName &(!'(')
  private static boolean MethodSpec_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodSpec_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = TypeName(b, l + 1);
    r = r && MethodSpec_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // &(!'(')
  private static boolean MethodSpec_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodSpec_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _AND_, null);
    r = MethodSpec_0_1_0(b, l + 1);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  // !'('
  private static boolean MethodSpec_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodSpec_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_, null);
    r = !consumeToken(b, LPAREN);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  // identifier Signature
  private static boolean MethodSpec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodSpec_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    r = r && Signature(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // MethodSpec (semi MethodSpec)* semi?
  static boolean MethodSpecs(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodSpecs")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = MethodSpec(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, MethodSpecs_1(b, l + 1));
    r = p && MethodSpecs_2(b, l + 1) && r;
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (semi MethodSpec)*
  private static boolean MethodSpecs_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodSpecs_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!MethodSpecs_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "MethodSpecs_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // semi MethodSpec
  private static boolean MethodSpecs_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodSpecs_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = semi(b, l + 1);
    r = r && MethodSpec(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // semi?
  private static boolean MethodSpecs_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodSpecs_2")) return false;
    semi(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ConstDeclaration
  //   | TypeDeclaration
  //   | VarDeclaration
  //   | FunctionDeclaration
  //   | MethodDeclaration
  static boolean OneOfDeclarations(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OneOfDeclarations")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ConstDeclaration(b, l + 1);
    if (!r) r = TypeDeclaration(b, l + 1);
    if (!r) r = VarDeclaration(b, l + 1);
    if (!r) r = FunctionDeclaration(b, l + 1);
    if (!r) r = MethodDeclaration(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // package identifier
  public static boolean PackageClause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PackageClause")) return false;
    if (!nextTokenIs(b, PACKAGE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokens(b, 1, PACKAGE, IDENTIFIER);
    p = r; // pin = 1
    exit_section_(b, l, m, PACKAGE_CLAUSE, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // identifier
  public static boolean ParamDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ParamDefinition")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, PARAM_DEFINITION, r);
    return r;
  }

  /* ********************************************************** */
  // ParamDefinition &(!('.' | ')')) (',' ParamDefinition)*
  static boolean ParamDefinitionListNoPin(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ParamDefinitionListNoPin")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ParamDefinition(b, l + 1);
    r = r && ParamDefinitionListNoPin_1(b, l + 1);
    r = r && ParamDefinitionListNoPin_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // &(!('.' | ')'))
  private static boolean ParamDefinitionListNoPin_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ParamDefinitionListNoPin_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _AND_, null);
    r = ParamDefinitionListNoPin_1_0(b, l + 1);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  // !('.' | ')')
  private static boolean ParamDefinitionListNoPin_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ParamDefinitionListNoPin_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_, null);
    r = !ParamDefinitionListNoPin_1_0_0(b, l + 1);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  // '.' | ')'
  private static boolean ParamDefinitionListNoPin_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ParamDefinitionListNoPin_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOT);
    if (!r) r = consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' ParamDefinition)*
  private static boolean ParamDefinitionListNoPin_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ParamDefinitionListNoPin_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!ParamDefinitionListNoPin_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ParamDefinitionListNoPin_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // ',' ParamDefinition
  private static boolean ParamDefinitionListNoPin_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ParamDefinitionListNoPin_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && ParamDefinition(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ParamDefinitionListNoPin? '...'? Type | Type
  public static boolean ParameterDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ParameterDeclaration")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<parameter declaration>");
    r = ParameterDeclaration_0(b, l + 1);
    if (!r) r = Type(b, l + 1);
    exit_section_(b, l, m, PARAMETER_DECLARATION, r, false, null);
    return r;
  }

  // ParamDefinitionListNoPin? '...'? Type
  private static boolean ParameterDeclaration_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ParameterDeclaration_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ParameterDeclaration_0_0(b, l + 1);
    r = r && ParameterDeclaration_0_1(b, l + 1);
    r = r && Type(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ParamDefinitionListNoPin?
  private static boolean ParameterDeclaration_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ParameterDeclaration_0_0")) return false;
    ParamDefinitionListNoPin(b, l + 1);
    return true;
  }

  // '...'?
  private static boolean ParameterDeclaration_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ParameterDeclaration_0_1")) return false;
    consumeToken(b, TRIPLE_DOT);
    return true;
  }

  /* ********************************************************** */
  // ParameterDeclaration (',' (ParameterDeclaration | &')'))*
  static boolean ParameterList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ParameterList")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = ParameterDeclaration(b, l + 1);
    p = r; // pin = 1
    r = r && ParameterList_1(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (',' (ParameterDeclaration | &')'))*
  private static boolean ParameterList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ParameterList_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!ParameterList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ParameterList_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // ',' (ParameterDeclaration | &')')
  private static boolean ParameterList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ParameterList_1_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && ParameterList_1_0_1(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // ParameterDeclaration | &')'
  private static boolean ParameterList_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ParameterList_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ParameterDeclaration(b, l + 1);
    if (!r) r = ParameterList_1_0_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // &')'
  private static boolean ParameterList_1_0_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ParameterList_1_0_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _AND_, null);
    r = consumeToken(b, RPAREN);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '(' [ (ParameterList ','?| TypeListNoPin) ] ')'
  public static boolean Parameters(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Parameters")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, Parameters_1(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, PARAMETERS, r, p, null);
    return r || p;
  }

  // [ (ParameterList ','?| TypeListNoPin) ]
  private static boolean Parameters_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Parameters_1")) return false;
    Parameters_1_0(b, l + 1);
    return true;
  }

  // ParameterList ','?| TypeListNoPin
  private static boolean Parameters_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Parameters_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Parameters_1_0_0(b, l + 1);
    if (!r) r = TypeListNoPin(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ParameterList ','?
  private static boolean Parameters_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Parameters_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ParameterList(b, l + 1);
    r = r && Parameters_1_0_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean Parameters_1_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Parameters_1_0_0_1")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  /* ********************************************************** */
  // '*' Type
  public static boolean PointerType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PointerType")) return false;
    if (!nextTokenIs(b, MUL)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, MUL);
    p = r; // pin = 1
    r = r && Type(b, l + 1);
    exit_section_(b, l, m, POINTER_TYPE, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '.' identifier
  public static boolean QualifiedReferenceExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "QualifiedReferenceExpression")) return false;
    if (!nextTokenIs(b, DOT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, null);
    r = consumeToken(b, DOT);
    r = r && consumeToken(b, IDENTIFIER);
    exit_section_(b, l, m, REFERENCE_EXPRESSION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '.' identifier
  public static boolean QualifiedTypeReferenceExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "QualifiedTypeReferenceExpression")) return false;
    if (!nextTokenIs(b, DOT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, null);
    r = consumeToken(b, DOT);
    r = r && consumeToken(b, IDENTIFIER);
    exit_section_(b, l, m, TYPE_REFERENCE_EXPRESSION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ExpressionsOrVariables? range ExpressionNoLiteral
  public static boolean RangeClause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "RangeClause")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, "<range clause>");
    r = RangeClause_0(b, l + 1);
    r = r && consumeToken(b, RANGE);
    p = r; // pin = 2
    r = r && ExpressionNoLiteral(b, l + 1);
    exit_section_(b, l, m, RANGE_CLAUSE, r, p, null);
    return r || p;
  }

  // ExpressionsOrVariables?
  private static boolean RangeClause_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "RangeClause_0")) return false;
    ExpressionsOrVariables(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '(' (identifier ReceiverTail | ReceiverTail) ')'
  public static boolean Receiver(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Receiver")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, Receiver_1(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, RECEIVER, r, p, null);
    return r || p;
  }

  // identifier ReceiverTail | ReceiverTail
  private static boolean Receiver_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Receiver_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Receiver_1_0(b, l + 1);
    if (!r) r = ReceiverTail(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // identifier ReceiverTail
  private static boolean Receiver_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Receiver_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    r = r && ReceiverTail(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // TypeReferenceExpression
  public static boolean ReceiverResultType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReceiverResultType")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = TypeReferenceExpression(b, l + 1);
    exit_section_(b, m, TYPE, r);
    return r;
  }

  /* ********************************************************** */
  // '*'? ReceiverResultType
  static boolean ReceiverTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReceiverTail")) return false;
    if (!nextTokenIs(b, "", MUL, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ReceiverTail_0(b, l + 1);
    r = r && ReceiverResultType(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '*'?
  private static boolean ReceiverTail_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReceiverTail_0")) return false;
    consumeToken(b, MUL);
    return true;
  }

  /* ********************************************************** */
  // TypeName | '(' '*' TypeName ')' | '(' ReceiverType ')'
  public static boolean ReceiverType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReceiverType")) return false;
    if (!nextTokenIs(b, "<receiver type>", LPAREN, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<receiver type>");
    r = TypeName(b, l + 1);
    if (!r) r = ReceiverType_1(b, l + 1);
    if (!r) r = ReceiverType_2(b, l + 1);
    exit_section_(b, l, m, RECEIVER_TYPE, r, false, null);
    return r;
  }

  // '(' '*' TypeName ')'
  private static boolean ReceiverType_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReceiverType_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && consumeToken(b, MUL);
    r = r && TypeName(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // '(' ReceiverType ')'
  private static boolean ReceiverType_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReceiverType_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && ReceiverType(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ExpressionsOrVariables? Expression
  public static boolean RecvStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "RecvStatement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<recv statement>");
    r = RecvStatement_0(b, l + 1);
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, l, m, RECV_STATEMENT, r, false, null);
    return r;
  }

  // ExpressionsOrVariables?
  private static boolean RecvStatement_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "RecvStatement_0")) return false;
    ExpressionsOrVariables(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // identifier
  public static boolean ReferenceExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReferenceExpression")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, REFERENCE_EXPRESSION, r);
    return r;
  }

  /* ********************************************************** */
  // '(' TypeListNoPin ')' | Type | Parameters
  public static boolean Result(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Result")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<result>");
    r = Result_0(b, l + 1);
    if (!r) r = Type(b, l + 1);
    if (!r) r = Parameters(b, l + 1);
    exit_section_(b, l, m, RESULT, r, false, null);
    return r;
  }

  // '(' TypeListNoPin ')'
  private static boolean Result_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Result_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && TypeListNoPin(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // return ExpressionList?
  public static boolean ReturnStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReturnStatement")) return false;
    if (!nextTokenIs(b, RETURN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, RETURN);
    p = r; // pin = 1
    r = r && ReturnStatement_1(b, l + 1);
    exit_section_(b, l, m, RETURN_STATEMENT, r, p, null);
    return r || p;
  }

  // ExpressionList?
  private static boolean ReturnStatement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReturnStatement_1")) return false;
    ExpressionList(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // select '{' ( CommClause )* '}'
  public static boolean SelectStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SelectStatement")) return false;
    if (!nextTokenIs(b, SELECT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, SELECT);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, LBRACE));
    r = p && report_error_(b, SelectStatement_2(b, l + 1)) && r;
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, SELECT_STATEMENT, r, p, null);
    return r || p;
  }

  // ( CommClause )*
  private static boolean SelectStatement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SelectStatement_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!SelectStatement_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "SelectStatement_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // ( CommClause )
  private static boolean SelectStatement_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SelectStatement_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = CommClause(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // Expression '<-' Expression
  public static boolean SendStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SendStatement")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, "<send statement>");
    r = Expression(b, l + 1, -1);
    r = r && consumeToken(b, SEND_CHANNEL);
    p = r; // pin = 2
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, l, m, SEND_STATEMENT, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // VarDefinitionList ':=' ExpressionList
  public static boolean ShortVarDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ShortVarDeclaration")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = VarDefinitionList(b, l + 1);
    r = r && consumeToken(b, VAR_ASSIGN);
    p = r; // pin = 2
    r = r && ExpressionList(b, l + 1);
    exit_section_(b, l, m, SHORT_VAR_DECLARATION, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // Parameters Result?
  public static boolean Signature(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Signature")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = Parameters(b, l + 1);
    p = r; // pin = 1
    r = r && Signature_1(b, l + 1);
    exit_section_(b, l, m, SIGNATURE, r, p, null);
    return r || p;
  }

  // Result?
  private static boolean Signature_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Signature_1")) return false;
    Result(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // AssignmentStatement
  //   | SendStatement
  //   | ShortVarDeclaration
  //   | Expression ['++' | '--']
  public static boolean SimpleStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SimpleStatement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, "<simple statement>");
    r = AssignmentStatement(b, l + 1);
    if (!r) r = SendStatement(b, l + 1);
    if (!r) r = ShortVarDeclaration(b, l + 1);
    if (!r) r = SimpleStatement_3(b, l + 1);
    exit_section_(b, l, m, SIMPLE_STATEMENT, r, false, null);
    return r;
  }

  // Expression ['++' | '--']
  private static boolean SimpleStatement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SimpleStatement_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Expression(b, l + 1, -1);
    r = r && SimpleStatement_3_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ['++' | '--']
  private static boolean SimpleStatement_3_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SimpleStatement_3_1")) return false;
    SimpleStatement_3_1_0(b, l + 1);
    return true;
  }

  // '++' | '--'
  private static boolean SimpleStatement_3_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SimpleStatement_3_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PLUS_PLUS);
    if (!r) r = consumeToken(b, MINUS_MINUS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // [SimpleStatement ';'?]
  static boolean SimpleStatementOpt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SimpleStatementOpt")) return false;
    SimpleStatementOpt_0(b, l + 1);
    return true;
  }

  // SimpleStatement ';'?
  private static boolean SimpleStatementOpt_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SimpleStatementOpt_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = SimpleStatement(b, l + 1);
    r = r && SimpleStatementOpt_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ';'?
  private static boolean SimpleStatementOpt_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SimpleStatementOpt_0_1")) return false;
    consumeToken(b, SEMICOLON);
    return true;
  }

  /* ********************************************************** */
  // (Expression? ':' Expression ':' Expression) | (Expression? ':' Expression?)
  static boolean SliceExprBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SliceExprBody")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = SliceExprBody_0(b, l + 1);
    if (!r) r = SliceExprBody_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // Expression? ':' Expression ':' Expression
  private static boolean SliceExprBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SliceExprBody_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = SliceExprBody_0_0(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && Expression(b, l + 1, -1);
    r = r && consumeToken(b, COLON);
    r = r && Expression(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  // Expression?
  private static boolean SliceExprBody_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SliceExprBody_0_0")) return false;
    Expression(b, l + 1, -1);
    return true;
  }

  // Expression? ':' Expression?
  private static boolean SliceExprBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SliceExprBody_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = SliceExprBody_1_0(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && SliceExprBody_1_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // Expression?
  private static boolean SliceExprBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SliceExprBody_1_0")) return false;
    Expression(b, l + 1, -1);
    return true;
  }

  // Expression?
  private static boolean SliceExprBody_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SliceExprBody_1_2")) return false;
    Expression(b, l + 1, -1);
    return true;
  }

  /* ********************************************************** */
  // ConstDeclaration
  //   | TypeDeclaration
  //   | VarDeclaration
  //   | LabeledStatement
  //   | SimpleStatement
  //   | GoStatement
  //   | ReturnStatement
  //   | BreakStatement
  //   | ContinueStatement
  //   | GotoStatement
  //   | FallthroughStatement
  //   | Block
  //   | IfStatement
  //   | SwitchStatement
  //   | SelectStatement
  //   | ForStatement
  //   | DeferStatement
  public static boolean Statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, "<statement>");
    r = ConstDeclaration(b, l + 1);
    if (!r) r = TypeDeclaration(b, l + 1);
    if (!r) r = VarDeclaration(b, l + 1);
    if (!r) r = LabeledStatement(b, l + 1);
    if (!r) r = SimpleStatement(b, l + 1);
    if (!r) r = GoStatement(b, l + 1);
    if (!r) r = ReturnStatement(b, l + 1);
    if (!r) r = BreakStatement(b, l + 1);
    if (!r) r = ContinueStatement(b, l + 1);
    if (!r) r = GotoStatement(b, l + 1);
    if (!r) r = FallthroughStatement(b, l + 1);
    if (!r) r = Block(b, l + 1);
    if (!r) r = IfStatement(b, l + 1);
    if (!r) r = SwitchStatement(b, l + 1);
    if (!r) r = SelectStatement(b, l + 1);
    if (!r) r = ForStatement(b, l + 1);
    if (!r) r = DeferStatement(b, l + 1);
    exit_section_(b, l, m, STATEMENT, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // !('!' | '!=' | '%' | '%=' | '&&' | '&' | '&=' | '&^' | '&^=' | '(' | '*' | '*=' | '+' | '++' | '+=' | '-' | '--' | '-=' | '.' | '...' | '/' | '/=' | ':' | ';' | '<' | '<-' | '<<' | '<<=' | '<=' | '=' | '==' | '>' | '>=' | '>>' | '>>=' | '[' | '^' | '^=' | 'type' | '{' | '|' | '|=' | '||' | '}' | break | case | chan | char | const | continue | decimali | default | defer | else | fallthrough | float | floati | for | func | go | goto | hex | identifier | if | imaginary | int | interface | map | oct | return | rune | select | string | struct | switch | var)
  static boolean StatementRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StatementRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_, null);
    r = !StatementRecover_0(b, l + 1);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  // '!' | '!=' | '%' | '%=' | '&&' | '&' | '&=' | '&^' | '&^=' | '(' | '*' | '*=' | '+' | '++' | '+=' | '-' | '--' | '-=' | '.' | '...' | '/' | '/=' | ':' | ';' | '<' | '<-' | '<<' | '<<=' | '<=' | '=' | '==' | '>' | '>=' | '>>' | '>>=' | '[' | '^' | '^=' | 'type' | '{' | '|' | '|=' | '||' | '}' | break | case | chan | char | const | continue | decimali | default | defer | else | fallthrough | float | floati | for | func | go | goto | hex | identifier | if | imaginary | int | interface | map | oct | return | rune | select | string | struct | switch | var
  private static boolean StatementRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StatementRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NOT);
    if (!r) r = consumeToken(b, NOT_EQ);
    if (!r) r = consumeToken(b, REMAINDER);
    if (!r) r = consumeToken(b, REMAINDER_ASSIGN);
    if (!r) r = consumeToken(b, COND_AND);
    if (!r) r = consumeToken(b, BIT_AND);
    if (!r) r = consumeToken(b, BIT_AND_ASSIGN);
    if (!r) r = consumeToken(b, BIT_CLEAR);
    if (!r) r = consumeToken(b, BIT_CLEAR_ASSIGN);
    if (!r) r = consumeToken(b, LPAREN);
    if (!r) r = consumeToken(b, MUL);
    if (!r) r = consumeToken(b, MUL_ASSIGN);
    if (!r) r = consumeToken(b, PLUS);
    if (!r) r = consumeToken(b, PLUS_PLUS);
    if (!r) r = consumeToken(b, PLUS_ASSIGN);
    if (!r) r = consumeToken(b, MINUS);
    if (!r) r = consumeToken(b, MINUS_MINUS);
    if (!r) r = consumeToken(b, MINUS_ASSIGN);
    if (!r) r = consumeToken(b, DOT);
    if (!r) r = consumeToken(b, TRIPLE_DOT);
    if (!r) r = consumeToken(b, QUOTIENT);
    if (!r) r = consumeToken(b, QUOTIENT_ASSIGN);
    if (!r) r = consumeToken(b, COLON);
    if (!r) r = consumeToken(b, SEMICOLON);
    if (!r) r = consumeToken(b, LESS);
    if (!r) r = consumeToken(b, SEND_CHANNEL);
    if (!r) r = consumeToken(b, SHIFT_LEFT);
    if (!r) r = consumeToken(b, SHIFT_LEFT_ASSIGN);
    if (!r) r = consumeToken(b, LESS_OR_EQUAL);
    if (!r) r = consumeToken(b, ASSIGN);
    if (!r) r = consumeToken(b, EQ);
    if (!r) r = consumeToken(b, GREATER);
    if (!r) r = consumeToken(b, GREATER_OR_EQUAL);
    if (!r) r = consumeToken(b, SHIFT_RIGHT);
    if (!r) r = consumeToken(b, SHIFT_RIGHT_ASSIGN);
    if (!r) r = consumeToken(b, LBRACK);
    if (!r) r = consumeToken(b, BIT_XOR);
    if (!r) r = consumeToken(b, BIT_XOR_ASSIGN);
    if (!r) r = consumeToken(b, TYPE_);
    if (!r) r = consumeToken(b, LBRACE);
    if (!r) r = consumeToken(b, BIT_OR);
    if (!r) r = consumeToken(b, BIT_OR_ASSIGN);
    if (!r) r = consumeToken(b, COND_OR);
    if (!r) r = consumeToken(b, RBRACE);
    if (!r) r = consumeToken(b, BREAK);
    if (!r) r = consumeToken(b, CASE);
    if (!r) r = consumeToken(b, CHAN);
    if (!r) r = consumeToken(b, CHAR);
    if (!r) r = consumeToken(b, CONST);
    if (!r) r = consumeToken(b, CONTINUE);
    if (!r) r = consumeToken(b, DECIMALI);
    if (!r) r = consumeToken(b, DEFAULT);
    if (!r) r = consumeToken(b, DEFER);
    if (!r) r = consumeToken(b, ELSE);
    if (!r) r = consumeToken(b, FALLTHROUGH);
    if (!r) r = consumeToken(b, FLOAT);
    if (!r) r = consumeToken(b, FLOATI);
    if (!r) r = consumeToken(b, FOR);
    if (!r) r = consumeToken(b, FUNC);
    if (!r) r = consumeToken(b, GO);
    if (!r) r = consumeToken(b, GOTO);
    if (!r) r = consumeToken(b, HEX);
    if (!r) r = consumeToken(b, IDENTIFIER);
    if (!r) r = consumeToken(b, IF);
    if (!r) r = consumeToken(b, IMAGINARY);
    if (!r) r = consumeToken(b, INT);
    if (!r) r = consumeToken(b, INTERFACE);
    if (!r) r = consumeToken(b, MAP);
    if (!r) r = consumeToken(b, OCT);
    if (!r) r = consumeToken(b, RETURN);
    if (!r) r = consumeToken(b, RUNE);
    if (!r) r = consumeToken(b, SELECT);
    if (!r) r = consumeToken(b, STRING);
    if (!r) r = consumeToken(b, STRUCT);
    if (!r) r = consumeToken(b, SWITCH);
    if (!r) r = consumeToken(b, VAR);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // Statement (semi|&'}')
  static boolean StatementWithSemi(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StatementWithSemi")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = Statement(b, l + 1);
    p = r; // pin = 1
    r = r && StatementWithSemi_1(b, l + 1);
    exit_section_(b, l, m, null, r, p, StatementRecover_parser_);
    return r || p;
  }

  // semi|&'}'
  private static boolean StatementWithSemi_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StatementWithSemi_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = semi(b, l + 1);
    if (!r) r = StatementWithSemi_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // &'}'
  private static boolean StatementWithSemi_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StatementWithSemi_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _AND_, null);
    r = consumeToken(b, RBRACE);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // StatementWithSemi*
  static boolean Statements(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Statements")) return false;
    int c = current_position_(b);
    while (true) {
      if (!StatementWithSemi(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "Statements", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // struct '{' Fields? '}'
  public static boolean StructType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StructType")) return false;
    if (!nextTokenIs(b, STRUCT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, STRUCT);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, LBRACE));
    r = p && report_error_(b, StructType_2(b, l + 1)) && r;
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, STRUCT_TYPE, r, p, null);
    return r || p;
  }

  // Fields?
  private static boolean StructType_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StructType_2")) return false;
    Fields(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // switch
  public static boolean SwitchStart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SwitchStart")) return false;
    if (!nextTokenIs(b, SWITCH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SWITCH);
    exit_section_(b, m, SWITCH_START, r);
    return r;
  }

  /* ********************************************************** */
  // SwitchStart (TypeSwitchStatement | ExprSwitchStatement)
  public static boolean SwitchStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SwitchStatement")) return false;
    if (!nextTokenIs(b, SWITCH)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _COLLAPSE_, null);
    r = SwitchStart(b, l + 1);
    p = r; // pin = 1
    r = r && SwitchStatement_1(b, l + 1);
    exit_section_(b, l, m, SWITCH_STATEMENT, r, p, null);
    return r || p;
  }

  // TypeSwitchStatement | ExprSwitchStatement
  private static boolean SwitchStatement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SwitchStatement_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = TypeSwitchStatement(b, l + 1);
    if (!r) r = ExprSwitchStatement(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // string
  public static boolean Tag(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Tag")) return false;
    if (!nextTokenIs(b, STRING)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, STRING);
    exit_section_(b, m, TAG, r);
    return r;
  }

  /* ********************************************************** */
  // OneOfDeclarations semi
  static boolean TopLevelDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TopLevelDeclaration")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = OneOfDeclarations(b, l + 1);
    p = r; // pin = 1
    r = r && semi(b, l + 1);
    exit_section_(b, l, m, null, r, p, TopLevelDeclarationRecover_parser_);
    return r || p;
  }

  /* ********************************************************** */
  // !(';' |'type' | const | func | var)
  static boolean TopLevelDeclarationRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TopLevelDeclarationRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_, null);
    r = !TopLevelDeclarationRecover_0(b, l + 1);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  // ';' |'type' | const | func | var
  private static boolean TopLevelDeclarationRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TopLevelDeclarationRecover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SEMICOLON);
    if (!r) r = consumeToken(b, TYPE_);
    if (!r) r = consumeToken(b, CONST);
    if (!r) r = consumeToken(b, FUNC);
    if (!r) r = consumeToken(b, VAR);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // TypeName | TypeLit | '(' Type ')'
  public static boolean Type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Type")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, "<type>");
    r = TypeName(b, l + 1);
    if (!r) r = TypeLit(b, l + 1);
    if (!r) r = Type_2(b, l + 1);
    exit_section_(b, l, m, TYPE, r, false, null);
    return r;
  }

  // '(' Type ')'
  private static boolean Type_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Type_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && Type(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // TypeSwitchCase ':' Statements?
  public static boolean TypeCaseClause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeCaseClause")) return false;
    if (!nextTokenIs(b, "<type case clause>", CASE, DEFAULT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<type case clause>");
    r = TypeSwitchCase(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && TypeCaseClause_2(b, l + 1);
    exit_section_(b, l, m, TYPE_CASE_CLAUSE, r, false, null);
    return r;
  }

  // Statements?
  private static boolean TypeCaseClause_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeCaseClause_2")) return false;
    Statements(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // 'type' ( TypeSpec | '(' TypeSpecs? ')' )
  public static boolean TypeDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeDeclaration")) return false;
    if (!nextTokenIs(b, TYPE_)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, TYPE_);
    p = r; // pin = 1
    r = r && TypeDeclaration_1(b, l + 1);
    exit_section_(b, l, m, TYPE_DECLARATION, r, p, null);
    return r || p;
  }

  // TypeSpec | '(' TypeSpecs? ')'
  private static boolean TypeDeclaration_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeDeclaration_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = TypeSpec(b, l + 1);
    if (!r) r = TypeDeclaration_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '(' TypeSpecs? ')'
  private static boolean TypeDeclaration_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeDeclaration_1_1")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, LPAREN);
    p = r; // pin = 1
    r = r && report_error_(b, TypeDeclaration_1_1_1(b, l + 1));
    r = p && consumeToken(b, RPAREN) && r;
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // TypeSpecs?
  private static boolean TypeDeclaration_1_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeDeclaration_1_1_1")) return false;
    TypeSpecs(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '(' 'type' ')'
  public static boolean TypeGuard(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeGuard")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, LPAREN);
    r = r && consumeToken(b, TYPE_);
    p = r; // pin = 2
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, l, m, TYPE_GUARD, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // Type ( ',' Type )*
  public static boolean TypeList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeList")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _COLLAPSE_, "<type list>");
    r = Type(b, l + 1);
    p = r; // pin = 1
    r = r && TypeList_1(b, l + 1);
    exit_section_(b, l, m, TYPE_LIST, r, p, null);
    return r || p;
  }

  // ( ',' Type )*
  private static boolean TypeList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeList_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!TypeList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "TypeList_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // ',' Type
  private static boolean TypeList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeList_1_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && Type(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // Type ( ',' Type )*
  public static boolean TypeListNoPin(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeListNoPin")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, "<type list no pin>");
    r = Type(b, l + 1);
    r = r && TypeListNoPin_1(b, l + 1);
    exit_section_(b, l, m, TYPE_LIST, r, false, null);
    return r;
  }

  // ( ',' Type )*
  private static boolean TypeListNoPin_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeListNoPin_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!TypeListNoPin_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "TypeListNoPin_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // ',' Type
  private static boolean TypeListNoPin_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeListNoPin_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && Type(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ArrayOrSliceType
  //   | StructType
  //   | PointerType
  //   | FunctionType
  //   | InterfaceType
  //   | MapType
  //   | ChannelType
  static boolean TypeLit(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeLit")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ArrayOrSliceType(b, l + 1);
    if (!r) r = StructType(b, l + 1);
    if (!r) r = PointerType(b, l + 1);
    if (!r) r = FunctionType(b, l + 1);
    if (!r) r = InterfaceType(b, l + 1);
    if (!r) r = MapType(b, l + 1);
    if (!r) r = ChannelType(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // TypeReferenceExpression QualifiedTypeReferenceExpression?
  static boolean TypeName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeName")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = TypeReferenceExpression(b, l + 1);
    r = r && TypeName_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // QualifiedTypeReferenceExpression?
  private static boolean TypeName_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeName_1")) return false;
    QualifiedTypeReferenceExpression(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // identifier
  public static boolean TypeReferenceExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeReferenceExpression")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, TYPE_REFERENCE_EXPRESSION, r);
    return r;
  }

  /* ********************************************************** */
  // identifier Type
  public static boolean TypeSpec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeSpec")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, IDENTIFIER);
    p = r; // pin = 1
    r = r && Type(b, l + 1);
    exit_section_(b, l, m, TYPE_SPEC, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // TypeSpec (semi TypeSpec)* semi?
  static boolean TypeSpecs(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeSpecs")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = TypeSpec(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, TypeSpecs_1(b, l + 1));
    r = p && TypeSpecs_2(b, l + 1) && r;
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (semi TypeSpec)*
  private static boolean TypeSpecs_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeSpecs_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!TypeSpecs_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "TypeSpecs_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // semi TypeSpec
  private static boolean TypeSpecs_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeSpecs_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = semi(b, l + 1);
    r = r && TypeSpec(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // semi?
  private static boolean TypeSpecs_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeSpecs_2")) return false;
    semi(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // case TypeList | default
  public static boolean TypeSwitchCase(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeSwitchCase")) return false;
    if (!nextTokenIs(b, "<type switch case>", CASE, DEFAULT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<type switch case>");
    r = TypeSwitchCase_0(b, l + 1);
    if (!r) r = consumeToken(b, DEFAULT);
    exit_section_(b, l, m, TYPE_SWITCH_CASE, r, false, null);
    return r;
  }

  // case TypeList
  private static boolean TypeSwitchCase_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeSwitchCase_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, CASE);
    p = r; // pin = 1
    r = r && TypeList(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // [ VarDefinition ':=' ] Expression '.' TypeGuard
  public static boolean TypeSwitchGuard(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeSwitchGuard")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<type switch guard>");
    r = TypeSwitchGuard_0(b, l + 1);
    r = r && Expression(b, l + 1, -1);
    r = r && consumeToken(b, DOT);
    r = r && TypeGuard(b, l + 1);
    exit_section_(b, l, m, TYPE_SWITCH_GUARD, r, false, null);
    return r;
  }

  // [ VarDefinition ':=' ]
  private static boolean TypeSwitchGuard_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeSwitchGuard_0")) return false;
    TypeSwitchGuard_0_0(b, l + 1);
    return true;
  }

  // VarDefinition ':='
  private static boolean TypeSwitchGuard_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeSwitchGuard_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = VarDefinition(b, l + 1);
    r = r && consumeToken(b, VAR_ASSIGN);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (TypeSwitchGuard | SimpleStatement ';'? TypeSwitchGuard) '{' ( TypeCaseClause )* '}'
  public static boolean TypeSwitchStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeSwitchStatement")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _LEFT_, "<type switch statement>");
    r = TypeSwitchStatement_0(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, consumeToken(b, LBRACE));
    r = p && report_error_(b, TypeSwitchStatement_2(b, l + 1)) && r;
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, TYPE_SWITCH_STATEMENT, r, p, null);
    return r || p;
  }

  // TypeSwitchGuard | SimpleStatement ';'? TypeSwitchGuard
  private static boolean TypeSwitchStatement_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeSwitchStatement_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = TypeSwitchGuard(b, l + 1);
    if (!r) r = TypeSwitchStatement_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // SimpleStatement ';'? TypeSwitchGuard
  private static boolean TypeSwitchStatement_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeSwitchStatement_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = SimpleStatement(b, l + 1);
    r = r && TypeSwitchStatement_0_1_1(b, l + 1);
    r = r && TypeSwitchGuard(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ';'?
  private static boolean TypeSwitchStatement_0_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeSwitchStatement_0_1_1")) return false;
    consumeToken(b, SEMICOLON);
    return true;
  }

  // ( TypeCaseClause )*
  private static boolean TypeSwitchStatement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeSwitchStatement_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!TypeSwitchStatement_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "TypeSwitchStatement_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // ( TypeCaseClause )
  private static boolean TypeSwitchStatement_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeSwitchStatement_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = TypeCaseClause(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // Expression | LiteralValue
  public static boolean Value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Value")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<value>");
    r = Expression(b, l + 1, -1);
    if (!r) r = LiteralValue(b, l + 1);
    exit_section_(b, l, m, VALUE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // var ( VarSpec | '(' VarSpecs? ')' )
  public static boolean VarDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VarDeclaration")) return false;
    if (!nextTokenIs(b, VAR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, VAR);
    p = r; // pin = 1
    r = r && VarDeclaration_1(b, l + 1);
    exit_section_(b, l, m, VAR_DECLARATION, r, p, null);
    return r || p;
  }

  // VarSpec | '(' VarSpecs? ')'
  private static boolean VarDeclaration_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VarDeclaration_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = VarSpec(b, l + 1);
    if (!r) r = VarDeclaration_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '(' VarSpecs? ')'
  private static boolean VarDeclaration_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VarDeclaration_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && VarDeclaration_1_1_1(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // VarSpecs?
  private static boolean VarDeclaration_1_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VarDeclaration_1_1_1")) return false;
    VarSpecs(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // identifier
  public static boolean VarDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VarDefinition")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, VAR_DEFINITION, r);
    return r;
  }

  /* ********************************************************** */
  // VarDefinition ( ',' VarDefinition )*
  static boolean VarDefinitionList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VarDefinitionList")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = VarDefinition(b, l + 1);
    p = r; // pin = 1
    r = r && VarDefinitionList_1(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // ( ',' VarDefinition )*
  private static boolean VarDefinitionList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VarDefinitionList_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!VarDefinitionList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "VarDefinitionList_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // ',' VarDefinition
  private static boolean VarDefinitionList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VarDefinitionList_1_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && VarDefinition(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // VarDefinitionList ( Type [ '=' ExpressionList ] | '=' ExpressionList )
  public static boolean VarSpec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VarSpec")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = VarDefinitionList(b, l + 1);
    r = r && VarSpec_1(b, l + 1);
    exit_section_(b, m, VAR_SPEC, r);
    return r;
  }

  // Type [ '=' ExpressionList ] | '=' ExpressionList
  private static boolean VarSpec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VarSpec_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = VarSpec_1_0(b, l + 1);
    if (!r) r = VarSpec_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // Type [ '=' ExpressionList ]
  private static boolean VarSpec_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VarSpec_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Type(b, l + 1);
    r = r && VarSpec_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // [ '=' ExpressionList ]
  private static boolean VarSpec_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VarSpec_1_0_1")) return false;
    VarSpec_1_0_1_0(b, l + 1);
    return true;
  }

  // '=' ExpressionList
  private static boolean VarSpec_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VarSpec_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ASSIGN);
    r = r && ExpressionList(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '=' ExpressionList
  private static boolean VarSpec_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VarSpec_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ASSIGN);
    r = r && ExpressionList(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // VarSpec (semi VarSpec)* semi?
  static boolean VarSpecs(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VarSpecs")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = VarSpec(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, VarSpecs_1(b, l + 1));
    r = p && VarSpecs_2(b, l + 1) && r;
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (semi VarSpec)*
  private static boolean VarSpecs_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VarSpecs_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!VarSpecs_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "VarSpecs_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // semi VarSpec
  private static boolean VarSpecs_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VarSpecs_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = semi(b, l + 1);
    r = r && VarSpec(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // semi?
  private static boolean VarSpecs_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "VarSpecs_2")) return false;
    semi(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '+' | '-' | '|' | '^'
  static boolean add_op(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "add_op")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PLUS);
    if (!r) r = consumeToken(b, MINUS);
    if (!r) r = consumeToken(b, BIT_OR);
    if (!r) r = consumeToken(b, BIT_XOR);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '=' | '+=' | '-=' | '|=' | '^=' | '*=' | '/=' | '%=' | '<<=' | '>>=' | '&=' | '&^='
  public static boolean assign_op(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assign_op")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<assign op>");
    r = consumeToken(b, ASSIGN);
    if (!r) r = consumeToken(b, PLUS_ASSIGN);
    if (!r) r = consumeToken(b, MINUS_ASSIGN);
    if (!r) r = consumeToken(b, BIT_OR_ASSIGN);
    if (!r) r = consumeToken(b, BIT_XOR_ASSIGN);
    if (!r) r = consumeToken(b, MUL_ASSIGN);
    if (!r) r = consumeToken(b, QUOTIENT_ASSIGN);
    if (!r) r = consumeToken(b, REMAINDER_ASSIGN);
    if (!r) r = consumeToken(b, SHIFT_LEFT_ASSIGN);
    if (!r) r = consumeToken(b, SHIFT_RIGHT_ASSIGN);
    if (!r) r = consumeToken(b, BIT_AND_ASSIGN);
    if (!r) r = consumeToken(b, BIT_CLEAR_ASSIGN);
    exit_section_(b, l, m, ASSIGN_OP, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '*' | '/' | '%' | '<<' | '>>' | '&' | '&^'
  static boolean mul_op(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mul_op")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, MUL);
    if (!r) r = consumeToken(b, QUOTIENT);
    if (!r) r = consumeToken(b, REMAINDER);
    if (!r) r = consumeToken(b, SHIFT_LEFT);
    if (!r) r = consumeToken(b, SHIFT_RIGHT);
    if (!r) r = consumeToken(b, BIT_AND);
    if (!r) r = consumeToken(b, BIT_CLEAR);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '==' | '!=' | '<' | '<=' | '>' | '>='
  static boolean rel_op(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rel_op")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQ);
    if (!r) r = consumeToken(b, NOT_EQ);
    if (!r) r = consumeToken(b, LESS);
    if (!r) r = consumeToken(b, LESS_OR_EQUAL);
    if (!r) r = consumeToken(b, GREATER);
    if (!r) r = consumeToken(b, GREATER_OR_EQUAL);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '<NL>' | ';' | <<eof>>
  static boolean semi(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "semi")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SEMICOLON_SYNTHETIC);
    if (!r) r = consumeToken(b, SEMICOLON);
    if (!r) r = eof(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '+' | '-' | '!' | '^' | '*' | '&' | '<-'
  static boolean unary_op(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unary_op")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PLUS);
    if (!r) r = consumeToken(b, MINUS);
    if (!r) r = consumeToken(b, NOT);
    if (!r) r = consumeToken(b, BIT_XOR);
    if (!r) r = consumeToken(b, MUL);
    if (!r) r = consumeToken(b, BIT_AND);
    if (!r) r = consumeToken(b, SEND_CHANNEL);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // Expression root: Expression
  // Operator priority table:
  // 0: BINARY(OrExpr)
  // 1: BINARY(AndExpr)
  // 2: BINARY(ConditionalExpr)
  // 3: BINARY(AddExpr)
  // 4: BINARY(MulExpr)
  // 5: PREFIX(UnaryExpr)
  // 6: ATOM(ConversionExpr)
  // 7: ATOM(CompositeLit) ATOM(OperandName) POSTFIX(BuiltinCallExpr) POSTFIX(CallExpr) POSTFIX(TypeAssertionExpr) BINARY(SelectorExpr) ATOM(MethodExpr) POSTFIX(SliceExpr) POSTFIX(IndexExpr) ATOM(Literal) ATOM(LiteralTypeExpr) ATOM(FunctionLit)
  // 8: PREFIX(ParenthesesExpr)
  public static boolean Expression(PsiBuilder b, int l, int g) {
    if (!recursion_guard_(b, l, "Expression")) return false;
    addVariant(b, "<expression>");
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, "<expression>");
    r = UnaryExpr(b, l + 1);
    if (!r) r = ConversionExpr(b, l + 1);
    if (!r) r = CompositeLit(b, l + 1);
    if (!r) r = OperandName(b, l + 1);
    if (!r) r = MethodExpr(b, l + 1);
    if (!r) r = Literal(b, l + 1);
    if (!r) r = LiteralTypeExpr(b, l + 1);
    if (!r) r = FunctionLit(b, l + 1);
    if (!r) r = ParenthesesExpr(b, l + 1);
    p = r;
    r = r && Expression_0(b, l + 1, g);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  public static boolean Expression_0(PsiBuilder b, int l, int g) {
    if (!recursion_guard_(b, l, "Expression_0")) return false;
    boolean r = true;
    while (true) {
      Marker m = enter_section_(b, l, _LEFT_, null);
      if (g < 0 && consumeTokenSmart(b, COND_OR)) {
        r = Expression(b, l, 0);
        exit_section_(b, l, m, OR_EXPR, r, true, null);
      }
      else if (g < 1 && consumeTokenSmart(b, COND_AND)) {
        r = Expression(b, l, 1);
        exit_section_(b, l, m, AND_EXPR, r, true, null);
      }
      else if (g < 2 && rel_op(b, l + 1)) {
        r = Expression(b, l, 2);
        exit_section_(b, l, m, CONDITIONAL_EXPR, r, true, null);
      }
      else if (g < 3 && add_op(b, l + 1)) {
        r = Expression(b, l, 3);
        exit_section_(b, l, m, ADD_EXPR, r, true, null);
      }
      else if (g < 4 && mul_op(b, l + 1)) {
        r = Expression(b, l, 4);
        exit_section_(b, l, m, MUL_EXPR, r, true, null);
      }
      else if (g < 7 && leftMarkerIs(b, REFERENCE_EXPRESSION) && BuiltinCallExpr_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, BUILTIN_CALL_EXPR, r, true, null);
      }
      else if (g < 7 && ArgumentList(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, CALL_EXPR, r, true, null);
      }
      else if (g < 7 && TypeAssertionExpr_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, TYPE_ASSERTION_EXPR, r, true, null);
      }
      else if (g < 7 && SelectorExpr_0(b, l + 1)) {
        r = Expression(b, l, 7);
        exit_section_(b, l, m, SELECTOR_EXPR, r, true, null);
      }
      else if (g < 7 && SliceExpr_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, SLICE_EXPR, r, true, null);
      }
      else if (g < 7 && IndexExpr_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, INDEX_EXPR, r, true, null);
      }
      else {
        exit_section_(b, l, m, null, false, false, null);
        break;
      }
    }
    return r;
  }

  public static boolean UnaryExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "UnaryExpr")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = unary_op(b, l + 1);
    p = r;
    r = p && Expression(b, l, 5);
    exit_section_(b, l, m, UNARY_EXPR, r, p, null);
    return r || p;
  }

  // &ConversionPredicate Type ConversionTail
  public static boolean ConversionExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConversionExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<conversion expr>");
    r = ConversionExpr_0(b, l + 1);
    r = r && Type(b, l + 1);
    r = r && ConversionTail(b, l + 1);
    exit_section_(b, l, m, CONVERSION_EXPR, r, false, null);
    return r;
  }

  // &ConversionPredicate
  private static boolean ConversionExpr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConversionExpr_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _AND_, null);
    r = ConversionPredicate(b, l + 1);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  // LiteralTypeExpr LiteralValue
  public static boolean CompositeLit(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CompositeLit")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<composite lit>");
    r = LiteralTypeExpr(b, l + 1);
    r = r && LiteralValue(b, l + 1);
    exit_section_(b, l, m, COMPOSITE_LIT, r, false, null);
    return r;
  }

  // ReferenceExpression QualifiedReferenceExpression?
  public static boolean OperandName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OperandName")) return false;
    if (!nextTokenIsFast(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, null);
    r = ReferenceExpression(b, l + 1);
    r = r && OperandName_1(b, l + 1);
    exit_section_(b, l, m, REFERENCE_EXPRESSION, r, false, null);
    return r;
  }

  // QualifiedReferenceExpression?
  private static boolean OperandName_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "OperandName_1")) return false;
    QualifiedReferenceExpression(b, l + 1);
    return true;
  }

  // <<isBuiltin>> '(' [ BuiltinArgs ','? ] ')'
  private static boolean BuiltinCallExpr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BuiltinCallExpr_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = isBuiltin(b, l + 1);
    r = r && consumeToken(b, LPAREN);
    r = r && BuiltinCallExpr_0_2(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // [ BuiltinArgs ','? ]
  private static boolean BuiltinCallExpr_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BuiltinCallExpr_0_2")) return false;
    BuiltinCallExpr_0_2_0(b, l + 1);
    return true;
  }

  // BuiltinArgs ','?
  private static boolean BuiltinCallExpr_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BuiltinCallExpr_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = BuiltinArgs(b, l + 1);
    r = r && BuiltinCallExpr_0_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ','?
  private static boolean BuiltinCallExpr_0_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BuiltinCallExpr_0_2_0_1")) return false;
    consumeTokenSmart(b, COMMA);
    return true;
  }

  // '.' '(' &(!'type') Type ')'
  private static boolean TypeAssertionExpr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeAssertionExpr_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, DOT);
    r = r && consumeToken(b, LPAREN);
    r = r && TypeAssertionExpr_0_2(b, l + 1);
    r = r && Type(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // &(!'type')
  private static boolean TypeAssertionExpr_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeAssertionExpr_0_2")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _AND_, null);
    r = TypeAssertionExpr_0_2_0(b, l + 1);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  // !'type'
  private static boolean TypeAssertionExpr_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeAssertionExpr_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_, null);
    r = !consumeTokenSmart(b, TYPE_);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  // '.' !('(' 'type')
  private static boolean SelectorExpr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SelectorExpr_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, DOT);
    r = r && SelectorExpr_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // !('(' 'type')
  private static boolean SelectorExpr_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SelectorExpr_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_, null);
    r = !SelectorExpr_0_1_0(b, l + 1);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  // '(' 'type'
  private static boolean SelectorExpr_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SelectorExpr_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, LPAREN);
    r = r && consumeToken(b, TYPE_);
    exit_section_(b, m, null, r);
    return r;
  }

  // ReceiverType '.' identifier
  public static boolean MethodExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "MethodExpr")) return false;
    if (!nextTokenIsFast(b, LPAREN, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<method expr>");
    r = ReceiverType(b, l + 1);
    r = r && consumeToken(b, DOT);
    r = r && consumeToken(b, IDENTIFIER);
    exit_section_(b, l, m, METHOD_EXPR, r, false, null);
    return r;
  }

  // '[' SliceExprBody ']'
  private static boolean SliceExpr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SliceExpr_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, LBRACK);
    r = r && SliceExprBody(b, l + 1);
    r = r && consumeToken(b, RBRACK);
    exit_section_(b, m, null, r);
    return r;
  }

  // '[' IndexExprBody ']'
  private static boolean IndexExpr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "IndexExpr_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, LBRACK);
    r = r && IndexExprBody(b, l + 1);
    r = r && consumeToken(b, RBRACK);
    exit_section_(b, m, null, r);
    return r;
  }

  // int
  //   | float
  //   | floati
  //   | decimali
  //   | hex
  //   | oct
  //   | imaginary
  //   | rune
  //   | string
  //   | char
  public static boolean Literal(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Literal")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<literal>");
    r = consumeTokenSmart(b, INT);
    if (!r) r = consumeTokenSmart(b, FLOAT);
    if (!r) r = consumeTokenSmart(b, FLOATI);
    if (!r) r = consumeTokenSmart(b, DECIMALI);
    if (!r) r = consumeTokenSmart(b, HEX);
    if (!r) r = consumeTokenSmart(b, OCT);
    if (!r) r = consumeTokenSmart(b, IMAGINARY);
    if (!r) r = consumeTokenSmart(b, RUNE);
    if (!r) r = consumeTokenSmart(b, STRING);
    if (!r) r = consumeTokenSmart(b, CHAR);
    exit_section_(b, l, m, LITERAL, r, false, null);
    return r;
  }

  // StructType
  //   | ArrayOrSliceType
  //   | MapType
  //   | TypeName
  public static boolean LiteralTypeExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LiteralTypeExpr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<literal type expr>");
    r = StructType(b, l + 1);
    if (!r) r = ArrayOrSliceType(b, l + 1);
    if (!r) r = MapType(b, l + 1);
    if (!r) r = TypeName(b, l + 1);
    exit_section_(b, l, m, LITERAL_TYPE_EXPR, r, false, null);
    return r;
  }

  // func Signature Block
  public static boolean FunctionLit(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionLit")) return false;
    if (!nextTokenIsFast(b, FUNC)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokenSmart(b, FUNC);
    p = r; // pin = 1
    r = r && report_error_(b, Signature(b, l + 1));
    r = p && Block(b, l + 1) && r;
    exit_section_(b, l, m, FUNCTION_LIT, r, p, null);
    return r || p;
  }

  public static boolean ParenthesesExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ParenthesesExpr")) return false;
    if (!nextTokenIsFast(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokenSmart(b, LPAREN);
    p = r;
    r = p && Expression(b, l, -1);
    r = p && report_error_(b, consumeToken(b, RPAREN)) && r;
    exit_section_(b, l, m, PARENTHESES_EXPR, r, p, null);
    return r || p;
  }

  final static Parser ExpressionListRecover_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return ExpressionListRecover(b, l + 1);
    }
  };
  final static Parser StatementRecover_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return StatementRecover(b, l + 1);
    }
  };
  final static Parser TopLevelDeclarationRecover_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return TopLevelDeclarationRecover(b, l + 1);
    }
  };
}
