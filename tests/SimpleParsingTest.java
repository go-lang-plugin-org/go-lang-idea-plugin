import com.intellij.testFramework.ParsingTestCase;
import ro.redeul.google.go.lang.parser.GoParserDefinition;

/**
 * TODO: Document this
 * <p/>
 * Created on Dec-30-2013 00:29
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class SimpleParsingTest extends ParsingTestCase {

    public SimpleParsingTest() {
        super("", "go", new GoParserDefinition());
    }

    public void testParsingA() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "testdata";
    }

    @Override
    protected boolean skipSpaces() {
        return false;
    }

    @Override
    protected boolean includeRanges() {
        return true;
    }
}