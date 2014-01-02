package ro.redeul.google.go.parsing;

import ro.redeul.google.go.GoParsingTestCase;

/**
 * TODO: Document this
 * <p/>
 * Created on Jan-02-2014 15:51
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class GoTopLevelParsingTestCase extends GoParsingTestCase {

  @Override
  protected String getRelativeTestDataPath() {
    return super.getRelativeTestDataPath() + "toplevel";
  }

  public void testPackageDeclaration() throws Exception { _test(); }

  public void testPackageKeyword() throws Exception { _test(); }

  public void testPackageWithComments() throws Exception { _test(); }

  public void testVar() throws Exception { _test(); }

  public void testVarWithMultipleDeclarations() throws Exception { _test(); }

  public void testVarMultipleOnOneLine() throws Exception { _test(); }

  public void testVarMultipleSplitAtComma() throws Exception { _test(); }

  public void testVarSingleCase2() throws Exception { _test(); }

  public void testVarWithComments() throws Exception { _test(); }

  public void testConstOneLiners() throws Exception { _test(); }
}
