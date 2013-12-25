package com.goide;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

public interface GoTokenTypes {
  IElementType wsWS = new GoElementType("WHITESPACE");
  IElementType wsNLS = new GoElementType("WS_NEW_LINES");

  IElementType litCHAR = new GoElementType("LITERAL_CHAR");
  IElementType litSTRING = new GoElementType("LITERAL_STRING");

  IElementType litOCT = new GoElementType("LITERAL_OCT");
  IElementType litINT = new GoElementType("LITERAL_INT");
  IElementType litHEX = new GoElementType("LITERAL_HEX");
  IElementType litFLOAT = new GoElementType("LITERAL_FLOAT");
  IElementType litFLOAT_I = new GoElementType("LITERAL_IMAGINARY_FLOAT");
  IElementType litDECIMAL_I = new GoElementType("LITERAL_IMAGINARY_INTEGER");

  IElementType kBREAK = new GoElementType("KEYWORD_BREAK");
  IElementType kDEFAULT = new GoElementType("KEYWORD_DEFAULT");
  IElementType kPACKAGE = new GoElementType("KEYWORD_PACKAGE");
  IElementType kFUNC = new GoElementType("KEYWORD_FUNC");
  IElementType kINTERFACE = new GoElementType("KEYWORD_INTERFACE");
  IElementType kSELECT = new GoElementType("KEYWORD_SELECT");

  IElementType kCASE = new GoElementType("KEYWORD_CASE");
  IElementType kDEFER = new GoElementType("KEYWORD_DEFER");
  IElementType kGO = new GoElementType("KEYWORD_GO");
  IElementType kMAP = new GoElementType("KEYWORD_MAP");

  IElementType kCHAN = new GoElementType("KEYWORD_CHAN");

  IElementType kSTRUCT = new GoElementType("KEYWORD_STRUCT");
  IElementType kELSE = new GoElementType("KEYWORD_ELSE");
  IElementType kGOTO = new GoElementType("KEYWORD_GOTO");
  IElementType kSWITCH = new GoElementType("KEYWORD_SWITCH");
  IElementType kCONST = new GoElementType("KEYWORD_CONST");
  IElementType kFALLTHROUGH = new GoElementType("KEYWORD_FALLTHROUGH");

  IElementType kIF = new GoElementType("KEYWORD_IF");
  IElementType kFOR = new GoElementType("KEYWORD_FOR");
  IElementType kRETURN = new GoElementType("KEYWORD_RETURN");
  IElementType kIMPORT = new GoElementType("KEYWORD_IMPORT");
  IElementType kCONTINUE = new GoElementType("KEYWORD_CONTINUE");

  IElementType kRANGE = new GoElementType("KEYWORD_RANGE");
  IElementType kTYPE = new GoElementType("KEYWORD_TYPE");
  IElementType kVAR = new GoElementType("KEYWORD_VAR");

  IElementType mWRONG = new GoElementType("WRONG");

  IElementType mSL_COMMENT = new GoElementType("SL_COMMENT");
  IElementType mML_COMMENT = new GoElementType("ML_COMMENT");

  IElementType mIDENT = new GoElementType("IDENTIFIER");

  IElementType pLCURLY = new GoElementType("{");
  IElementType pRCURLY = new GoElementType("}");
  IElementType pLBRACK = new GoElementType("[");
  IElementType pRBRACK = new GoElementType("]");
  IElementType pLPAREN = new GoElementType("(");
  IElementType pRPAREN = new GoElementType(")");

  IElementType oSEMI = new GoElementType(";");
  IElementType oSEMI_SYNTHETIC = new GoElementType("; (synthetic)");

  IElementType oTRIPLE_DOT = new GoElementType("...");
  IElementType oDOT = new GoElementType(".");
  IElementType oCOLON = new GoElementType(":");
  IElementType oCOMMA = new GoElementType(",");

  IElementType oEQ = new GoElementType("==");
  IElementType oASSIGN = new GoElementType("=");

  IElementType oNOT_EQ = new GoElementType("!=");
  IElementType oNOT = new GoElementType("!");

  IElementType oPLUS_PLUS = new GoElementType("++");
  IElementType oPLUS_ASSIGN = new GoElementType("+=");
  IElementType oPLUS = new GoElementType("+");

  IElementType oMINUS_MINUS = new GoElementType("--");
  IElementType oMINUS_ASSIGN = new GoElementType("-=");
  IElementType oMINUS = new GoElementType("-");

  IElementType oBIT_OR_ASSIGN = new GoElementType("|=");
  IElementType oBIT_OR = new GoElementType("|");
  IElementType oCOND_OR = new GoElementType("||");

  IElementType oBIT_CLEAR_ASSIGN = new GoElementType("^&=");
  IElementType oBIT_CLEAR = new GoElementType("^&");
  IElementType oCOND_AND = new GoElementType("&&");

  IElementType oBIT_AND_ASSIGN = new GoElementType("&=");
  IElementType oBIT_AND = new GoElementType("&");

  IElementType oBIT_XOR_ASSIGN = new GoElementType("^=");
  IElementType oBIT_XOR = new GoElementType("^");

  IElementType oMUL_ASSIGN = new GoElementType("*=");
  IElementType oMUL = new GoElementType("*");

  IElementType oQUOTIENT_ASSIGN = new GoElementType("/=");
  IElementType oQUOTIENT = new GoElementType("/");

  IElementType oREMAINDER_ASSIGN = new GoElementType("%=");
  IElementType oREMAINDER = new GoElementType("%");

  IElementType oSEND_CHANNEL = new GoElementType("<-");
  IElementType oSHIFT_LEFT_ASSIGN = new GoElementType("<<=");
  IElementType oSHIFT_LEFT = new GoElementType("<<");

  IElementType oSHIFT_RIGHT_ASSIGN = new GoElementType(">>=");
  IElementType oSHIFT_RIGHT = new GoElementType(">>");

  IElementType oLESS_OR_EQUAL = new GoElementType("<=");
  IElementType oLESS = new GoElementType("<");

  IElementType oGREATER_OR_EQUAL = new GoElementType(">=");
  IElementType oGREATER = new GoElementType(">");

  IElementType oVAR_ASSIGN = new GoElementType(":=");

  TokenSet WHITESPACES = TokenSet.create(wsWS, wsNLS);
  TokenSet COMMENTS = TokenSet.create(mSL_COMMENT, mML_COMMENT);
  TokenSet STRING_LITERALS = TokenSet.create(litCHAR, litSTRING);
}