package ro.redeul.google.go.lang.parser.parsing.statements;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;
import static ro.redeul.google.go.lang.parser.GoParser.ParsingFlag.AllowCompositeLiteral;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 25, 2010
 * Time: 8:01:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class IfStatement implements GoElementTypes {

    public static IElementType parse(PsiBuilder builder, GoParser parser) {

        if ( ! ParserUtils.lookAhead(builder, kIF) )
            return null;

        PsiBuilder.Marker marker = builder.mark();

        ParserUtils.getToken(builder, kIF);

        boolean allowComposite = parser.resetFlag(AllowCompositeLiteral, false);

        if (ParserUtils.lookAhead(builder, oSEMI)) {
            builder.mark().done(GoElementTypes.EMPTY_STATEMENT);
            ParserUtils.getToken(builder, oSEMI);
            parser.parseExpression(builder);
        } else {
            PsiBuilder.Marker mark = builder.mark();

            parser.parseExpression(builder);

            if ( !ParserUtils.lookAhead(builder, pLCURCLY)) {
                mark.rollbackTo();
                parser.tryParseSimpleStmt(builder);
                ParserUtils.getToken(builder, oSEMI, GoBundle.message("error.semicolon.expected"));
                parser.parseExpression(builder);
            } else {
                mark.drop();
            }
        }
        parser.resetFlag(AllowCompositeLiteral, allowComposite);
        parser.parseBody(builder);

        ParserUtils.skipNLS(builder);
        if (builder.getTokenType() == kELSE) {
            ParserUtils.getToken(builder, kELSE);
            ParserUtils.skipNLS(builder);
            Statements.parse(builder, parser);
        }

        marker.done(IF_STATEMENT);
        return IF_STATEMENT;
    }
}
