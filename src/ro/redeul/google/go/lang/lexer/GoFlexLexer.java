package ro.redeul.google.go.lang.lexer;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 2:07:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class GoFlexLexer extends FlexAdapter {
    public GoFlexLexer() {
        super(new _GoLexer((Reader)null));
    }
}
