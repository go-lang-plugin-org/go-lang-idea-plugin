package ro.redeul.google.go.lang.lexer;

import com.intellij.lexer.DelegateLexer;
import com.intellij.lexer.Lexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 25, 2010
 * Time: 12:56:23 AM
 * To change this template use File | Settings | File Templates.
 */
class GoFixElidedSemiLexer extends DelegateLexer implements GoTokenTypes {

    private IElementType lastElementType;

    private final TokenSet elidingTokens = TokenSet.create(
            mIDENT,
            litINT, litOCT, litHEX, litFLOAT,
            litDECIMAL_I, litFLOAT_I,
            litCHAR, litSTRING,
            kBREAK, kCONTINUE, kFALLTHROUGH, kRETURN,
            oPLUS_PLUS, oMINUS_MINUS,
            pRCURLY, pRBRACK, pRPAREN
    );

    public GoFixElidedSemiLexer(Lexer delegate) {
        super(delegate);
    }

    @Override
    public void advance() {        
        if ( ! isElidedPlace() ) {
            lastElementType = super.getTokenType() == wsWS ? lastElementType : super.getTokenType(); 
            super.advance();
        } else {
            lastElementType = getTokenType();
        }
    }

    @Override
    public IElementType getTokenType() {
        if (isElidedPlace()) {
            return oSEMI;
        }

        return super.getTokenType();
    }

    @Override
    public int getTokenEnd() {
        if ( isElidedPlace() ) {
            return super.getTokenStart();
        }

        return super.getTokenEnd();
    }

    private boolean isElidedPlace() {
        IElementType actualToken = super.getTokenType();
        return actualToken != null && lastElementType != null &&
                actualToken.equals(GoTokenTypes.wsNLS) && elidingTokens.contains(lastElementType);
    }
}
