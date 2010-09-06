package ro.redeul.google.go.highlight;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoLexer;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.parser.GoElementTypes;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static ro.redeul.google.go.lang.lexer.GoTokenTypeSets.*;

public class GoSyntaxHighlighter extends SyntaxHighlighterBase implements GoTokenTypes {

    private static final Map<IElementType, TextAttributesKey> ATTRIBUTES = new HashMap<IElementType, TextAttributesKey>();

    public static final String DEFAULT_SETTINGS_ID = "go.default";
    public static final String LINE_COMMENT_ID = "go.line.comment";
    public static final String BLOCK_COMMENT_ID = "go.block.comment";
    public static final String STRING_ID = "go.string";
    public static final String NUMBER_ID = "go.number";
    public static final String KEYWORD_ID = "go.keyword";
    public static final String IDENTIFIER_ID = "go.identifier";


    @NonNls
    public static final String CURLY_ID = "Go Curly";
    @NonNls
    public static final String BRACES_ID = "Go Braces";
    @NonNls
    public static final String BRACKETS_ID = "Go Brackets";

    @NonNls
    public static final String OPERATOR_ID = "Go Operator";


    // psi formats
    public static final String TYPE_NAME_ID = "go.type.name";

    public static final String VARIABLE_ID = "go.variable";

//    @NonNls
//    public static final String CONDITIONAL_ID = "Conditional operator";
//    @NonNls
//    public static final String BAD_CHARACTER_ID = "Bad character";
//    @NonNls
//    public static final String HERE_DOC_ID = "Here-document";
//    @NonNls
//    public static final String HERE_DOC_START_ID = "Here-document start marker";
//    @NonNls
//    public static final String HERE_DOC_END_ID = "Here-document end marker";
//    @NonNls
//    public static final String INTERNAL_COMMAND_ID = "Build-in Bash command";
//    @NonNls
//    public static final String EXTERNAL_COMMAND_ID = "External command";
//
//    public static final String SUBSHELL_COMMAND_ID = "Subshell command";
//    @NonNls
//    public static final String BACKQUOTE_COMMAND_ID = "Backquote command `...`";
//    @NonNls
//    public static final String FUNCTION_CALL_ID = "Function call";
//    @NonNls
//    public static final String VAR_DEF_ID = "Variable declaration, e.g. a=1";
// //    @NonNls
//    public static final String VAR_USE_ID = "Variable use $a";
//    @NonNls
//    public static final String VAR_USE_BUILTIN_ID = "Variable use of built-in ($PATH, ...)";
//    @NonNls
//    public static final String VAR_USE_COMPOSED_ID = "Variable use of composed variable like ${A}";
//

    private static final TextAttributes DEFAULT_ATTRIB = new TextAttributes(Color.black, Color.white, null, null, Font.PLAIN);

    static {
//        TextAttributesKey.createTextAttributesKey(LINE_COMMENT_ID, SyntaxHighlighterColors.LINE_COMMENT.getDefaultAttributes());
//        TextAttributesKey.createTextAttributesKey(KEYWORD_ID, SyntaxHighlighterColors.KEYWORD.getDefaultAttributes());
//        TextAttributesKey.createTextAttributesKey(BRACES_ID, SyntaxHighlighterColors.BRACES.getDefaultAttributes());
//        TextAttributesKey.createTextAttributesKey(BRACKETS_ID, SyntaxHighlighterColors.BRACKETS.getDefaultAttributes());
//        TextAttributesKey.createTextAttributesKey(CURLY_ID, SyntaxHighlighterColors.BRACES.getDefaultAttributes());
//        TextAttributesKey.createTextAttributesKey(NUMBER_ID, SyntaxHighlighterColors.NUMBER.getDefaultAttributes());
//        TextAttributesKey.createTextAttributesKey(OPERATOR_ID, SyntaxHighlighterColors.OPERATION_SIGN.getDefaultAttributes());
//        TextAttributesKey.createTextAttributesKey(IDENTIFIER_ID, SyntaxHighlighterColors.OPERATION_SIGN.getDefaultAttributes());
            // TextAttributesKey.createTextAttributesKey(DEFAULT_SETTINGS_ID, DEFAULT_ATTRIB);

//         TextAttributesKey.createTextAttributesKey(STRING2_ID, STRING2_ATTRIB);
//         TextAttributesKey.createTextAttributesKey(REDIRECTION_ID, REDIRECTION_ATTRIB);
//         TextAttributesKey.createTextAttributesKey(CONDITIONAL_ID, CONDITIONAL_ATTRIB);
//         TextAttributesKey.createTextAttributesKey(INTERNAL_COMMAND_ID, INTERNAL_COMMAND_ATTRIB);
//         TextAttributesKey.createTextAttributesKey(VAR_USE_ID, VAR_USE_ATTRIB);
//         TextAttributesKey.createTextAttributesKey(BAD_CHARACTER_ID, HighlighterColors.BAD_CHARACTER.getDefaultAttributes());

        //psi highlighting
//        TextAttributesKey.createTextAttributesKey(STRING_ID, SyntaxHighlighterColors.STRING.getDefaultAttributes());

//         TextAttributesKey.createTextAttributesKey(HERE_DOC_ID, HERE_DOC_ATTRIB);
//         TextAttributesKey.createTextAttributesKey(HERE_DOC_START_ID, HERE_DOC_START_ATTRIB);
//         TextAttributesKey.createTextAttributesKey(HERE_DOC_END_ID, HERE_DOC_END_ATTRIB);
//         TextAttributesKey.createTextAttributesKey(EXTERNAL_COMMAND_ID, EXTERNAL_COMMAND_ATTRIB);
//         TextAttributesKey.createTextAttributesKey(BACKQUOTE_COMMAND_ID, BACKQUOTE_ATTRIB);
//         TextAttributesKey.createTextAttributesKey(SUBSHELL_COMMAND_ID, SUBSHELL_COMMAND_ATTRIB);
//         TextAttributesKey.createTextAttributesKey(FUNCTION_CALL_ID, FUNCTION_CALL_ATTRIB);
//         TextAttributesKey.createTextAttributesKey(VAR_DEF_ID, VAR_DEF_ATTRIB);
//         TextAttributesKey.createTextAttributesKey(VAR_USE_BUILTIN_ID, VAR_USE_INTERNAL_ATTRIB);
//         TextAttributesKey.createTextAttributesKey(VAR_USE_COMPOSED_ID, VAR_USE_COMPOSED_ATTRIB);
    }

    public static final TextAttributesKey LINE_COMMENT = TextAttributesKey.createTextAttributesKey(LINE_COMMENT_ID,
            new TextAttributes(new Color(128, 128, 0), null, null, null, Font.PLAIN));

    public static final TextAttributesKey BLOCK_COMMENT = TextAttributesKey.createTextAttributesKey(BLOCK_COMMENT_ID,
            new TextAttributes(new Color(128, 128, 0), null, null, null, Font.PLAIN));

    public static final TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey(KEYWORD_ID,
            new TextAttributes(new Color(0, 102, 153), null, null, null, Font.BOLD));

    public static final TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey(STRING_ID,
            new TextAttributes(Color.blue, null, null, null, Font.PLAIN));

    public static final TextAttributesKey IDENTIFIER = TextAttributesKey.createTextAttributesKey(IDENTIFIER_ID, DEFAULT_ATTRIB.clone());

    public static final TextAttributesKey NUMBER = TextAttributesKey.createTextAttributesKey(NUMBER_ID,
            TextAttributes.merge(DEFAULT_ATTRIB, new TextAttributes(Color.red, null, null, null, Font.PLAIN)));

    public static final TextAttributesKey TYPE_NAME = TextAttributesKey.createTextAttributesKey(TYPE_NAME_ID,
            new TextAttributes(new Color(0, 128, 0), null, null, null, Font.BOLD));

    public static final TextAttributesKey VARIABLE = TextAttributesKey.createTextAttributesKey(VARIABLE_ID,
            new TextAttributes(Color.gray, null, null, null, Font.BOLD));

    public static final TextAttributesKey BRACKET = TextAttributesKey.createTextAttributesKey(BRACKETS_ID, DEFAULT_ATTRIB.clone());
    public static final TextAttributesKey CURLY = TextAttributesKey.createTextAttributesKey(CURLY_ID, DEFAULT_ATTRIB.clone());
    public static final TextAttributesKey OPERATOR = TextAttributesKey.createTextAttributesKey(OPERATOR_ID, DEFAULT_ATTRIB.clone());

    public static final TextAttributesKey DEFAULT = TextAttributesKey.createTextAttributesKey(DEFAULT_SETTINGS_ID);

    static {
        fillMap(ATTRIBUTES, LINE_COMMENTS, LINE_COMMENT);
        fillMap(ATTRIBUTES, BLOCK_COMMENTS, BLOCK_COMMENT);
        fillMap(ATTRIBUTES, KEYWORDS, KEYWORD);
        fillMap(ATTRIBUTES, NUMBERS, NUMBER);
        fillMap(ATTRIBUTES, STRINGS, STRING);
        fillMap(ATTRIBUTES, TokenSet.create(pLPAREN, pRPAREN), BRACKET);
        fillMap(ATTRIBUTES, TokenSet.create(pLBRACK, pRBRACK), BRACKET);
        fillMap(ATTRIBUTES, TokenSet.create(pLCURCLY, pRCURLY), CURLY);
        fillMap(ATTRIBUTES, OPERATORS, OPERATOR);
        fillMap(ATTRIBUTES, IDENTIFIERS, IDENTIFIER);
        fillMap(ATTRIBUTES, BAD_TOKENS, CodeInsightColors.ERRORS_ATTRIBUTES);
        fillMap(ATTRIBUTES, TokenSet.create(wsNLS, wsWS), DEFAULT);
    }

    @NotNull
    public Lexer getHighlightingLexer() {
        return new GoLexer();
    }

    @NotNull
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        return pack(ATTRIBUTES.get(tokenType));
    }
}
