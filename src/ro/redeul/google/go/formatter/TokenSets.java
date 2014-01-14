package ro.redeul.google.go.formatter;

import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.lang.parser.GoElementTypes;

import static ro.redeul.google.go.lang.lexer.GoTokenTypes.mML_COMMENT;
import static ro.redeul.google.go.lang.lexer.GoTokenTypes.mSL_COMMENT;
import static ro.redeul.google.go.lang.parser.GoElementTypes.*;

/**
 * TODO: Document this
 * <p/>
 * Created on Jan-13-2014 21:10
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public interface TokenSets {

    public static final TokenSet STMT_SELECT_HOLD_TOGETHER = TokenSet.orSet(
        GoElementTypes.SELECT_CLAUSES,
        GoElementTypes.COMMENTS
    );

    public static final TokenSet LEAF_BLOCKS = TokenSet.create(
        mSL_COMMENT, mML_COMMENT,
        LITERAL_BOOL,
        LITERAL_CHAR,
        LITERAL_STRING,
        LITERAL_FLOAT, LITERAL_INTEGER, LITERAL_IMAGINARY,
        LITERAL_IOTA, LITERAL_IDENTIFIER,

        kPACKAGE,

        kIMPORT, kVAR, kCONST, kTYPE, kFUNC,

        kSTRUCT, kINTERFACE,

        kBREAK, kCONTINUE, kFALLTHROUGH, kDEFER, kGO, kGOTO, kRETURN,
        kSELECT, kSWITCH, kCASE, kDEFAULT,
        kIF, kELSE,
        kFOR, kRANGE,

        oASSIGN, oVAR_ASSIGN, oCOMMA, oSEND_CHANNEL, oCOLON, oDOT, oTRIPLE_DOT,

        TYPE_NAME_DECLARATION,

        pLPAREN, pRPAREN, pLBRACK, pRBRACK, pLCURLY, pRCURLY
    );
}
