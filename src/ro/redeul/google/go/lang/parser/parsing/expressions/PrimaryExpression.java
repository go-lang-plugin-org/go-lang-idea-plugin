package ro.redeul.google.go.lang.parser.parsing.expressions;

import com.intellij.lang.PsiBuilder;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.completion.GoCompletionContributor;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;

import java.util.regex.Pattern;

import static ro.redeul.google.go.lang.parser.GoParser.ParsingFlag.AllowCompositeLiteral;
import static ro.redeul.google.go.lang.parser.GoParser.ParsingFlag.ParseIota;
import static ro.redeul.google.go.lang.parser.parsing.declarations.FunctionOrMethodDeclaration.parseCompleteMethodSignature;

class PrimaryExpression implements GoElementTypes {

    private static final Pattern BOOLEAN_LITERAL = Pattern.compile("true|false");
    private static final Pattern IOTA_LITERAL = Pattern.compile("iota");

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

        if (parseConstantLiteral(builder))
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
                if (builder.getTokenType() == pLCURLY) {
                    if ( parser.isSet(GoParser.ParsingFlag.AllowCompositeLiteral)) {
                        if (parseLiteralComposite(builder, parser, mark)) {
                            return true;
                        }
                    }
                }

                mark.done(LITERAL_EXPRESSION);
                return true;
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
                    if (!ParserUtils.lookAhead(builder, pLPAREN)) {
                        parser.resetFlag(AllowCompositeLiteral, allowComposite);
                        mark.done(GoElementTypes.PARENTHESISED_EXPRESSION);
                        return true;
                    }
                }
            }
            parser.resetFlag(AllowCompositeLiteral, allowComposite);
        }

        mark.rollbackTo();
        mark = builder.mark();

        if (parser.parseType(builder) != null) {
            if (builder.getTokenType() == pLCURLY) {
                if (parseLiteralComposite(builder, parser, mark)) {
                    return true;
                }
            }

            if (builder.getTokenType() == pLPAREN) {
                return parseCallOrConversion(builder, parser, mark);
            }
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

    private static void parseLiteralFunction(PsiBuilder builder,
                                                GoParser parser) {

        PsiBuilder.Marker mark = builder.mark();

        if (!ParserUtils.getToken(builder, kFUNC)) {
            mark.drop();
            return;
        }

        parseCompleteMethodSignature(builder, parser);

        parser.parseBody(builder);

        mark.done(LITERAL_FUNCTION);
    }


    private static boolean parseIndexOrSlice(PsiBuilder builder,
                                             GoParser parser,
                                             PsiBuilder.Marker mark) {

        ParserUtils.getToken(builder, pLBRACK);
        boolean allowComposite = parser.resetFlag(AllowCompositeLiteral, true);
        parser.parseExpression(builder);
        parser.resetFlag(AllowCompositeLiteral, allowComposite);
        boolean isSlice = false;
        if (builder.getTokenType() == oCOLON) {
            builder.advanceLexer();
            isSlice = true;

            parser.parseExpression(builder);

            if (builder.getTokenType() == oCOLON) {
                builder.advanceLexer();
                parser.parseExpression(builder);
            }
        }

        ParserUtils.getToken(builder, pRBRACK, "right.bracket.expected");

        mark.done(isSlice ? SLICE_EXPRESSION : INDEX_EXPRESSION);

        return true;
    }


    private static boolean parseCallOrConversion(PsiBuilder builder,
                                                 GoParser parser,
                                                 PsiBuilder.Marker mark) {
        boolean allowComposite = parser.resetFlag(AllowCompositeLiteral, true);
        ParserUtils.getToken(builder, pLPAREN);

        if (builder.getTokenType() != pRPAREN) {
            PsiBuilder.Marker expressionList = builder.mark();
            if (parser.parseExpressionList(builder) > 1) {
                expressionList.done(GoElementTypes.EXPRESSION_LIST);
            } else {
                expressionList.drop();
            }
        }

        ParserUtils.getToken(builder, pRPAREN, "closed.parenthesis.expected");

        mark.done(CALL_OR_CONVERSION_EXPRESSION);
        parser.resetFlag(AllowCompositeLiteral, allowComposite);
        return true;
    }

    private static boolean parseSelectorOrTypeAssertion(PsiBuilder builder,
                                                        GoParser parser,
                                                        PsiBuilder.Marker mark) {
        if (ParserUtils.lookAhead(builder, oDOT, pLPAREN, kTYPE, pRPAREN)) {
            return false;
        }

        ParserUtils.getToken(builder, oDOT);

        if (ParserUtils.lookAhead(builder, pLPAREN))  {
            ParserUtils.getToken(builder, pLPAREN, "open.parenthesis.expected");

            parser.parseType(builder);

            ParserUtils.getToken(builder, pRPAREN, "closed.parenthesis.expected");
            mark.done(TYPE_ASSERTION_EXPRESSION);
            return true;
        }

        if (ParserUtils.lookAhead(builder, mIDENT)) {
            ParserUtils.eatElement(builder, LITERAL_IDENTIFIER);
        } else {
            builder.error(GoBundle.message("error.identifier.expected"));
        }

        mark.done(SELECTOR_EXPRESSION);
        return true;
    }

    private static boolean parseConstantLiteral(PsiBuilder builder) {
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
//        if (!parser.isSet(AllowCompositeLiteral))
//            return false;

//        boolean wrapCompositeInExpression =
//            parser.resetFlag(WrapCompositeInExpression, false);

        boolean allowComposite =
            parser.resetFlag(AllowCompositeLiteral, true);

        mark.rollbackTo();
        mark = builder.mark();

        parser.parseType(builder);

        parseCompositeLiteralValue(builder, parser);

        mark.done(LITERAL_COMPOSITE);

//        if (wrapCompositeInExpression) {
            mark.precede().done(LITERAL_EXPRESSION);
//        }

        parser.resetFlag(AllowCompositeLiteral, allowComposite);
//        parser.resetFlag(WrapCompositeInExpression,
//                         wrapCompositeInExpression);

        return true;
    }

    private static void parseCompositeLiteralValue(PsiBuilder builder,
                                                   GoParser parser) {

        PsiBuilder.Marker literalValue = builder.mark();

        ParserUtils.getToken(builder, pLCURLY);

        while (!builder.eof() && builder.getTokenType() != pRCURLY) {

            parseCompositeLiteralValueElement(builder, parser);

            if ( ParserUtils.lookAhead(builder, GoTokenTypes.oSEMI_SYNTHETIC) ) {
                builder.error(GoBundle.message("error.comma.expected.before.newline"));
                ParserUtils.getToken(builder, GoTokenTypes.oSEMI_SYNTHETIC);
            }

            if ( ! ParserUtils.getToken(builder, oCOMMA) )
                break;
        }

        ParserUtils.getToken(builder, pRCURLY, GoBundle.message("error.closing.curly.expected"));

        literalValue.done(LITERAL_COMPOSITE_VALUE);
    }

    private static void parseCompositeLiteralValueElement(PsiBuilder builder,
                                                          GoParser parser) {

        PsiBuilder.Marker valueElement = builder.mark();

        if ( ParserUtils.lookAhead(builder, pLCURLY) ) {
            parseCompositeLiteralValue(builder, parser);
        } else {
            if (!parser.parseExpression(builder)) {
                ParserUtils.wrapError(builder, "expression.expected");
            }

            if (ParserUtils.lookAhead(builder, oCOLON)) {
                valueElement.done(LITERAL_COMPOSITE_ELEMENT_KEY);
                valueElement = valueElement.precede();

                ParserUtils.getToken(builder, oCOLON);
            }

            if (ParserUtils.lookAhead(builder, pLCURLY)) {
                parseCompositeLiteralValue(builder, parser);
            } else {
                parser.parseExpression(builder);
            }
        }

        valueElement.done(LITERAL_COMPOSITE_ELEMENT);
    }

    private static boolean parseLiteralIdentifier(PsiBuilder builder,
                                                  GoParser parser) {
        String identifier = builder.getTokenText();

        if (BOOLEAN_LITERAL.matcher(identifier).matches()) {
            ParserUtils.eatElement(builder, LITERAL_BOOL);
            return true;
        }

        if (IOTA_LITERAL.matcher(identifier).matches() && parser.isSet(
            ParseIota)) {
            ParserUtils.eatElement(builder, LITERAL_IOTA);
            return true;
        }

        PsiBuilder.Marker mark = builder.mark();

        if (!ParserUtils.getToken(builder, mIDENT))
            return false;

        if ( identifier != null) {
            identifier = identifier.replaceAll(GoCompletionContributor.DUMMY_IDENTIFIER, "");
        }

        if (parser.isPackageName(identifier) &&
            ParserUtils.lookAhead(builder, oDOT)) {
            ParserUtils.getToken(builder, oDOT);
            if (ParserUtils.lookAhead(builder, mIDENT)) {
                ParserUtils.getToken(builder, mIDENT);
            } else {
                builder.error(GoBundle.message("identifier.expected"));
            }

        }

        mark.done(GoElementTypes.LITERAL_IDENTIFIER);
        return true;
    }

    private static boolean parseFunctionTypeOrLiteral(PsiBuilder builder,
                                                      GoParser parser) {

        PsiBuilder.Marker mark = builder.mark();

        parser.parseType(builder);

        PsiBuilder.Marker mark2 = builder.mark();

        if (pLCURLY == builder.getTokenType()) {
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
