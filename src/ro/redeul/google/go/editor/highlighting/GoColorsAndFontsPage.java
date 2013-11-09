package ro.redeul.google.go.editor.highlighting;

import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.highlight.GoSyntaxHighlighter;
import static ro.redeul.google.go.GoBundle.message;
import static ro.redeul.google.go.highlight.GoSyntaxHighlighter.BLOCK_COMMENT;
import static ro.redeul.google.go.highlight.GoSyntaxHighlighter.BLOCK_COMMENT_ID;
import static ro.redeul.google.go.highlight.GoSyntaxHighlighter.BRACKET;
import static ro.redeul.google.go.highlight.GoSyntaxHighlighter.BRACKETS_ID;
import static ro.redeul.google.go.highlight.GoSyntaxHighlighter.CONST;
import static ro.redeul.google.go.highlight.GoSyntaxHighlighter.CONST_ID;
import static ro.redeul.google.go.highlight.GoSyntaxHighlighter.GLOBAL_VARIABLE;
import static ro.redeul.google.go.highlight.GoSyntaxHighlighter.GLOBAL_VARIABLE_ID;
import static ro.redeul.google.go.highlight.GoSyntaxHighlighter.IDENTIFIER;
import static ro.redeul.google.go.highlight.GoSyntaxHighlighter.IDENTIFIER_ID;
import static ro.redeul.google.go.highlight.GoSyntaxHighlighter.KEYWORD;
import static ro.redeul.google.go.highlight.GoSyntaxHighlighter.KEYWORD_ID;
import static ro.redeul.google.go.highlight.GoSyntaxHighlighter.LINE_COMMENT;
import static ro.redeul.google.go.highlight.GoSyntaxHighlighter.LINE_COMMENT_ID;
import static ro.redeul.google.go.highlight.GoSyntaxHighlighter.NUMBER;
import static ro.redeul.google.go.highlight.GoSyntaxHighlighter.NUMBER_ID;
import static ro.redeul.google.go.highlight.GoSyntaxHighlighter.OPERATOR;
import static ro.redeul.google.go.highlight.GoSyntaxHighlighter.OPERATOR_ID;
import static ro.redeul.google.go.highlight.GoSyntaxHighlighter.STRING;
import static ro.redeul.google.go.highlight.GoSyntaxHighlighter.STRING_ID;
import static ro.redeul.google.go.highlight.GoSyntaxHighlighter.TYPE_NAME;
import static ro.redeul.google.go.highlight.GoSyntaxHighlighter.TYPE_NAME_ID;
import static ro.redeul.google.go.highlight.GoSyntaxHighlighter.VARIABLE;
import static ro.redeul.google.go.highlight.GoSyntaxHighlighter.VARIABLE_ID;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 *         Date: Sep 6, 2010
 */
public class GoColorsAndFontsPage implements ColorSettingsPage {

    private static final AttributesDescriptor[] ATTRIBUTES_DESCRIPTORS =
        new AttributesDescriptor[]{
            new AttributesDescriptor(message("color." + LINE_COMMENT_ID), LINE_COMMENT),
            new AttributesDescriptor(message("color." + BLOCK_COMMENT_ID), BLOCK_COMMENT),
            new AttributesDescriptor(message("color." + KEYWORD_ID), KEYWORD),
            new AttributesDescriptor(message("color." + BRACKETS_ID), BRACKET),
            new AttributesDescriptor(message("color." + OPERATOR_ID), OPERATOR),
            new AttributesDescriptor(message("color." + NUMBER_ID), NUMBER),
            new AttributesDescriptor(message("color." + STRING_ID), STRING),
            new AttributesDescriptor(message("color." + IDENTIFIER_ID), IDENTIFIER),

            // psi
            new AttributesDescriptor(message("color." + TYPE_NAME_ID), TYPE_NAME),
            new AttributesDescriptor(message("color." + CONST_ID), CONST),
            new AttributesDescriptor(message("color." + VARIABLE_ID), VARIABLE),
            new AttributesDescriptor(message("color." + GLOBAL_VARIABLE_ID), GLOBAL_VARIABLE),
        };


    @NotNull
    public String getDisplayName() {
        return "Google Go";
    }

    public Icon getIcon() {
        return GoIcons.GO_ICON_16x16;
    }

    @NotNull
    public AttributesDescriptor[] getAttributeDescriptors() {
        return ATTRIBUTES_DESCRIPTORS;
    }

    @NotNull
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    public SyntaxHighlighter getHighlighter() {
        final SyntaxHighlighter highlighter =
            SyntaxHighlighterFactory.getSyntaxHighlighter(
                GoFileType.INSTANCE, null, null);

        assert highlighter != null;
        return highlighter;
    }

    @NotNull
    public String getDemoText() {
        return
            "/**\n" +
                " * Comment\n" +
                " */\n" +
                "package main\n" +
                "import (\n" +
                "   fmt \"fmt\"\n" +
                "   <unused.import>\"unusedImport\"</unused.import>\n" +
                ")\n" +
                "\n" +
                "type <typeName>T</typeName> <typeName>int</typeName>\n" +
                "type (\n" +
                "   <typeName>T1</typeName> []<typeName>T</typeName>\n" +
                ")\n" +
                "const <const>CONST_VALUE</const> = 10\n\n" +
                "var <globalVariable>globalValue</globalVariable> = 5\n" +
                "\n" +
                "// line comment \n" +
                "func (<variable>t</variable>* <typeName>T1</typeName>) <method.declaration>function1</method.declaration>(<unused.parameter>a</unused.parameter> <typeName>int</typeName>, <variable>c</variable> <typeName>T</typeName>) (<typeName>string</typeName>) {\n" +
                "   x := 'a'\n" +
                "   <unused.variable>y</unused.variable> := 1\n" +
                "   var <variable>x</variable> <typeName>T1</typeName> = 10.10 + <globalVariable>globalValue</globalVariable> + <const>CONST_VALUE</const>\n" +
                "   fmt.Printf(<variable>x</variable>);\n" +
                "   return <variable>x</variable>\n" +
                "}\n" +
                "<error>function</error>\n";
    }

    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        final Map<String, TextAttributesKey> map = new HashMap<>();

        map.put("unused.parameter", CodeInsightColors.NOT_USED_ELEMENT_ATTRIBUTES);
        map.put("unused.import", CodeInsightColors.NOT_USED_ELEMENT_ATTRIBUTES);
        map.put("unused.variable", CodeInsightColors.NOT_USED_ELEMENT_ATTRIBUTES);
        map.put("method.declaration", GoSyntaxHighlighter.METHOD_DECLARATION);
        map.put("variable", VARIABLE);
        map.put("globalVariable", GLOBAL_VARIABLE);
        map.put("typeName", TYPE_NAME);
        map.put("const", CONST);
        map.put("error", CodeInsightColors.ERRORS_ATTRIBUTES);

        return map;

    }
}
