// This is a generated file. Not intended for manual editing.
package com.plan9.intel.lang.core.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.plan9.intel.lang.core.psi.AsmIntelTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class AsmIntelParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    if (t == FRAME_SIZE) {
      r = FrameSize(b, 0);
    }
    else if (t == FUNCTION) {
      r = Function(b, 0);
    }
    else if (t == FUNCTION_BODY) {
      r = FunctionBody(b, 0);
    }
    else if (t == FUNCTION_FLAGS) {
      r = FunctionFlags(b, 0);
    }
    else if (t == FUNCTION_HEADER) {
      r = FunctionHeader(b, 0);
    }
    else if (t == INSTRUCTION_STMT) {
      r = InstructionStmt(b, 0);
    }
    else if (t == LITERAL) {
      r = Literal(b, 0);
    }
    else if (t == OPERANDS) {
      r = Operands(b, 0);
    }
    else if (t == PREPROCESSOR_DIRECTIVE) {
      r = PreprocessorDirective(b, 0);
    }
    else if (t == STATEMENT) {
      r = Statement(b, 0);
    }
    else {
      r = parse_root_(t, b, 0);
    }
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return File(b, l + 1);
  }

  /* ********************************************************** */
  // Statement*
  static boolean File(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "File")) return false;
    int c = current_position_(b);
    while (true) {
      if (!Statement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "File", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // '$' Literal ( '-' Literal )?
  public static boolean FrameSize(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FrameSize")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<frame size>");
    r = consumeToken(b, "$");
    r = r && Literal(b, l + 1);
    r = r && FrameSize_2(b, l + 1);
    exit_section_(b, l, m, FRAME_SIZE, r, false, null);
    return r;
  }

  // ( '-' Literal )?
  private static boolean FrameSize_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FrameSize_2")) return false;
    FrameSize_2_0(b, l + 1);
    return true;
  }

  // '-' Literal
  private static boolean FrameSize_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FrameSize_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "-");
    r = r && Literal(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // FunctionHeader FunctionBody
  public static boolean Function(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Function")) return false;
    if (!nextTokenIs(b, TEXT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = FunctionHeader(b, l + 1);
    r = r && FunctionBody(b, l + 1);
    exit_section_(b, m, FUNCTION, r);
    return r;
  }

  /* ********************************************************** */
  // ( InstructionStmt | LABEL )*
  public static boolean FunctionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, "<function body>");
    int c = current_position_(b);
    while (true) {
      if (!FunctionBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "FunctionBody", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, l, m, FUNCTION_BODY, true, false, null);
    return true;
  }

  // InstructionStmt | LABEL
  private static boolean FunctionBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionBody_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = InstructionStmt(b, l + 1);
    if (!r) r = consumeToken(b, LABEL);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // FLAG | '(' FLAG ( '|' FLAG )* ')'
  public static boolean FunctionFlags(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionFlags")) return false;
    if (!nextTokenIs(b, "<function flags>", LPAREN, FLAG)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<function flags>");
    r = consumeToken(b, FLAG);
    if (!r) r = FunctionFlags_1(b, l + 1);
    exit_section_(b, l, m, FUNCTION_FLAGS, r, false, null);
    return r;
  }

  // '(' FLAG ( '|' FLAG )* ')'
  private static boolean FunctionFlags_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionFlags_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && consumeToken(b, FLAG);
    r = r && FunctionFlags_1_2(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( '|' FLAG )*
  private static boolean FunctionFlags_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionFlags_1_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!FunctionFlags_1_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "FunctionFlags_1_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // '|' FLAG
  private static boolean FunctionFlags_1_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionFlags_1_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, BIT_OR);
    r = r && consumeToken(b, FLAG);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // TEXT identifier '(' PSEUDO_REG ')' ',' FunctionFlags ( ',' FrameSize )?
  public static boolean FunctionHeader(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionHeader")) return false;
    if (!nextTokenIs(b, TEXT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TEXT, IDENTIFIER);
    r = r && consumeToken(b, LPAREN);
    r = r && consumeToken(b, PSEUDO_REG);
    r = r && consumeToken(b, RPAREN);
    r = r && consumeToken(b, COMMA);
    r = r && FunctionFlags(b, l + 1);
    r = r && FunctionHeader_7(b, l + 1);
    exit_section_(b, m, FUNCTION_HEADER, r);
    return r;
  }

  // ( ',' FrameSize )?
  private static boolean FunctionHeader_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionHeader_7")) return false;
    FunctionHeader_7_0(b, l + 1);
    return true;
  }

  // ',' FrameSize
  private static boolean FunctionHeader_7_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FunctionHeader_7_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && FrameSize(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // INSTRUCTION Operands
  public static boolean InstructionStmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "InstructionStmt")) return false;
    if (!nextTokenIs(b, INSTRUCTION)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, INSTRUCTION);
    r = r && Operands(b, l + 1);
    exit_section_(b, m, INSTRUCTION_STMT, r);
    return r;
  }

  /* ********************************************************** */
  // int | hex
  public static boolean Literal(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Literal")) return false;
    if (!nextTokenIs(b, "<literal>", HEX, INT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<literal>");
    r = consumeToken(b, INT);
    if (!r) r = consumeToken(b, HEX);
    exit_section_(b, l, m, LITERAL, r, false, null);
    return r;
  }

  /* ********************************************************** */
  public static boolean Operands(PsiBuilder b, int l) {
    Marker m = enter_section_(b);
    exit_section_(b, m, OPERANDS, true);
    return true;
  }

  /* ********************************************************** */
  // import STRING
  public static boolean PreprocessorDirective(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PreprocessorDirective")) return false;
    if (!nextTokenIs(b, IMPORT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IMPORT, STRING);
    exit_section_(b, m, PREPROCESSOR_DIRECTIVE, r);
    return r;
  }

  /* ********************************************************** */
  // PreprocessorDirective | Function
  public static boolean Statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Statement")) return false;
    if (!nextTokenIs(b, "<statement>", TEXT, IMPORT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<statement>");
    r = PreprocessorDirective(b, l + 1);
    if (!r) r = Function(b, l + 1);
    exit_section_(b, l, m, STATEMENT, r, false, null);
    return r;
  }

}
