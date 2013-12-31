package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * TODO: Document this
 * <p/>
 * Created on Dec-30-2013 22:56
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class GoBlockUtil {
  public interface Spacings {
    static final Spacing ONE_LINE = Spacing.createSpacing(0, 0, 1, false, 0);
    static final Spacing ONE_LINE_KEEP_BREAKS = Spacing.createSpacing(0, 0, 1, true, 1);
    static final Spacing BASIC = Spacing.createSpacing(1, 1, 0, false, 0);
    static final Spacing BASIC_KEEP_BREAKS = Spacing.createSpacing(1, 1, 0, true, 0);
    static final Spacing NONE = Spacing.createSpacing(0, 0, 0, false, 0);
    static final Spacing NONE_KEEP_BREAKS = Spacing.createSpacing(0, 0, 0, true, 0);
    static final Spacing EMPTY_LINE = Spacing.createSpacing(0, 0, 2, false, 0);
  }

  public interface Indents {

    static final Indent NONE = Indent.getNoneIndent();
    static final Indent NONE_ABSOLUTE = Indent.getAbsoluteNoneIndent();

    static final Indent NORMAL = Indent.getNormalIndent();
    static final Indent NORMAL_RELATIVE = Indent.getNormalIndent(true);
  }

  public interface Wraps {
    static final Wrap NONE = Wrap.createWrap(WrapType.NONE, false);
  }

  static public class Alignments {

    public enum Key {
      Operator, Value, Type, Comments
    }

    static final EnumSet<Key> EMPTY_KEY_SET = EnumSet.noneOf(Key.class);
    static final Map<Key, Alignment> EMPTY_MAP = Collections.emptyMap();

    static final Alignment NONE = null;


    public static Alignment one() { return Alignment.createAlignment(true); }

    public static Alignment[] set(Alignment... alignments) {
      return alignments;
    }

    public static Alignment[] set(int count) {
      Alignment[] alignments = new Alignment[count];

      for (int i = 0; i < alignments.length; i++) {
        alignments[i] = one();
      }

      return alignments;
    }

    public static <Key extends Enum<Key>> Map<Key, Alignment> set(@NotNull Set<Key> keys) {
      Map<Key, Alignment> entries = new HashMap<Key, Alignment>();

      for (Key enumKey : keys) {
        entries.put(enumKey, one());
      }

      return entries;
    }
  }
}
