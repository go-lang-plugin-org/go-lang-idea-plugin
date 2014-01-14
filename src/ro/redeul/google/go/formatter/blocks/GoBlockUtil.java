package ro.redeul.google.go.formatter.blocks;

import com.google.common.collect.ImmutableMap;
import com.intellij.formatting.*;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.containers.MultiMap;
import com.intellij.util.containers.MultiMapBasedOnSet;
import org.jetbrains.annotations.NotNull;
import static ro.redeul.google.go.lang.lexer.GoTokenTypeSets.*;
import static ro.redeul.google.go.lang.parser.GoElementTypes.*;

import java.util.*;

import static ro.redeul.google.go.lang.lexer.GoTokenTypeSets.INC_DEC_OPS;

/**
 * TODO: Document this
 * <p/>
 * Created on Dec-30-2013 22:56
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class GoBlockUtil {

    public interface Spacings {
        static final Spacing LINE = Spacing.createSpacing(0, 0, 1, false, 0);
        static final Spacing LINE_HOLD_BREAKS = Spacing.createSpacing(0, 0, 1, true, 1);

        static final Spacing SPACE = Spacing.createSpacing(1, 1, 0, false, 0);
        static final Spacing SPACE_HOLD_BREAKS = Spacing.createSpacing(1, 1, 0, true, 0);

        static final Spacing NONE = Spacing.createSpacing(0, 0, 0, false, 0);
        static final Spacing NONE_HOLD_BREAKS = Spacing.createSpacing(0, 0, 0, true, 1);

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
        public static final Map<Key, Alignment> EMPTY_MAP = Collections.emptyMap();

        public static final Alignment NONE = null;


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

    public static class CustomSpacing {

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

            public Builder setNone(IElementType leftType, TokenSet rightTypes) {
                return set(leftType, rightTypes, Spacings.NONE);
            }

            public Builder setNone(TokenSet leftTypes, TokenSet rightTypes) {
                return set(leftTypes, rightTypes, Spacings.NONE);
            }

            public Builder setNoneCanBreak(IElementType typeChild1, IElementType typeChild2) {
                return set(typeChild1, typeChild2, Spacings.NONE_HOLD_BREAKS);
            }

            public Builder setNoneCanBreak(TokenSet leftTypes, IElementType rightType) {
                return set(leftTypes, rightType, Spacings.NONE_HOLD_BREAKS);
            }

            public Builder setNoneCanBreak(IElementType leftType, TokenSet rightTypes) {
                return set(leftType, rightTypes, Spacings.NONE_HOLD_BREAKS);
            }

            public Builder setNoneCanBreak(TokenSet leftTypes, TokenSet rightTypes) {
                return set(leftTypes, rightTypes, Spacings.NONE_HOLD_BREAKS);
            }

            public Builder setBasic(IElementType leftType, IElementType rightType) {
                return set(leftType, rightType, Spacings.SPACE);
            }

            public Builder setBasic(IElementType leftType, TokenSet rightTypes) {
                return set(leftType, rightTypes, Spacings.SPACE);
            }

            public Builder setBasic(TokenSet leftTypes, IElementType rightType) {
                return set(leftTypes, rightType, Spacings.SPACE);
            }

            public Builder setBasic(TokenSet leftTypes, TokenSet rightTypes) {
                return set(leftTypes, rightTypes, Spacings.SPACE);
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

    public static interface CustomSpacings {

        static final CustomSpacing STMT_INC_DEC = CustomSpacing.Builder()
            .setNone(EXPRESSIONS, INC_DEC_OPS)
            .setNone(INC_DEC_OPS, oSEMI)
            .build();

        static final CustomSpacing SEND_STATEMENT = CustomSpacing.Builder()
            .setNone(EXPRESSIONS, INC_DEC_OPS)
            .setNone(INC_DEC_OPS, oSEMI)
            .build();

        static final CustomSpacing NO_SPACE_BEFORE_COMMA = CustomSpacing.Builder()
            .setNone(LITERAL_IDENTIFIER, oCOMMA)
            .setNone(EXPRESSIONS, oCOMMA)
            .setNone(oTRIPLE_DOT, TYPES)
            .setNone(FUNCTION_PARAMETER, oCOMMA)
            .set(oCOMMA, LITERAL_IDENTIFIER, Spacings.SPACE_HOLD_BREAKS)
            .set(oCOMMA, FUNCTION_PARAMETER, Spacings.SPACE_HOLD_BREAKS)
            .build();

        static final CustomSpacing CLAUSES_COLON = CustomSpacing.Builder()
            .setNone(EXPRESSIONS, oCOLON)
            .setNone(EXPRESSIONS, oCOMMA)
            .setNone(TYPES, oCOLON)
            .setNone(TYPES, oCOMMA)
            .setNone(TYPE_LIST, oCOLON)
            .setNone(SELECT_COMM_CLAUSE_RECV_EXPR, oCOLON)
            .setNone(kDEFAULT, oCOLON)
            .build();

        static final CustomSpacing STMT_IF = CustomSpacing.Builder()
            .setNone(STMTS, oSEMI)
            .setNone(EXPRESSIONS, oSEMI)
            .build();

        static final CustomSpacing STMT_FOR = CustomSpacing.Builder()
            .setNone(EXPRESSIONS, oCOMMA)
            .setNone(LITERAL_IDENTIFIER, oCOMMA)
            .setNone(STMTS, oSEMI)
            .setNone(EXPRESSIONS, oSEMI)
            .build();

        static final CustomSpacing UNARY_EXPRESSION = CustomSpacing.Builder()
            .setNone(UNARY_OPERATORS, EXPRESSIONS)
            .build();

        static final CustomSpacing SLICE_EXPRESSION_EXPANDED = CustomSpacing.Builder()
            .setBasic(oCOLON, EXPRESSIONS_BINARY)
            .setBasic(EXPRESSIONS_BINARY, oCOLON)
            .build();

        static final CustomSpacing CALL_OR_CONVERSION = CustomSpacing.Builder()
            .setNone(EXPRESSIONS, pLPAREN)
            .setNone(TYPES, pLPAREN)
            .setNone(pLPAREN, TYPES)
            .setNoneCanBreak(pLPAREN, EXPRESSIONS)
            .setNone(EXPRESSIONS, pRPAREN)
            .setNone(pLPAREN, pRPAREN)
            .setNone(EXPRESSIONS, oCOMMA)
            .setNone(TYPES, oCOMMA)
            .set(oCOMMA, EXPRESSIONS, Spacings.SPACE_HOLD_BREAKS)
            .build();

        static final CustomSpacing TYPE_ARRAY = CustomSpacing.Builder()
            .setNone(pLBRACK, EXPRESSIONS)
            .setNone(EXPRESSIONS, pRBRACK)
            .setNone(pLBRACK, oTRIPLE_DOT)
            .setNone(oTRIPLE_DOT, pRBRACK)
            .setNone(pRBRACK, TYPES)
            .build();

        static final CustomSpacing TYPE_POINTER = CustomSpacing.Builder()
            .setNone(oMUL, TYPES)
            .build();

        static final CustomSpacing TYPE_SLICE = CustomSpacing.Builder()
            .setNone(pLBRACK, pRBRACK)
            .setNone(pRBRACK, TYPES)
            .build();

        static final CustomSpacing TYPE_MAP = CustomSpacing.Builder()
            .setNone(kMAP, pLBRACK)
            .setNone(pLBRACK, TYPES)
            .setNone(TYPES, pRBRACK)
            .setNone(pRBRACK, TYPES)
            .build();

        static final CustomSpacing TYPE_CHANNEL = CustomSpacing.Builder()
            .setNone(kCHAN, oSEND_CHANNEL)
            .setNone(oSEND_CHANNEL, kCHAN)
            .build();

        static final CustomSpacing TYPE_PARENTHESISED = CustomSpacing.Builder()
            .setNone(pLPAREN, pRPAREN)
            .setNone(pLPAREN, TYPES)
            .setNone(TYPES, pRPAREN)
            .build();

        static final CustomSpacing TYPE_FUNCTION = CustomSpacing.Builder()
            .setNone(kFUNC, pLPAREN)
            .setNone(pLPAREN, pRPAREN)
            .setNone(pLPAREN, FUNCTION_PARAMETER_LIST)
            .setNone(FUNCTION_PARAMETER_LIST, pRPAREN)
            .build();

        static final CustomSpacing FUNCTION_RESULT = CustomSpacing.Builder()
            .setNone(pLPAREN, pRPAREN)
            .setNone(pLPAREN, FUNCTION_PARAMETER_LIST)
            .setNone(FUNCTION_PARAMETER_LIST, pRPAREN)
            .build();

        static final CustomSpacing STMT_SWITCH_EXPR = CustomSpacing.Builder()
            .set(pLCURLY, pRCURLY, Spacings.LINE_HOLD_BREAKS)
            .setNone(STMTS, oSEMI)
            .build();

        static final CustomSpacing STMT_SWITCH_TYPE = CustomSpacing.Builder()
            .set(pLCURLY, pRCURLY, Spacings.LINE_HOLD_BREAKS)
            .setNone(STMTS, oSEMI)
            .build();

        static final CustomSpacing STMT_SELECT = CustomSpacing.Builder()
            .setNone(pLCURLY, pRCURLY)
            .build();

        static final CustomSpacing SWITCH_TYPE_GUARD = CustomSpacing.Builder()
            .setNone(EXPRESSIONS, oDOT)
            .setNone(oDOT, pLPAREN)
            .setNone(pLPAREN, kTYPE)
            .setNone(kTYPE, pRPAREN)
            .build();

        static final CustomSpacing LISTS = CustomSpacing.Builder()
            .setNone(TYPES, oCOMMA)
            .setNone(EXPRESSIONS, oCOMMA)
            .setNone(FUNCTION_PARAMETER, oCOMMA)
            .setNone(FUNCTION_PARAMETER_VARIADIC, oCOMMA)
            .set(oCOMMA, EXPRESSIONS, Spacings.SPACE_HOLD_BREAKS)
            .set(oCOMMA, FUNCTION_PARAMETER, Spacings.SPACE_HOLD_BREAKS)
            .set(oCOMMA, FUNCTION_PARAMETER_VARIADIC, Spacings.SPACE_HOLD_BREAKS)
            .build();
    }
}
