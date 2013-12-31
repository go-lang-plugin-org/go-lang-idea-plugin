package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;

/**
 * TODO: Document this
 * <p/>
 * Created on Dec-30-2013 22:56
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class GoBlockUtil {
  public interface Spacings {
    static final Spacing ONE_LINE = Spacing.createSpacing(0, 0,  1, false, 0);
    static final Spacing ONE_LINE_KEEP_BREAKS = Spacing.createSpacing(0, 0, 1, true, 1);
    static final Spacing BASIC = Spacing.createSpacing(1, 1, 0, false, 0);
    static final Spacing BASIC_KEEP_BREAKS = Spacing.createSpacing(1, 1, 0, true, 0);
    static final Spacing NONE = Spacing.createSpacing(0, 0, 0, false, 0);
    static final Spacing NONE_KEEP_BREAKS = Spacing.createSpacing(0, 0, 0, true, 0);
    static final Spacing EMPTY_LINE = Spacing.createSpacing(0, 0, 2, false, 0);
  }

  public interface Indents {
    static final Indent NONE = Indent.getNoneIndent();
    static final Indent NORMAL = Indent.getNormalIndent();
    static final Indent NORMAL_RELATIVE = Indent.getNormalIndent(true);
  }

}
