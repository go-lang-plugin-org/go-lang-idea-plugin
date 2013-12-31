package ro.redeul.google.go.formatter;

/**
 * Top level file formatter test cases.
 * <br/>
 * <p/>
 * Created on Dec-29-2013 22:27
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public abstract class GoImportsFormatterTest extends GoFormatterTestCase {

  @Override
  protected String getRelativeTestDataPath() {
    return super.getRelativeTestDataPath() + "imports/";
  }

  public void testSimpleImport() throws Exception { _test(); }

  public void testRemoveEmptyLines() throws Exception { _test(); }

}
