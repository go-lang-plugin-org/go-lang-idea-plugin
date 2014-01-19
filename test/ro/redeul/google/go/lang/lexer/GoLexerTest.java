package ro.redeul.google.go.lang.lexer;

import junit.framework.TestCase;

public class GoLexerTest extends TestCase {

    public void testXX() {
        String text = "package main\n" +
            "var ( a,\n" +
            " b = 1,\n" +
            "\n" +
            " 2\n" +
            " )\n";

        GoFlexLexer flexLexer = new GoFlexLexer();

        flexLexer.start(text);
        while ( flexLexer.getTokenType() != null ) {
//            if ( flexLexer.getTokenType() != GoTokenTypes.wsNLS && flexLexer.getTokenType() != GoTokenTypes.wsWS ) {
                System.out
                      .println(
                          "" + flexLexer.getTokenType() + " -> " + flexLexer.getTokenText());
//            }
            flexLexer.advance();
        }
    }


    public void testXxx2() {
        String text = "package main\n" +
          "\n" +
          "const A // ana\n" +
          "\n";

        GoFlexLexer flexLexer = new GoFlexLexer();

        flexLexer.start(text);
        while ( flexLexer.getTokenType() != null ) {
            flexLexer.advance();
            if ( flexLexer.getTokenType() != GoTokenTypes.wsNLS && flexLexer.getTokenType() != GoTokenTypes.wsWS ) {
                System.out
                      .println(
                          "" + flexLexer.getTokenType() + " -> " + flexLexer.getTokenText());
            }
        }
    }
}
