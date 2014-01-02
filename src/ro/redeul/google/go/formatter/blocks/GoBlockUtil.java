package ro.redeul.google.go.formatter.blocks;

import com.google.common.collect.ImmutableMap;
import com.intellij.formatting.*;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.containers.MultiMap;
import com.intellij.util.containers.MultiMapBasedOnSet;
import org.jetbrains.annotations.NotNull;

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

    static class CustomSpacing {

        Map<IElementType, Map<IElementType, Spacing>> spacings;

        private CustomSpacing() { }

        static class Builder {
            MultiMap<IElementType, Pair<IElementType, Spacing>> entries =
                new MultiMapBasedOnSet<IElementType, Pair<IElementType, Spacing>>();

            public Builder setNone(IElementType typeChild1, IElementType typeChild2) {
                return set(typeChild1, typeChild2, Spacings.NONE);
            }

            public Builder setNone(TokenSet leftTypes, IElementType rightType) {
                return set(leftTypes, rightType, Spacings.NONE);
            }

            public Builder setBasic(IElementType typeChild1, IElementType typeChild2) {
                return set(typeChild1, typeChild2, Spacings.BASIC);
            }

            public Builder set(IElementType leftType, IElementType rightType, Spacing spacing) {
                entries.putValue(leftType, Pair.create(rightType, spacing));
                return this;
            }

            public Builder set(TokenSet leftTypes, IElementType rightType, Spacing spacing) {
                for (IElementType leftType : leftTypes.getTypes()) {
                    set(leftType, rightType, spacing);
                }
                return this;
            }

            public Builder set(IElementType leftType, TokenSet rightTypes, Spacing spacing) {
                for (IElementType rightType : rightTypes.getTypes())
                    set(leftType, rightType, spacing);

                return this;
            }

            public Builder set(TokenSet leftTypes, TokenSet rightTypes, Spacing spacing) {
                for (IElementType leftType : leftTypes.getTypes())
                    for (IElementType rightType : rightTypes.getTypes())
                        set(leftTypes, rightType, spacing);

                return this;
            }

            private Map<IElementType, Map<IElementType, Spacing>> makeEntriesImmutable() {

                ImmutableMap.Builder<IElementType, Map<IElementType, Spacing>> builder = ImmutableMap.builder();

                for (Map.Entry<IElementType, Collection<Pair<IElementType, Spacing>>> entry : entries.entrySet()) {

                    ImmutableMap.Builder<IElementType, Spacing> spacingsMapBuilder = ImmutableMap.builder();

                    for (Pair<IElementType, Spacing> pair : entry.getValue()) {
                        spacingsMapBuilder.put(pair.first, pair.second);
                    }

                    builder.put(entry.getKey(), spacingsMapBuilder.build());
                }

                return builder.build();
            }

            public CustomSpacing build() {
                CustomSpacing spacing = new CustomSpacing();

                spacing.spacings = entries.size() > 0
                    ? makeEntriesImmutable()
                    : ImmutableMap.<IElementType, Map<IElementType, Spacing>>of();
                return spacing;
            }
        }

        static Builder Builder() {
            return new CustomSpacing.Builder();
        }

        public Spacing getSpacingBetween(IElementType firstElement, IElementType secondElement) {
            Map<IElementType, Spacing> secondMap = this.spacings.get(firstElement);
            return secondMap != null
                ? secondMap.get(secondElement)
                : null;
        }
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
