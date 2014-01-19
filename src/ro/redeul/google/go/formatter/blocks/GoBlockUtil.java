package ro.redeul.google.go.formatter.blocks;

import com.google.common.collect.ImmutableMap;
import com.intellij.formatting.*;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.containers.MultiMap;
import com.intellij.util.containers.MultiMapBasedOnSet;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypeSets;
import ro.redeul.google.go.lang.parser.GoElementTypes;

import java.util.*;

import static ro.redeul.google.go.lang.lexer.GoTokenTypeSets.*;
import static ro.redeul.google.go.lang.lexer.GoTokenTypeSets.kCHAN;
import static ro.redeul.google.go.lang.lexer.GoTokenTypeSets.kDEFAULT;
import static ro.redeul.google.go.lang.lexer.GoTokenTypeSets.kFUNC;
import static ro.redeul.google.go.lang.lexer.GoTokenTypeSets.kIMPORT;
import static ro.redeul.google.go.lang.lexer.GoTokenTypeSets.kMAP;
import static ro.redeul.google.go.lang.lexer.GoTokenTypeSets.kRETURN;
import static ro.redeul.google.go.lang.lexer.GoTokenTypeSets.kTYPE;
import static ro.redeul.google.go.lang.lexer.GoTokenTypeSets.mML_COMMENT;
import static ro.redeul.google.go.lang.lexer.GoTokenTypeSets.oCOLON;
import static ro.redeul.google.go.lang.lexer.GoTokenTypeSets.oCOMMA;
import static ro.redeul.google.go.lang.lexer.GoTokenTypeSets.oDOT;
import static ro.redeul.google.go.lang.lexer.GoTokenTypeSets.oMUL;
import static ro.redeul.google.go.lang.lexer.GoTokenTypeSets.oSEMI;
import static ro.redeul.google.go.lang.lexer.GoTokenTypeSets.oSEND_CHANNEL;
import static ro.redeul.google.go.lang.lexer.GoTokenTypeSets.oTRIPLE_DOT;
import static ro.redeul.google.go.lang.lexer.GoTokenTypeSets.pLBRACK;
import static ro.redeul.google.go.lang.lexer.GoTokenTypeSets.pLCURLY;
import static ro.redeul.google.go.lang.lexer.GoTokenTypeSets.pLPAREN;
import static ro.redeul.google.go.lang.lexer.GoTokenTypeSets.pRBRACK;
import static ro.redeul.google.go.lang.lexer.GoTokenTypeSets.pRCURLY;
import static ro.redeul.google.go.lang.lexer.GoTokenTypeSets.pRPAREN;
import static ro.redeul.google.go.lang.parser.GoElementTypes.*;
import static ro.redeul.google.go.lang.parser.GoElementTypes.COMMENTS;

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

        static final Indent CONTINUATION = Indent.getContinuationIndent();
        static final Indent CONTINUATION_RELATIVE = Indent.getContinuationIndent(true);
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

            public Builder none(IElementType typeChild1, IElementType typeChild2) {
                return set(typeChild1, typeChild2, Spacings.NONE);
            }

            public Builder none(TokenSet leftTypes, IElementType rightType) {
                return set(leftTypes, rightType, Spacings.NONE);
            }

            public Builder none(IElementType leftType, TokenSet rightTypes) {
                return set(leftType, rightTypes, Spacings.NONE);
            }

            public Builder none(TokenSet leftTypes, TokenSet rightTypes) {
                return set(leftTypes, rightTypes, Spacings.NONE);
            }

            public Builder noneWithBreaks(IElementType typeChild1, IElementType typeChild2) {
                return set(typeChild1, typeChild2, Spacings.NONE_HOLD_BREAKS);
            }

            public Builder noneWithBreaks(TokenSet leftTypes, IElementType rightType) {
                return set(leftTypes, rightType, Spacings.NONE_HOLD_BREAKS);
            }

            public Builder noneWithBreaks(IElementType leftType, TokenSet rightTypes) {
                return set(leftType, rightTypes, Spacings.NONE_HOLD_BREAKS);
            }

            public Builder noneWithBreaks(TokenSet leftTypes, TokenSet rightTypes) {
                return set(leftTypes, rightTypes, Spacings.NONE_HOLD_BREAKS);
            }

            public Builder space(IElementType leftType, IElementType rightType) {
                return set(leftType, rightType, Spacings.SPACE);
            }

            public Builder space(IElementType leftType, TokenSet rightTypes) {
                return set(leftType, rightTypes, Spacings.SPACE);
            }

            public Builder space(TokenSet leftTypes, IElementType rightType) {
                return set(leftTypes, rightType, Spacings.SPACE);
            }

            public Builder space(TokenSet leftTypes, TokenSet rightTypes) {
                return set(leftTypes, rightTypes, Spacings.SPACE);
            }

            public Builder spaceWithBreaks(IElementType leftType, IElementType rightType) {
                return set(leftType, rightType, Spacings.SPACE_HOLD_BREAKS);
            }

            public Builder spaceWithBreaks(IElementType leftType, TokenSet rightTypes) {
                return set(leftType, rightTypes, Spacings.SPACE_HOLD_BREAKS);
            }

            public Builder spaceWithBreaks(TokenSet leftTypes, IElementType rightType) {
                return set(leftTypes, rightType, Spacings.SPACE_HOLD_BREAKS);
            }

            public Builder spaceWithBreaks(TokenSet leftTypes, TokenSet rightTypes) {
                return set(leftTypes, rightTypes, Spacings.SPACE_HOLD_BREAKS);
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

        static final CustomSpacing FUNCTION = CustomSpacing.Builder()
            .space(kFUNC, LITERAL_IDENTIFIER)               // func| a() () { }
            .space(kFUNC, METHOD_RECEIVER)                   // func| (int) f() {}
            .space(METHOD_RECEIVER, LITERAL_IDENTIFIER)      // func (int)| main()
            .none(LITERAL_IDENTIFIER, pLPAREN)              // func main|() () { }
            .none(kFUNC, pLPAREN)                           // func|() { }

            .none(pLPAREN, pRPAREN)                         // func (|)

            .none(pLPAREN, FUNCTION_PARAMETER_LIST)         // func (|a)
            .none(FUNCTION_PARAMETER_LIST, pRPAREN)         // func (a|)

            .space(pRPAREN, BLOCK_STATEMENT)                // func (a)| {}
            .space(pRPAREN, FUNCTION_RESULT)                // func (a)|() {}
            .space(FUNCTION_RESULT, BLOCK_STATEMENT)        // func (a)| {}
            .build();

        static final CustomSpacing FUNCTION_METHOD_RECEIVER = CustomSpacing.Builder()
            .noneWithBreaks(pLPAREN, TYPES)
            .none(TYPES, pRPAREN)
            .noneWithBreaks(pLPAREN, LITERAL_IDENTIFIER)
            .space(LITERAL_IDENTIFIER, TYPES)
            .build();

        static final CustomSpacing FUNCTION_RESULT_SPACING = CustomSpacing.Builder()
            .none(pLPAREN, pRPAREN)
            .none(pLPAREN, FUNCTION_PARAMETER_LIST)
            .none(FUNCTION_PARAMETER_LIST, pRPAREN)
            .build();

        static final CustomSpacing STMT_INC_DEC = CustomSpacing.Builder()
            .none(EXPRESSIONS, INC_DEC_OPS)
            .none(INC_DEC_OPS, oSEMI)
            .build();

        static final CustomSpacing STMT_SEND = CustomSpacing.Builder()
            .none(EXPRESSIONS, INC_DEC_OPS)
            .none(INC_DEC_OPS, oSEMI)
            .build();

        static final CustomSpacing STMT_IF = CustomSpacing.Builder()
            .none(STMTS, oSEMI)
            .none(EXPRESSIONS, oSEMI)
            .build();

        static final CustomSpacing STMT_FOR = CustomSpacing.Builder()
            .none(EXPRESSIONS, oCOMMA)
            .none(LITERAL_IDENTIFIER, oCOMMA)
            .none(STMTS, oSEMI)
            .none(EXPRESSIONS, oSEMI)
            .build();

        static final CustomSpacing NO_SPACE_BEFORE_COMMA = CustomSpacing.Builder()
            .none(LITERAL_IDENTIFIER, oCOMMA)
            .none(EXPRESSIONS, oCOMMA)
            .none(oTRIPLE_DOT, TYPES)
            .none(GoElementTypes.FUNCTION_PARAMETER, oCOMMA)
            .spaceWithBreaks(oCOMMA, LITERAL_IDENTIFIER)
            .spaceWithBreaks(oCOMMA, GoElementTypes.FUNCTION_PARAMETER)
            .build();

        static final CustomSpacing FUNCTION_PARAMETER = CustomSpacing.Builder()
            .none(LITERAL_IDENTIFIER, oCOMMA)
            .spaceWithBreaks(oCOMMA, LITERAL_IDENTIFIER)
            .none(oTRIPLE_DOT, TYPES)
            .build();

        static final CustomSpacing CLAUSES_COLON = CustomSpacing.Builder()
            .none(EXPRESSIONS, oCOLON)
            .none(EXPRESSIONS, oCOMMA)
            .none(TYPES, oCOLON)
            .none(TYPES, oCOMMA)
            .none(TYPE_LIST, oCOLON)
            .none(SELECT_COMM_CLAUSE_RECV_EXPR, oCOLON)
            .none(kDEFAULT, oCOLON)
            .build();

        static final CustomSpacing TYPE_ARRAY = CustomSpacing.Builder()
            .none(pLBRACK, EXPRESSIONS)
            .none(EXPRESSIONS, pRBRACK)
            .none(pLBRACK, oTRIPLE_DOT)
            .none(oTRIPLE_DOT, pRBRACK)
            .none(pRBRACK, TYPES)
            .build();

        static final CustomSpacing TYPE_POINTER = CustomSpacing.Builder()
            .none(oMUL, TYPES)
            .build();

        static final CustomSpacing TYPE_SLICE = CustomSpacing.Builder()
            .none(pLBRACK, pRBRACK)
            .none(pRBRACK, TYPES)
            .build();

        static final CustomSpacing TYPE_MAP = CustomSpacing.Builder()
            .none(kMAP, pLBRACK)
            .none(pLBRACK, TYPES)
            .none(TYPES, pRBRACK)
            .none(pRBRACK, TYPES)
            .build();

        static final CustomSpacing TYPE_CHANNEL = CustomSpacing.Builder()
            .none(kCHAN, oSEND_CHANNEL)
            .none(oSEND_CHANNEL, kCHAN)
            .build();

        static final CustomSpacing TYPE_PARENTHESISED = CustomSpacing.Builder()
            .none(pLPAREN, pRPAREN)
            .none(pLPAREN, TYPES)
            .none(TYPES, pRPAREN)
            .build();

        static final CustomSpacing TYPE_FUNCTION = FUNCTION;

        static final CustomSpacing STMT_RETURN = CustomSpacing.Builder()
            .space(kRETURN, EXPRESSIONS)
            .space(EXPRESSIONS, GoTokenTypeSets.COMMENTS)
            .build();

        static final CustomSpacing STMT_SWITCH_EXPR = CustomSpacing.Builder()
            .set(pLCURLY, pRCURLY, Spacings.LINE_HOLD_BREAKS)
            .none(STMTS, oSEMI)
            .build();

        static final CustomSpacing STMT_SWITCH_TYPE = CustomSpacing.Builder()
            .set(pLCURLY, pRCURLY, Spacings.LINE_HOLD_BREAKS)
            .none(STMTS, oSEMI)
            .build();

        static final CustomSpacing STMT_SELECT = CustomSpacing.Builder()
            .none(pLCURLY, pRCURLY)
            .build();

        static final CustomSpacing STMT_BLOCK = CustomSpacing.Builder()
            .set(pLCURLY, pRCURLY, Spacings.LINE_HOLD_BREAKS)
            .set(pLCURLY, STMTS, Spacings.LINE_HOLD_BREAKS)
            .set(pLCURLY, COMMENTS, Spacings.LINE_HOLD_BREAKS)
            .set(STMTS, pRCURLY, Spacings.LINE_HOLD_BREAKS)
            .set(COMMENTS, pRCURLY, Spacings.LINE_HOLD_BREAKS)
            .set(STMTS, STMTS, Spacings.LINE_HOLD_BREAKS)
            .set(mML_COMMENT, STMTS, Spacings.SPACE_HOLD_BREAKS)
            .build();

        static final CustomSpacing STMT_BLOCK_COMPACT = CustomSpacing.Builder()
            .set(pLCURLY, pRCURLY, Spacings.NONE_HOLD_BREAKS)
            .set(pLCURLY, STMTS, Spacings.SPACE)
            .set(STMTS, pRCURLY, Spacings.SPACE)
            .set(pLCURLY, mML_COMMENT, Spacings.SPACE)
            .set(STMTS, STMTS, Spacings.SPACE_HOLD_BREAKS)
            .set(mML_COMMENT, STMTS, Spacings.SPACE_HOLD_BREAKS)
            .build();

        static final CustomSpacing SWITCH_TYPE_GUARD = CustomSpacing.Builder()
            .none(EXPRESSIONS, oDOT)
            .none(oDOT, pLPAREN)
            .none(pLPAREN, kTYPE)
            .none(kTYPE, pRPAREN)
            .build();

        static final CustomSpacing LISTS = CustomSpacing.Builder()
            .none(TYPES, oCOMMA)
            .none(EXPRESSIONS, oCOMMA)
            .none(GoElementTypes.FUNCTION_PARAMETER, oCOMMA)
            .none(FUNCTION_PARAMETER_VARIADIC, oCOMMA)
            .set(oCOMMA, EXPRESSIONS, Spacings.SPACE_HOLD_BREAKS)
            .set(oCOMMA, GoElementTypes.FUNCTION_PARAMETER, Spacings.SPACE_HOLD_BREAKS)
            .set(oCOMMA, FUNCTION_PARAMETER_VARIADIC, Spacings.SPACE_HOLD_BREAKS)
            .build();

        static final CustomSpacing EXPR_UNARY = CustomSpacing.Builder()
            .none(UNARY_OPERATORS, EXPRESSIONS)
            .build();

        static final CustomSpacing EXPR_SLICE_EXPANDED = CustomSpacing.Builder()
            .space(oCOLON, EXPRESSIONS_BINARY)
            .space(EXPRESSIONS_BINARY, oCOLON)
            .build();

        static final CustomSpacing EXPR_CALL_OR_CONVERSION = CustomSpacing.Builder()
            .none(EXPRESSIONS, pLPAREN)
            .none(TYPES, pLPAREN)
            .none(pLPAREN, TYPES)
            .noneWithBreaks(pLPAREN, EXPRESSIONS)
            .none(EXPRESSIONS, pRPAREN)
            .none(pLPAREN, pRPAREN)
            .none(EXPRESSIONS, oCOMMA)
            .none(TYPES, oCOMMA)
            .set(oCOMMA, EXPRESSIONS, Spacings.SPACE_HOLD_BREAKS)
            .build();

        static final CustomSpacing EXPR_SELECTOR = CustomSpacing.Builder()
            .none(EXPRESSIONS, oDOT)         // a|.s
            .none(oDOT, LITERAL_IDENTIFIER)  // a.|s
            .build();

        static final CustomSpacing EXPR_INDEX = CustomSpacing.Builder()
            .none(EXPRESSIONS, pLBRACK) // a|[i]
            .none(pLBRACK, EXPRESSIONS) // a[|i]
            .none(EXPRESSIONS, pRBRACK) // a[i|]
            .build();

        static final CustomSpacing EXPR_PARENTHESISED = CustomSpacing.Builder()
            .none(pLPAREN, EXPRESSIONS)  // (|e)
            .none(EXPRESSIONS, pRPAREN)  // (e|)
            .build();

        static final CustomSpacing EXPR_TYPE_ASSERT = CustomSpacing.Builder()
            .none(EXPRESSIONS, oDOT) // a|.(type)
            .none(oDOT, pLPAREN)     // a.|(type)
            .none(pLPAREN, TYPES)    // a.(|type)
            .none(TYPES, pRPAREN)    // a.(type|)
            .build();

        static final CustomSpacing EXPR_BINARY = CustomSpacing.Builder()
            .spaceWithBreaks(OPS_ADD, EXPRESSIONS)
            .spaceWithBreaks(OPS_MUL, EXPRESSIONS)
            .spaceWithBreaks(OPS_REL, EXPRESSIONS)
            .spaceWithBreaks(OPS_LOG_AND, EXPRESSIONS)
            .spaceWithBreaks(OPS_LOG_OR, EXPRESSIONS)
            .build();

        static final CustomSpacing EXPR_BINARY_COMPACT = CustomSpacing.Builder()
            .none(EXPRESSIONS, OPS_ADD)
            .noneWithBreaks(OPS_ADD, EXPRESSIONS)
            .none(EXPRESSIONS, OPS_MUL)
            .noneWithBreaks(OPS_MUL, EXPRESSIONS)
            .spaceWithBreaks(OPS_LOG_AND, EXPRESSIONS)
            .spaceWithBreaks(OPS_LOG_OR, EXPRESSIONS)
            .spaceWithBreaks(OPS_REL, EXPRESSIONS)
            .build();

        static final CustomSpacing LITERAL_FUNCTION = FUNCTION;

        static final CustomSpacing IMPORT_DECL = CustomSpacing.Builder()
            .space(kIMPORT, IMPORT_DECLARATION)
            .none(IMPORT_DECLARATION, oSEMI)
            .space(IMPORT_DECLARATION, COMMENTS)
            .space(kIMPORT, pLPAREN)
            .set(pLPAREN, IMPORT_DECLARATION, Spacings.LINE_HOLD_BREAKS)
            .set(IMPORT_DECLARATION, IMPORT_DECLARATION, Spacings.LINE_HOLD_BREAKS)
            .set(IMPORT_DECLARATION, pRPAREN, Spacings.LINE_HOLD_BREAKS)
            .space(pRPAREN, COMMENTS)
            .build();

        static final CustomSpacing IMPORT_SPEC = CustomSpacing.Builder()
            .space(PACKAGE_REFERENCE, LITERAL_STRING)
            .space(LITERAL_STRING, COMMENTS)
            .build();

        static final CustomSpacing CONST_DECL = CustomSpacing.Builder()
            .space(kCONST, CONST_DECLARATION)
            .space(kCONST, pLPAREN)
            .set(pLPAREN, CONST_DECLARATION, Spacings.LINE)
            .set(CONST_DECLARATION, CONST_DECLARATION, Spacings.LINE_HOLD_BREAKS)
            .build();

        static final CustomSpacing CONST_SPEC = CustomSpacing.Builder()
            .none(LITERAL_IDENTIFIER, oCOMMA)
            .spaceWithBreaks(oCOMMA, LITERAL_IDENTIFIER)
            .space(LITERAL_IDENTIFIER, oASSIGN)
            .space(oASSIGN, EXPRESSIONS)
            .none(EXPRESSIONS, oCOMMA)
            .spaceWithBreaks(oCOMMA, EXPRESSIONS)
            .space(EXPRESSIONS, COMMENTS)
            .build();

        static final CustomSpacing VAR_DECL = CustomSpacing.Builder()
            .space(kCONST, VAR_DECLARATION)
            .space(kCONST, pLPAREN)
            .set(pLPAREN, VAR_DECLARATION, Spacings.LINE)
            .set(VAR_DECLARATION, VAR_DECLARATION, Spacings.LINE_HOLD_BREAKS)
            .build();

        static final CustomSpacing VAR_SPEC = CustomSpacing.Builder()
            .none(LITERAL_IDENTIFIER, oCOMMA)
            .spaceWithBreaks(oCOMMA, LITERAL_IDENTIFIER)
            .space(LITERAL_IDENTIFIER, oASSIGN)
            .space(oASSIGN, EXPRESSIONS)
            .none(EXPRESSIONS, oCOMMA)
            .spaceWithBreaks(oCOMMA, EXPRESSIONS)
            .space(EXPRESSIONS, COMMENTS)
            .build();

    }
}
