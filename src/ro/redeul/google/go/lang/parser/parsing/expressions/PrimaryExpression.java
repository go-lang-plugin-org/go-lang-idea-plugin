package ro.redeul.google.go.lang.parser.parsing.expressions;

import java.util.regex.Pattern;

import com.intellij.lang.PsiBuilder;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.declarations.FunctionOrMethodDeclaration;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;
import static ro.redeul.google.go.lang.parser.GoParser.ParsingFlag.AllowCompositeLiteral;
import static ro.redeul.google.go.lang.parser.GoParser.ParsingFlag.ParseIota;
import static ro.redeul.google.go.lang.parser.GoParser.ParsingFlag.WrapCompositeInExpression;

public class PrimaryExpression implements GoElementTypes {

    static Pattern BOOLEAN_LITERAL = Pattern.compile("true|false");
    static Pattern IOTA_LITERAL = Pattern.compile("iota");

    /**
     * PrimaryExpr :=
     * SimplePrimaryExpresion |
     * PrimaryExpr Selector |
     * PrimaryExpr Index |
     * PrimaryExpr Slice |
     * PrimaryExpr TypeAssertion |
     * PrimaryExpr Call .
     */
    public static boolean parse(PsiBuilder builder, GoParser parser) {

        ParserUtils.skipNLS(builder);

        int position = builder.getCurrentOffset();
        PsiBuilder.Marker mark = builder.mark();

        boolean wasParsed = parseSimplePrimaryExpression(builder, parser);

        while (!builder.eof() && wasParsed) {
            wasParsed = false;

            if (ParserUtils.lookAhead(builder, oDOT))
                wasParsed = parseSelectorOrTypeAssertion(builder, parser, mark);

            if (!wasParsed && ParserUtils.lookAhead(builder, pLPAREN))
                wasParsed = parseCallOrConversion(builder, parser, mark);

            if (!wasParsed && ParserUtils.getToken(builder, pLBRACK))
                wasParsed = parseIndexOrSlice(builder, parser, mark);

            if (wasParsed) {
                mark = mark.precede();
            }
        }

        mark.drop();

        return position != builder.getCurrentOffset();
    }

    private static boolean parseSimplePrimaryExpression(PsiBuilder builder,
                                                        GoParser parser) {
        /*
        *       int_lit | float_lit | imaginary_lit | char_lit | string_lit |
        *       "func"                                              -> function literal
        *       "(" Expression ")"                                  -> ExpressionLiteral
        *       "(" Type ") "{"                                     -> CompositeLiteral
        *       "(" "*" [ PackageName "." ] identifier ")" "."      -> MethodExpr
        *       identifier "("                                      -> BuiltinCall
        *
        *       [ PackageName "." ] identifier                      -> QualifiedIdent
        *       [ PackageName "." ] identifier "."                  -> MethodExpr
        *       Type "{"                                            -> CompositeLiteral
        *       Type "(" Expression ")"                             -> ConversionCall
        *
        *
        */
        // Operand      ->
        //      Literal
        //          CompositeLit    -> TypeName '{'
        //                          -> Type '{'
        //          FunctionLit     -> kFUNC ...
        //      QualifiedIdent      -> mIdent<package>
        //      MethodExpr
        //                          -> '(' '*' TypeName ')' '.'
        //                          -> TypeName '.'
        //      ParaExpression
        //                          -> '(' Expr ')'
        // Conversion
        //                          ->  TypeName -> QualifiedIdent
        //                          ->  TypeLit, TypeName  ->
        //                              '(' Type ')'

        if (parseConstantLiteral(builder, parser))
            return true;

        if (ParserUtils.lookAhead(builder, kFUNC, pLPAREN)) {
            PsiBuilder.Marker literal = builder.mark();
            parseLiteralFunction(builder, parser);
            literal.done(LITERAL_EXPRESSION);
            return true;
        }

        PsiBuilder.Marker mark = builder.mark();
        if (builder.getTokenType() == mIDENT) {
            if (BuiltInCallExpression.parse(builder, parser)) {
                mark.drop();
                return true;
            }

            if (parseLiteralIdentifier(builder, parser)) {
                // package? '.' ident '{' -> CompositeLiteral
                if (builder.getTokenType() == pLCURCLY) {
                    if (parseLiteralComposite(builder, parser, mark)) {
                        return true;
                    }
                }

                mark.done(LITERAL_EXPRESSION);
                return true;
            }
        }

        if (parser.parseType(builder)) {
            if (builder.getTokenType() == pLCURCLY) {
                if (parseLiteralComposite(builder, parser, mark)) {
                    return true;
                }
            }

            if (builder.getTokenType() == pLPAREN) {
                return parseCallOrConversion(builder, parser, mark);
            }
        }

        mark.rollbackTo();
        mark = builder.mark();

        if (ParserUtils.getToken(builder, pLPAREN)) {
            if (ParserUtils.getToken(builder, oMUL)) {
                if (parseLiteralIdentifier(builder, parser)) {
                    if (ParserUtils.lookAhead(builder, pRPAREN, oDOT)) {
                        mark.rollbackTo();
                        return parseMethodExpression(builder, parser);
                    }
                }
            }
        }

        mark.rollbackTo();
        mark = builder.mark();
        if (ParserUtils.getToken(builder, pLPAREN)) {
            boolean allowComposite =
                parser.resetFlag(AllowCompositeLiteral, true);
            if (parser.parseExpression(builder)) {
                if (ParserUtils.getToken(builder, pRPAREN)) {
                    parser.resetFlag(AllowCompositeLiteral, allowComposite);
                    mark.done(GoElementTypes.EXPRESSION_PARENTHESIZED);
                    return true;
                }
            }
            parser.resetFlag(AllowCompositeLiteral, allowComposite);
        }

        mark.drop();
        return false;
    }

    private static boolean parseMethodExpression(PsiBuilder builder,
                                                 GoParser parser) {
        PsiBuilder.Marker mark = builder.mark();

        if (ParserUtils.getToken(builder, pLPAREN)) {
            ParserUtils.getToken(builder, oMUL);
            parser.parseTypeName(builder);
            ParserUtils.getToken(builder, pRPAREN);
        }

        ParserUtils.getToken(builder, oDOT);

        ParserUtils.getToken(builder, mIDENT,
                             GoBundle.message("error.method.name.expected"));
        mark.done(GoElementTypes.METHOD_EXPRESSION);

        return true;
    }

    private static boolean parseLiteralFunction(PsiBuilder builder,
                                               GoParser parser) {

        PsiBuilder.Marker mark = builder.mark();

        if (!ParserUtils.getToken(builder, kFUNC)) {
            mark.drop();
            return false;
        }

        FunctionOrMethodDeclaration.parseCompleteMethodSignature(builder, parser);

        ParserUtils.skipNLS(builder);
        parser.parseBody(builder);

        mark.done(LITERAL_FUNCTION);

        return true;
    }


    private static boolean parseIndexOrSlice(PsiBuilder builder,
                                             GoParser parser,
                                             PsiBuilder.Marker mark) {

        ParserUtils.getToken(builder, pLBRACK);
        ParserUtils.skipNLS(builder);

        parser.parseExpression(builder);
        ParserUtils.skipNLS(builder);

        boolean isSlice = false;
        if (builder.getTokenType() == oCOLON) {
            builder.advanceLexer();
            isSlice = true;

            parser.parseExpression(builder);
            ParserUtils.skipNLS(builder);
        }

        ParserUtils.getToken(builder, pRBRACK, "right.bracket.expected");

        mark.done(isSlice ? SLICE_EXPRESSION : INDEX_EXPRESSION);

        return true;
    }


    private static boolean parseCallOrConversion(PsiBuilder builder,
                                                 GoParser parser,
                                                 PsiBuilder.Marker mark) {

        ParserUtils.getToken(builder, pLPAREN);

        if (builder.getTokenType() != pRPAREN) {
            parser.parseExpressionList(builder);
        }

        ParserUtils.getToken(builder, pRPAREN, "closed.parenthesis.expected");

        mark.done(CALL_OR_CONVERSION_EXPRESSION);
        return true;
    }

    private static boolean parseSelectorOrTypeAssertion(PsiBuilder builder,
                                                        GoParser parser,
                                                        PsiBuilder.Marker mark) {
        PsiBuilder.Marker rollBackMarker = builder.mark();

        ParserUtils.getToken(builder, oDOT);
        ParserUtils.skipNLS(builder);

        if (mIDENT == builder.getTokenType()) {
            ParserUtils.getToken(builder, mIDENT);
            rollBackMarker.drop();
            mark.done(SELECTOR_EXPRESSION);
            return true;
        }

        ParserUtils.skipNLS(builder);
        ParserUtils.getToken(builder, pLPAREN, "open.parenthesis.expected");

        ParserUtils.skipNLS(builder);
        if (kTYPE == builder.getTokenType()) {
            rollBackMarker.rollbackTo();
            return false;
        }

        parser.parseType(builder);

        ParserUtils.getToken(builder, pRPAREN, "closed.parenthesis.expected");
        rollBackMarker.drop();
        mark.done(TYPE_ASSERTION_EXPRESSION);
        return true;
    }

    private static boolean parseConstantLiteral(PsiBuilder builder,
                                                GoParser parser) {
        PsiBuilder.Marker expr = builder.mark();

        if (ParserUtils.markTokenIf(builder, LITERAL_STRING, litSTRING)) {
            expr.done(LITERAL_EXPRESSION);
            return true;
        }

        if (ParserUtils.markTokenIf(builder, LITERAL_CHAR, litCHAR)) {
            expr.done(LITERAL_EXPRESSION);
            return true;
        }

        if (ParserUtils.markTokenIf(builder, LITERAL_IMAGINARY,
                                    LITERALS_IMAGINARY)) {
            expr.done(LITERAL_EXPRESSION);
            return true;
        }

        if (ParserUtils.markTokenIf(builder, LITERAL_INTEGER, LITERALS_INT)) {
            expr.done(LITERAL_EXPRESSION);
            return true;
        }

        if (ParserUtils.markTokenIf(builder, LITERAL_FLOAT, LITERALS_FLOAT)) {
            expr.done(LITERAL_EXPRESSION);
            return true;
        }

        expr.drop();
        return false;
    }

    private static boolean parseLiteralComposite(PsiBuilder builder,
                                                 GoParser parser,
                                                 PsiBuilder.Marker mark) {
        if (!parser.isSet(AllowCompositeLiteral))
            return false;

        mark.rollbackTo();
        mark = builder.mark();

        parser.parseType(builder);

        ParserUtils.getToken(builder, pLCURCLY);
        ParserUtils.skipNLS(builder);

        boolean allowComposite =
            parser.resetFlag(AllowCompositeLiteral, true);
        boolean wrapCompositeInExpression =
            parser.resetFlag(WrapCompositeInExpression, false);

        while (!builder.eof() && builder.getTokenType() != pRCURLY) {

            PsiBuilder.Marker elementMarker = builder.mark();

            PsiBuilder.Marker keyOrValueExpression = builder.mark();

            if (!parser.parseExpression(builder)) {
                ParserUtils.wrapError(builder, "expression.expected");
            }

            if (builder.getTokenType() == oCOLON) {
                keyOrValueExpression.done(COMPOSITE_LITERAL_ELEMENT_KEY);
                builder.advanceLexer();
                ParserUtils.skipNLS(builder);

                keyOrValueExpression = builder.mark();
                parser.parseExpression(builder);
            }

            keyOrValueExpression.done(COMPOSITE_LITERAL_ELEMENT_VALUE);

            elementMarker.done(COMPOSITE_LITERAL_ELEMENT);
            if (builder.getTokenType() != pRCURLY) {
                ParserUtils.getToken(builder, oCOMMA, "comma.expected");
            }

            ParserUtils.skipNLS(builder);
        }

        ParserUtils.getToken(builder, pRCURLY,
                             "closed.parenthesis.expected");

        parser.resetFlag(AllowCompositeLiteral, allowComposite);
        parser.resetFlag(WrapCompositeInExpression,
                         wrapCompositeInExpression);
        mark.done(LITERAL_COMPOSITE);

        if (wrapCompositeInExpression) {
            mark.precede().done(LITERAL_EXPRESSION);
        }

        return true;
    }

    private static boolean parseLiteralIdentifier(PsiBuilder builder,
                                                  GoParser parser) {
        String identifier = builder.getTokenText();

        if (BOOLEAN_LITERAL.matcher(identifier).matches()) {
            ParserUtils.eatElement(builder, LITERAL_BOOL);
            return true;
        }

        if (IOTA_LITERAL.matcher(identifier).matches() && parser.isSet(ParseIota))
        {
            ParserUtils.eatElement(builder, LITERAL_IOTA);
            return true;
        }

        PsiBuilder.Marker mark = builder.mark();

        if (!ParserUtils.getToken(builder, mIDENT))
            return false;

        if (parser.isPackageName(identifier) &&
            ParserUtils.lookAhead(builder, oDOT)) {
            ParserUtils.getToken(builder, oDOT);
            ParserUtils.getToken(builder, mIDENT,
                                 GoBundle.message("identifier.expected"));
        }

        mark.done(GoElementTypes.LITERAL_IDENTIFIER);
        return true;
    }

    private static boolean parseFunctionTypeOrLiteral(PsiBuilder builder,
                                                      GoParser parser) {

        PsiBuilder.Marker mark = builder.mark();

        parser.parseType(builder);

        PsiBuilder.Marker mark2 = builder.mark();

        ParserUtils.skipNLS(builder);

        if (pLCURCLY == builder.getTokenType()) {
            parser.parseBody(builder);

            mark2.drop();
            mark.done(LITERAL_FUNCTION);
        } else {
            mark2.rollbackTo();
            mark.drop();
        }

        return true;
    }

}
