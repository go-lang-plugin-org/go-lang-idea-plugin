package ro.redeul.google.go.lang.psi.utils;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.lang.parser.GoElementTypes;

import static ro.redeul.google.go.lang.parser.GoElementTypes.*;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 7/16/11
 * Time: 3:12 AM
 */
public class GoTokenSets {

    public static final TokenSet GO_FILE_ENTRY_POINT_TYPES = TokenSet.create(
        IMPORT_DECLARATIONS, TYPE_DECLARATIONS, VAR_DECLARATIONS,
        CONST_DECLARATIONS, FUNCTION_DECLARATION, METHOD_DECLARATION
    );

    public static final TokenSet GO_BLOCK_ENTRY_POINT_TYPES =
        TokenSet.create(VAR_DECLARATIONS, CONST_DECLARATIONS, SHORT_VAR_STATEMENT);

    public static final TokenSet NO_IDENTIFIER_COMPLETION_PARENTS =
        TokenSet.create(VAR_DECLARATION, CONST_DECLARATION, SHORT_VAR_STATEMENT);

    public static final TokenSet UNARY_OPS = TokenSet.create(
        oPLUS, oMINUS, oNOT, oBIT_XOR, oBIT_AND, oMUL, oSEND_CHANNEL
    );
    public static final TokenSet WHITESPACE = TokenSet.create(
        wsNLS, wsWS, TokenType.WHITE_SPACE
    );

    public static final TokenSet WHITESPACE_OR_COMMENTS = TokenSet.create(
        wsNLS, wsWS, TokenType.WHITE_SPACE, GoElementTypes.mML_COMMENT, GoElementTypes.mSL_COMMENT
    );

    public static TokenSet LIKE_oSEMI = TokenSet.create(wsNLS, oSEMI);
}
