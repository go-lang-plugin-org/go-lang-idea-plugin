package ro.redeul.google.go.formatter;

import ro.redeul.google.go.GoFormatterTestCase;

/**
 * Top level file formatter test cases.
 * <br/>
 * <p/>
 * Created on Dec-29-2013 22:27
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class GoFailingFormatterTest extends GoFormatterTestCase {

    @Override
    protected String getRelativeTestDataPath() {
        return super.getRelativeTestDataPath() + "failing/";
    }

    // https://github.com/mtoader/google-go-lang-idea-plugin/issues/449
    public void testLeadingCommentGroups() throws Exception { _test(); }

    // https://github.com/mtoader/google-go-lang-idea-plugin/issues/448
    public void testStructInconsistencies() throws Exception { _test(); }

    // https://github.com/mtoader/google-go-lang-idea-plugin/issues/450
    public void testIncompleteConstsSlightMisalignment() throws Exception { _test(); }

    // https://github.com/mtoader/google-go-lang-idea-plugin/issues/451
    public void testIncompleteVarsMisalignments() throws Exception { _test(); }

    public void testTrailingCommentsAreMisaligned() throws Exception { _test(); }

    // https://github.com/mtoader/google-go-lang-idea-plugin/issues/473
    public void testSimpleCommentsInSelectAreMisaligned() throws Exception { _test(); }

    public void testEliminateEmptyParenthesizedThings() throws Exception { _test(); }
}
