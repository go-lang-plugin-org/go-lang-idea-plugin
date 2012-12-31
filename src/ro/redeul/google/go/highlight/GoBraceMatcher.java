package ro.redeul.google.go.highlight;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;

public class GoBraceMatcher implements PairedBraceMatcher {

    private static final BracePair[] PAIRS = {
        new BracePair(GoTokenTypes.pLPAREN, GoTokenTypes.pRPAREN, true),
        new BracePair(GoTokenTypes.pLBRACK, GoTokenTypes.pRBRACK, false),
        new BracePair(GoTokenTypes.pLCURLY, GoTokenTypes.pRCURLY, true),

//        new BracePair(GroovyDocTokenTypes.mGDOC_INLINE_TAG_START, GroovyDocTokenTypes.mGDOC_INLINE_TAG_END, true),
//        new BracePair(GroovyDocTokenTypes.mGDOC_TAG_VALUE_LPAREN, GroovyDocTokenTypes.mGDOC_TAG_VALUE_RPAREN, false),
//        new BracePair(GroovyTokenTypes.mGSTRING_BEGIN, GroovyTokenTypes.mGSTRING_END, false),
//        new BracePair(GroovyTokenTypes.mREGEX_BEGIN, GroovyTokenTypes.mREGEX_END, false)
    };

    public BracePair[] getPairs() {
        return PAIRS;
    }

    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType tokenType, @Nullable IElementType contextType) {
        return tokenType == null ||
//            || GoTokenTypes.wsWS == tokenType
//            || GoTokenTypes.wsNLS == tokenType
//            || GoTokenTypeSets.COMMENTS.contains(tokenType)
//            || tokenType == GoTokenTypes.oSEMI
//            || tokenType == GoTokenTypes.oCOMMA
                ( tokenType != GoTokenTypes.pRPAREN   && tokenType != GoTokenTypes.pRBRACK && tokenType != GoTokenTypes.pRCURLY)
                ;
    }

    public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
        return openingBraceOffset;
    }
}
