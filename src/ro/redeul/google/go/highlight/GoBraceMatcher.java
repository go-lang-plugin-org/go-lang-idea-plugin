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
    };

    public BracePair[] getPairs() {
        return PAIRS;
    }

    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType tokenType, @Nullable IElementType contextType) {
        return tokenType != GoTokenTypes.pRPAREN && tokenType != GoTokenTypes.pRBRACK && tokenType != GoTokenTypes.pRCURLY;
        //return true;
    }

    public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
        return openingBraceOffset;
    }
}
