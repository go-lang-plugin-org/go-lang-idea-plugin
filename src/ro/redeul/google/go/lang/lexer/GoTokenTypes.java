package ro.redeul.google.go.lang.lexer;

import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 2:30:09 AM
 * To change this template use File | Settings | File Templates.
 */
public interface GoTokenTypes {

    IElementType wsWS = new GoElementTypeImpl("WHITESPACE");
    IElementType wsNLS = new GoElementTypeImpl("WS_NEW_LINES");

    IElementType litCHAR = new GoElementTypeImpl("LITERAL_CHAR");
    IElementType litSTRING = new GoElementTypeImpl("LITERAL_STRING");

    IElementType litOCT = new GoElementTypeImpl("LITERAL_OCT");
    IElementType litINT = new GoElementTypeImpl("LITERAL_INT");
    IElementType litHEX = new GoElementTypeImpl("LITERAL_HEX");
    IElementType litFLOAT = new GoElementTypeImpl("LITERAL_FLOAT");
    IElementType litFLOAT_I = new GoElementTypeImpl("LITERAL_IMAGINARY_FLOAT");
    IElementType litDECIMAL_I = new GoElementTypeImpl("LITERAL_IMAGINARY_INTEGER");

    IElementType kBREAK = new GoElementTypeImpl("KEYWORD_BREAK");
    IElementType kDEFAULT = new GoElementTypeImpl("KEYWORD_DEFAULT");
    IElementType kPACKAGE = new GoElementTypeImpl("KEYWORD_PACKAGE");
    IElementType kFUNC = new GoElementTypeImpl("KEYWORD_FUNC");
    IElementType kINTERFACE = new GoElementTypeImpl("KEYWORD_INTERFACE");
    IElementType kSELECT = new GoElementTypeImpl("KEYWORD_SELECT");

    IElementType kCASE = new GoElementTypeImpl("KEYWORD_CASE");
    IElementType kDEFER = new GoElementTypeImpl("KEYWORD_DEFER");
    IElementType kGO = new GoElementTypeImpl("KEYWORD_GO");
    IElementType kMAP = new GoElementTypeImpl("KEYWORD_MAP");

    IElementType kCHAN = new GoElementTypeImpl("KEYWORD_CHAN");

    IElementType kSTRUCT = new GoElementTypeImpl("KEYWORD_STRUCT");
    IElementType kELSE = new GoElementTypeImpl("KEYWORD_ELSE");
    IElementType kGOTO = new GoElementTypeImpl("KEYWORD_GOTO");
    IElementType kSWITCH = new GoElementTypeImpl("KEYWORD_SWITCH");
    IElementType kCONST = new GoElementTypeImpl("KEYWORD_CONST");
    IElementType kFALLTHROUGH = new GoElementTypeImpl("KEYWORD_FALLTHROUGH");

    IElementType kIF = new GoElementTypeImpl("KEYWORD_IF");
    IElementType kFOR = new GoElementTypeImpl("KEYWORD_FOR");
    IElementType kRETURN = new GoElementTypeImpl("KEYWORD_RETURN");
    IElementType kIMPORT = new GoElementTypeImpl("KEYWORD_IMPORT");
    IElementType kCONTINUE = new GoElementTypeImpl("KEYWORD_CONTINUE");

    IElementType kRANGE = new GoElementTypeImpl("KEYWORD_RANGE");
    IElementType kTYPE = new GoElementTypeImpl("KEYWORD_TYPE");
    IElementType kVAR = new GoElementTypeImpl("KEYWORD_VAR");

    IElementType mWRONG = new GoElementTypeImpl("WRONG");

    IElementType mSL_COMMENT = new GoElementTypeImpl("SL_COMMENT");
    IElementType mML_COMMENT = new GoElementTypeImpl("ML_COMMENT");

    IElementType mIDENT = new GoElementTypeImpl("IDENTIFIER");

    IElementType pLCURCLY = new GoElementTypeImpl("{");
    IElementType pRCURLY = new GoElementTypeImpl("}");
    IElementType pLBRACK = new GoElementTypeImpl("[");
    IElementType pRBRACK = new GoElementTypeImpl("]");
    IElementType pLPAREN = new GoElementTypeImpl("(");
    IElementType pRPAREN = new GoElementTypeImpl(")");

    IElementType oSEMI = new GoElementTypeImpl(";");

    IElementType oTRIPLE_DOT = new GoElementTypeImpl("...");
    IElementType oDOT = new GoElementTypeImpl(".");
    IElementType oCOLON = new GoElementTypeImpl(":");
    IElementType oCOMMA = new GoElementTypeImpl(",");

    IElementType oEQ = new GoElementTypeImpl("==");
    IElementType oASSIGN = new GoElementTypeImpl("=");

    IElementType oNOT_EQ = new GoElementTypeImpl("!=");
    IElementType oNOT = new GoElementTypeImpl("!");

    IElementType oPLUS_PLUS = new GoElementTypeImpl("++");
    IElementType oPLUS_ASSIGN = new GoElementTypeImpl("+=");
    IElementType oPLUS = new GoElementTypeImpl("+");

    IElementType oMINUS_MINUS = new GoElementTypeImpl("--");
    IElementType oMINUS_ASSIGN = new GoElementTypeImpl("-=");
    IElementType oMINUS = new GoElementTypeImpl("-");

    IElementType oBIT_OR_ASSIGN = new GoElementTypeImpl("|=");
    IElementType oBIT_OR = new GoElementTypeImpl("|");
    IElementType oCOND_OR = new GoElementTypeImpl("||");

    IElementType oBIT_CLEAR_ASSIGN = new GoElementTypeImpl("&^=");
    IElementType oBIT_CLEAR = new GoElementTypeImpl("&^");
    IElementType oCOND_AND = new GoElementTypeImpl("&&");

    IElementType oBIT_AND_ASSIGN = new GoElementTypeImpl("&=");
    IElementType oBIT_AND = new GoElementTypeImpl("&");

    IElementType oBIT_XOR_ASSIGN = new GoElementTypeImpl("^=");
    IElementType oBIT_XOR = new GoElementTypeImpl("^");

    IElementType oMUL_ASSIGN = new GoElementTypeImpl("*=");
    IElementType oMUL = new GoElementTypeImpl("*");

    IElementType oQUOTIENT_ASSIGN = new GoElementTypeImpl("/=");
    IElementType oQUOTIENT = new GoElementTypeImpl("/");

    IElementType oREMAINDER_ASSIGN = new GoElementTypeImpl("%=");
    IElementType oREMAINDER = new GoElementTypeImpl("%");

    IElementType oSEND_CHANNEL = new GoElementTypeImpl("<-");
    IElementType oSHIFT_LEFT_ASSIGN = new GoElementTypeImpl("<<=");
    IElementType oSHIFT_LEFT = new GoElementTypeImpl("<<");

    IElementType oSHIFT_RIGHT_ASSIGN = new GoElementTypeImpl(">>=");
    IElementType oSHIFT_RIGHT = new GoElementTypeImpl(">>");

    IElementType oLESS_OR_EQUAL = new GoElementTypeImpl("<=");
    IElementType oLESS = new GoElementTypeImpl("<");

    IElementType oGREATER_OR_EQUAL = new GoElementTypeImpl(">=");
    IElementType oGREATER = new GoElementTypeImpl(">");

    IElementType oVAR_ASSIGN = new GoElementTypeImpl(":=");


}
