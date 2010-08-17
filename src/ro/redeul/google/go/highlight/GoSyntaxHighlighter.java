package ro.redeul.google.go.highlight;

import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.SyntaxHighlighterColors;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoLexer;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;

import java.util.HashMap;
import java.util.Map;

import static ro.redeul.google.go.lang.lexer.GoTokenTypeSets.*;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 1:32:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class GoSyntaxHighlighter extends SyntaxHighlighterBase implements GoTokenTypes {

    private static final Map<IElementType, TextAttributesKey> ATTRIBUTES = new HashMap<IElementType, TextAttributesKey>();

    static {
        fillMap(ATTRIBUTES, LINE_COMMENTS, SyntaxHighlighterColors.LINE_COMMENT);
        fillMap(ATTRIBUTES, BLOCK_COMMENTS, SyntaxHighlighterColors.LINE_COMMENT);
        fillMap(ATTRIBUTES, BAD_TOKENS, CodeInsightColors.ERRORS_ATTRIBUTES);
        fillMap(ATTRIBUTES, KEYWORDS, SyntaxHighlighterColors.KEYWORD);
        fillMap(ATTRIBUTES, NUMBERS, SyntaxHighlighterColors.NUMBER);
        fillMap(ATTRIBUTES, STRINGS, SyntaxHighlighterColors.STRING);
        fillMap(ATTRIBUTES, BRACES, SyntaxHighlighterColors.BRACES);
        fillMap(ATTRIBUTES, OPERATORS, SyntaxHighlighterColors.OPERATION_SIGN);
        fillMap(ATTRIBUTES, IDENTIFIERS, HighlightInfoType.UNUSED_SYMBOL.getAttributesKey());
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
