package ro.redeul.google.go.intentions.parenthesis;

import ro.redeul.google.go.intentions.GoIntentionTestCase;

public class AddDeclarationParenthesesIntentionTest extends GoIntentionTestCase {
    public void testVar() throws Exception { doTest(); }
    public void testImport() throws Exception { doTest(); }
    public void testConst() throws Exception { doTest(); }
}
