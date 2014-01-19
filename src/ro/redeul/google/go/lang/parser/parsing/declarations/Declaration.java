package ro.redeul.google.go.lang.parser.parsing.declarations;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.types.Types;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

import static ro.redeul.google.go.lang.parser.GoParser.ParsingFlag.ParseIota;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 25, 2010
 * Time: 12:03:28 AM
 */
public class Declaration extends ParserUtils implements GoElementTypes {

    public static IElementType parse(PsiBuilder builder, GoParser parser) {

        if (lookAhead(builder, kCONST))
            return parseDeclaration(builder, parser, kCONST, CONST_DECLARATIONS, ConstSpecParser);

        if (lookAhead(builder, kVAR))
            return parseDeclaration(builder, parser, kVAR, VAR_DECLARATIONS, VarSpecParser);

        if (lookAhead(builder, kTYPE))
            return parseDeclaration(builder, parser, kTYPE, TYPE_DECLARATIONS, TypeSpecParser);

        return null;
    }

    private static final SpecParser ConstSpecParser = new SpecParser() {
        @Override
        public boolean parse(PsiBuilder builder, GoParser parser) {
            PsiBuilder.Marker spec = builder.mark();

            if (parser.parseIdentifierList(builder, false) == 0) {
                spec.drop();
                builder.error("identifier.list.expected");
                return false;
            }

            boolean closeStatement = true;

            if (!ParserUtils.lookAhead(builder, oASSIGN)) {
                parser.parseType(builder);
            }

            if (ParserUtils.getToken(builder, oASSIGN)) {
                boolean parseIota = parser.resetFlag(ParseIota, true);
                if (parser.parseExpressionList(builder) == 0) {
                    closeStatement = false;
                    builder.error(GoBundle.message("error.missing.expr.for.const.declaration"));
                }
                parser.resetFlag(ParseIota, parseIota);
            }

            completeNodeWithDocs(builder, spec, CONST_DECLARATION, closeStatement, true, true);
            return true;
        }
    };

    private static final SpecParser VarSpecParser = new SpecParser() {
        @Override
        public boolean parse(PsiBuilder builder, GoParser parser) {
            PsiBuilder.Marker spec = builder.mark();
            if (parser.parseIdentifierList(builder, false) == 0) {
                spec.drop();
                builder.error("identifier.list.expected");
                return false;
            }

            boolean closeStatement = true;
            if (!ParserUtils.lookAhead(builder, oASSIGN)) {
                parser.parseType(builder);
            }

            if (ParserUtils.getToken(builder, oASSIGN)) {
                if (parser.parseExpressionList(builder) == 0) {
                    closeStatement = false;
                    builder.error(GoBundle.message("error.missing.expr.for.var.declaration"));
                }
            }

            completeNodeWithDocs(builder, spec, VAR_DECLARATION, closeStatement, true, true);
            return true;
        }
    };

    private static final SpecParser TypeSpecParser = new SpecParser() {
        @Override
        public boolean parse(PsiBuilder builder, GoParser parser) {
            if (!ParserUtils.lookAhead(builder, mIDENT)) {
                builder.error(GoBundle.message("error.identifier.expected"));
                return false;
            }

            PsiBuilder.Marker spec = builder.mark();
            ParserUtils.eatElement(builder, TYPE_NAME_DECLARATION);

            if (Types.parseTypeDeclaration(builder, parser) == null) {
                builder.error(GoBundle.message("error.type.expected"));
            }

            completeNodeWithDocs(builder, spec, TYPE_DECLARATION, true, true, true);
            return true;
        }
    };

    public static IElementType parseDeclaration(PsiBuilder builder, GoParser parser,
                                                IElementType tokenType, IElementType elementType,
                                                SpecParser specParser) {

        PsiBuilder.Marker declaration = builder.mark();

        if (!getToken(builder, tokenType)) {
            declaration.rollbackTo();
            return null;
        }

        boolean hasNested = Declaration.parseNestedOrBasic(builder, parser, specParser);

        return completeNodeWithDocs(builder, declaration, elementType, hasNested, true, false);
    }

    public interface SpecParser {
        boolean parse(PsiBuilder builder, GoParser parser);
    }

    public static boolean parseNestedOrBasic(PsiBuilder builder, GoParser parser,
                                             SpecParser declarationParser) {

        if (!ParserUtils.getToken(builder, pLPAREN)) {
            declarationParser.parse(builder, parser);
            return false;
        }

        while (!builder.eof() && !lookAhead(builder, pRPAREN)) {
            if (!declarationParser.parse(builder, parser)) {
                break;
            }
        }

        getToken(builder, pRPAREN, GoBundle.message("error.closing.para.expected"));
        return true;
    }
}
