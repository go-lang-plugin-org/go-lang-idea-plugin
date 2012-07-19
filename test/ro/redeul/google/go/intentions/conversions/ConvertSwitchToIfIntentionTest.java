package ro.redeul.google.go.intentions.conversions;

import ro.redeul.google.go.intentions.GoIntentionTestCase;

public class ConvertSwitchToIfIntentionTest extends GoIntentionTestCase {
    public void testNormalSwitch() throws Exception { doTest(); }
    public void testEmptySwitch() throws Exception { doTest(); }
    public void testTrueExpression() throws Exception { doTest(); }
    public void testFalseExpression() throws Exception { doTest(); }
    public void testSwitchWithoutCase() throws Exception { doTest(); }
    public void testSwitchWithoutExpression() throws Exception { doTest(); }
    public void testSwitchWithoutDefault() throws Exception { doTest(); }
    public void testSwitchWithoutSimpleStatement() throws Exception { doTest(); }
}
