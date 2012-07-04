package ro.redeul.google.go.intentions.conversions;

import ro.redeul.google.go.intentions.GoIntentionTestCase;

public class ConvertToRawStringIntentionTest extends GoIntentionTestCase {
    public void testNormalEscape() throws Exception { doTest(); }
    public void testUnicodeEscape() throws Exception { doTest(); }
    public void testExplicitUtf8Bytes() throws Exception { doTest(); }
}
