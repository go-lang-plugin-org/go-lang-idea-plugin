package ro.redeul.google.go.lang.lexer;

import junit.framework.TestCase;

public class GoLexerTest extends TestCase {

    public void testXX() {
        String text = "" +
            "package main\n" +
            "func chcopy() {\n" +
            "for;;{\n" +
            "\"ad\\\"sfasdf\"" +
            "\"\\\\\"\n" +
            "}\n" +
            "}\n" +
            "\n" +
            "func usage() {\n" +
            "\tfmt.Fprintf(stderr, \"usage: yacc [-o output] [-v parsetable] input\\n\")\n" +
            "\texit(1)\n" +
            "}\n";

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
