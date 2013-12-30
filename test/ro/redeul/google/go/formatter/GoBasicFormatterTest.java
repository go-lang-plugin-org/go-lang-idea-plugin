package ro.redeul.google.go.formatter;

/**
 * Basic formatter tests.<br/>
 * TODO: split them based on coverage.<br/>
 * <p/>
 * Created on Dec-29-2013 22:56
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class GoBasicFormatterTest extends GoFormatterTest {

    public void testTopLevel() throws Exception { doTest(); }
    public void testBinaryExpressions() throws Exception { doTest(); }
    public void testAssignment() throws Exception { doTest(); }
    public void testType() throws Exception { doTest(); }
    public void testCastFunc() throws Exception { doTest(); }
    public void testFunctionCall() throws Exception { doTest(); }
    public void testInterfaceType() throws Exception { doTest(); }
    public void testCallParameters() throws Exception { doTest(); }
    public void testComments() throws Exception { doTest(); }
    public void testComposites() throws Exception { doTest(); }
    public void testCrlf() throws Exception { doTest(); }
    public void testSlices1() throws Exception { doTest(); }
    public void testSlices2() throws Exception { doTest(); }
    public void testFunctionDeclaration() throws Exception { doTest(); }
    // @TODO These tests are not passing yet due to refactoring done automatically by gofmt
    /*
    public void testImport() throws Exception { doTest(); }
    public void testTypeswitch() throws Exception { doTest(); }
    */

    public void testIssue_gh255() throws  Exception { doTest(); }
    public void testIssue_gh373() throws  Exception { doTest(); }
}
