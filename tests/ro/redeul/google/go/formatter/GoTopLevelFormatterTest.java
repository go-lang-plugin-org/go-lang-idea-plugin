package ro.redeul.google.go.formatter;

/**
 * Top level file formatter test cases.
 * <br/>
 * <p/>
 * Created on Dec-29-2013 22:27
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class GoTopLevelFormatterTest extends GoFormatterTestCase {

  @Override
  protected String getRelativeTestDataPath() {
    return super.getRelativeTestDataPath() + "toplevel/";
  }

  public void testBasic() throws Exception { _test(); }

  public void testBasicWithLineComments() throws Exception { _test(); }

//    public void testBasicWithMultiLineComments() throws Exception { _test(); }

  public void testCommentAtTheStart() throws Exception { _test(); }

  public void testEofNoWhiteSpace() throws Exception { _test(); }

  public void testEofTooMuchWhiteSpace() throws Exception { _test(); }

  public void testAlternatingComments() throws Exception { _test(); }

  public void testImportSimple() throws Exception { _test(); }

  public void testImportRemoveEmptyLines() throws Exception { _test(); }
}
