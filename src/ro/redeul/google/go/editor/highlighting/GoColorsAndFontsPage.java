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
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.highlight.GoSyntaxHighlighter;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 *         Date: Sep 6, 2010
 */
public class GoColorsAndFontsPage implements ColorSettingsPage {

    private static final AttributesDescriptor[] ATTRIBUTES_DESCRIPTORS =
        new AttributesDescriptor[]{
            new AttributesDescriptor(GoBundle.message(
                "color." + GoSyntaxHighlighter.DEFAULT_SETTINGS_ID),
                                     GoSyntaxHighlighter.DEFAULT),
            new AttributesDescriptor(GoBundle.message(
                "color." + GoSyntaxHighlighter.LINE_COMMENT_ID),
                                     GoSyntaxHighlighter.LINE_COMMENT),
            new AttributesDescriptor(GoBundle.message(
                "color." + GoSyntaxHighlighter.BLOCK_COMMENT_ID),
                                     GoSyntaxHighlighter.BLOCK_COMMENT),
            new AttributesDescriptor(
                GoBundle.message("color." + GoSyntaxHighlighter.KEYWORD_ID),
                GoSyntaxHighlighter.KEYWORD),
            new AttributesDescriptor(
                GoBundle.message("color." + GoSyntaxHighlighter.BRACKETS_ID),
                GoSyntaxHighlighter.BRACKET),
            new AttributesDescriptor(
                GoBundle.message("color." + GoSyntaxHighlighter.OPERATOR_ID),
                GoSyntaxHighlighter.OPERATOR),
//                    new AttributesDescriptor(GoBundle.message("color." + GoSyntaxHighlighter.BRACKETS_ID), GoSyntaxHighlighter.BRACKET),
            new AttributesDescriptor(
                GoBundle.message("color." + GoSyntaxHighlighter.NUMBER_ID),
                GoSyntaxHighlighter.NUMBER),
            new AttributesDescriptor(
                GoBundle.message("color." + GoSyntaxHighlighter.STRING_ID),
                GoSyntaxHighlighter.STRING),
            new AttributesDescriptor(
                GoBundle.message("color." + GoSyntaxHighlighter.IDENTIFIER_ID),
                GoSyntaxHighlighter.IDENTIFIER),

            // psi
            new AttributesDescriptor(
                GoBundle.message("color." + GoSyntaxHighlighter.TYPE_NAME_ID),
                GoSyntaxHighlighter.TYPE_NAME),
            new AttributesDescriptor(
                GoBundle.message("color." + GoSyntaxHighlighter.CONST_ID),
                GoSyntaxHighlighter.CONST),
            new AttributesDescriptor(
                GoBundle.message("color." + GoSyntaxHighlighter.VARIABLE_ID),
                GoSyntaxHighlighter.VARIABLE),
            new AttributesDescriptor(
                GoBundle.message(
                    "color." + GoSyntaxHighlighter.GLOBAL_VARIABLE_ID),
                GoSyntaxHighlighter.GLOBAL_VARIABLE),
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
                "import fmt \"fmt\"\n" +
                "type <typeName>T</typeName> <typeName>int</typeName>\n" +
                "type (\n" +
                "   <typeName>T1</typeName> []<typeName>T</typeName>\n" +
                ")\n" +
                "const <const>CONST_VALUE</const> = 10\n\n" +
                "var <globalVariable>globalValue</globalVariable> = 5" +
                "// line comment \n" +
                "func(<variable>t</variable>* <typeName>T1</typeName>) function1(<variable>a</variable>, <variable>b</variable> <typeName>int</typeName>, <variable>c</variable> <typeName>T</typeName>) (<typeName>string</typeName>) {\n" +
                "   x := 'a'\n" +
                "   var <variable>x</variable> <typeName>T1</typeName> = 10.10 + <globalVariable>globalValue</globalVariable> + <const>CONST_VALUE</const>\n" +
                "   fmt.Printf(<variable>x</variable>);\n" +
                "   return <variable>x</variable>\n" +
                "}\n" +
                "<error>function</error>\n";
    }

    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        final Map<String, TextAttributesKey> map = new HashMap<String, TextAttributesKey>();

        map.put("variable", GoSyntaxHighlighter.VARIABLE);
        map.put("globalVariable", GoSyntaxHighlighter.GLOBAL_VARIABLE);
        map.put("typeName", GoSyntaxHighlighter.TYPE_NAME);
        map.put("const", GoSyntaxHighlighter.CONST);
        map.put("error", CodeInsightColors.ERRORS_ATTRIBUTES);

        return map;

    }
}
