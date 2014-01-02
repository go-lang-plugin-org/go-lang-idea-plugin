package ro.redeul.google.go;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.testFramework.ParsingTestCase;
import com.intellij.testFramework.TestDataFile;
import org.jetbrains.annotations.NonNls;
import ro.redeul.google.go.lang.parser.GoParserDefinition;

import java.io.File;
import java.io.IOException;

/**
 * TODO: Document this
 * <p/>
 * Created on Jan-02-2014 15:48
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public abstract class GoParsingTestCase extends ParsingTestCase {
  public GoParsingTestCase() {
    super("", "go", new GoParserDefinition());
  }

  @Override
  protected boolean skipSpaces() {
    return true;
  }

  @Override
  protected boolean includeRanges() {
    return true;
  }

  @Override
  final protected String getTestDataPath() {
    return FileUtil.normalize(String.format("%s/%s", "testdata", getRelativeTestDataPath()));
  }

  protected String getRelativeTestDataPath() {
    return "parsing/";
  }

  protected String loadFile(@NonNls @TestDataFile String name) throws IOException {
    String text = FileUtil.loadFile(new File(myFullDataPath, name), CharsetToolkit.UTF8);
    text = StringUtil.convertLineSeparators(text);
    return text;
  }

  protected void _test() throws Exception {
    doTest("_psi");
  }
}
