package ro.redeul.google.go.lang.lexer;

import com.intellij.lexer.LookAheadLexer;
import com.intellij.lexer.MergingLexerAdapter;
import com.intellij.psi.tree.TokenSet;

import static ro.redeul.google.go.lang.lexer.GoTokenTypes.*;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 3:10:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class GoLexer extends LookAheadLexer {

    private static final TokenSet tokensToMerge = TokenSet.create(
//        mSL_COMMENT,
//        mML_COMMENT,
        wsWS, wsNLS
    );

    public GoLexer() {
        super(new MergingLexerAdapter(new GoFlexLexer(), tokensToMerge));
        // super(new MergingLexerAdapter(new GoFixElidedSemiLexer(new GoFlexLexer()), tokensToMerge));
    }
}
