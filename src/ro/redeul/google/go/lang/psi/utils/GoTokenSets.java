package ro.redeul.google.go.lang.psi.utils;

import com.intellij.psi.tree.TokenSet;

import static ro.redeul.google.go.lang.parser.GoElementTypes.*;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 7/16/11
 * Time: 3:12 AM
 */
public class GoTokenSets {

    public static final TokenSet GO_FILE_ENTRY_POINT_TYPES = TokenSet.create(
        IMPORT_DECLARATIONS, TYPE_DECLARATIONS, VAR_DECLARATIONS, CONST_DECLARATIONS, FUNCTION_DECLARATION, METHOD_DECLARATION
    );

    public static final TokenSet GO_BLOCK_ENTRY_POINT_TYPES = TokenSet.create(VAR_DECLARATIONS, SHORT_VAR_STATEMENT, FOR_WITH_RANGE_STATEMENT, FOR_WITH_CLAUSES_STATEMENT);

    public static final TokenSet NO_IDENTIFIER_COMPLETION_PARENTS = TokenSet.create(VAR_DECLARATION, SHORT_VAR_STATEMENT);
}
