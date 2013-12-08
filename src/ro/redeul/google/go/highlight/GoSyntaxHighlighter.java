package ro.redeul.google.go.highlight;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.intellij.lang.annotations.JdkConstants;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoLexer;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;

import java.util.HashMap;
import java.util.Map;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;
import static ro.redeul.google.go.lang.lexer.GoTokenTypeSets.*;

public class GoSyntaxHighlighter extends SyntaxHighlighterBase
    implements GoTokenTypes {

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
    public static final String BRACKETS_ID = "go.brackets";

    @NonNls
    public static final String OPERATOR_ID = "go.operators";

    // psi formats
    public static final String TYPE_NAME_ID = "go.type.name";

    public static final String CONST_ID = "go.const";

    public static final String VARIABLE_ID = "go.variable";
    public static final String GLOBAL_VARIABLE_ID = "go.global.variable";
    private static final String METHOD_DECLARATION_ID = "go.method.declaration";

    public static final TextAttributesKey LINE_COMMENT = createKey(LINE_COMMENT_ID, DefaultLanguageHighlighterColors.LINE_COMMENT);

    public static final TextAttributesKey BLOCK_COMMENT = createKey(BLOCK_COMMENT_ID, DefaultLanguageHighlighterColors.BLOCK_COMMENT);

    public static final TextAttributesKey KEYWORD = createKey(KEYWORD_ID,DefaultLanguageHighlighterColors.KEYWORD);

    public static final TextAttributesKey STRING = createKey(STRING_ID, DefaultLanguageHighlighterColors.STRING);

    public static final TextAttributesKey NUMBER = createKey(NUMBER_ID, DefaultLanguageHighlighterColors.NUMBER);

    public static final TextAttributesKey BRACKET = createKey(BRACKETS_ID, DefaultLanguageHighlighterColors.BRACKETS);

    public static final TextAttributesKey OPERATOR = createKey(OPERATOR_ID, DefaultLanguageHighlighterColors.OPERATION_SIGN);

    public static final TextAttributesKey IDENTIFIER = createKey(IDENTIFIER_ID, CodeInsightColors.LOCAL_VARIABLE_ATTRIBUTES);

    public static final TextAttributesKey TYPE_NAME = createKey(TYPE_NAME_ID, CodeInsightColors.ANNOTATION_NAME_ATTRIBUTES);

    public static final TextAttributesKey VARIABLE = createKey(VARIABLE_ID, CodeInsightColors.INSTANCE_FIELD_ATTRIBUTES);

    public static final TextAttributesKey CONST = createKey(CONST_ID, CodeInsightColors.STATIC_FINAL_FIELD_ATTRIBUTES);

    public static final TextAttributesKey GLOBAL_VARIABLE = createKey(GLOBAL_VARIABLE_ID, CodeInsightColors.STATIC_FIELD_ATTRIBUTES);

    public static final TextAttributesKey METHOD_DECLARATION = createKey(METHOD_DECLARATION_ID, CodeInsightColors.METHOD_DECLARATION_ATTRIBUTES);

    static {
        fillMap(ATTRIBUTES, LINE_COMMENTS, LINE_COMMENT);
        fillMap(ATTRIBUTES, BLOCK_COMMENTS, BLOCK_COMMENT);
        fillMap(ATTRIBUTES, KEYWORDS, KEYWORD);
        fillMap(ATTRIBUTES, NUMBERS, NUMBER);
        fillMap(ATTRIBUTES, STRINGS, STRING);
        fillMap(ATTRIBUTES, TokenSet.create(pLPAREN, pRPAREN), BRACKET);
        fillMap(ATTRIBUTES, TokenSet.create(pLBRACK, pRBRACK), BRACKET);
        fillMap(ATTRIBUTES, TokenSet.create(pLCURLY, pRCURLY), BRACKET);
        fillMap(ATTRIBUTES, OPERATORS, OPERATOR);
        fillMap(ATTRIBUTES, IDENTIFIERS, IDENTIFIER);
        fillMap(ATTRIBUTES, BAD_TOKENS, CodeInsightColors.ERRORS_ATTRIBUTES);
    }

    @NotNull
    public Lexer getHighlightingLexer() {
        return new GoLexer();
    }

    @NotNull
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        return pack(ATTRIBUTES.get(tokenType));
    }

    private static TextAttributesKey createKey(String externalName, TextAttributesKey fallbackAttrs) {
        return createTextAttributesKey(externalName, fallbackAttrs);
    }

    private static TextAttributes changeFont(TextAttributes under, @JdkConstants.FontStyle int fontStyle) {
        under.setFontType(fontStyle);
        return under;
    }

    private static TextAttributes changeFont(TextAttributesKey under, @JdkConstants.FontStyle int fontStyle) {
        return changeFont(under.getDefaultAttributes(), fontStyle);
    }
}
