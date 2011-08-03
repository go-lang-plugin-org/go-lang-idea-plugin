package ro.redeul.google.go.editor.highlighting;

import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.highlight.GoSyntaxHighlighter;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Sep 6, 2010
 * Time: 5:50:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoColorsAndFontsPage implements ColorSettingsPage {

    private static final AttributesDescriptor[] ATTRIBUTES_DESCRIPTORS =
            new AttributesDescriptor[]{
                    new AttributesDescriptor(GoBundle.message("color." + GoSyntaxHighlighter.DEFAULT_SETTINGS_ID), GoSyntaxHighlighter.DEFAULT),
                    new AttributesDescriptor(GoBundle.message("color." + GoSyntaxHighlighter.LINE_COMMENT_ID), GoSyntaxHighlighter.LINE_COMMENT),
                    new AttributesDescriptor(GoBundle.message("color." + GoSyntaxHighlighter.BLOCK_COMMENT_ID), GoSyntaxHighlighter.BLOCK_COMMENT),
                    new AttributesDescriptor(GoBundle.message("color." + GoSyntaxHighlighter.KEYWORD_ID), GoSyntaxHighlighter.KEYWORD),
                    new AttributesDescriptor(GoBundle.message("color." + GoSyntaxHighlighter.BRACKETS_ID), GoSyntaxHighlighter.BRACKET),
//                    new AttributesDescriptor(GoBundle.message("color." + GoSyntaxHighlighter.BRACKETS_ID), GoSyntaxHighlighter.BRACKET),
                    new AttributesDescriptor(GoBundle.message("color." + GoSyntaxHighlighter.NUMBER_ID), GoSyntaxHighlighter.NUMBER),
                    new AttributesDescriptor(GoBundle.message("color." + GoSyntaxHighlighter.STRING_ID), GoSyntaxHighlighter.STRING),
                    new AttributesDescriptor(GoBundle.message("color." + GoSyntaxHighlighter.IDENTIFIER_ID), GoSyntaxHighlighter.IDENTIFIER),

                    // psi
                    new AttributesDescriptor(GoBundle.message("color." + GoSyntaxHighlighter.TYPE_NAME_ID), GoSyntaxHighlighter.TYPE_NAME),
                    new AttributesDescriptor(GoBundle.message("color." + GoSyntaxHighlighter.VARIABLE_ID), GoSyntaxHighlighter.VARIABLE),
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
        return SyntaxHighlighter.PROVIDER.create(GoFileType.GO_FILE_TYPE, null, null);
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
                        "const C iota\n" +
                        "// line comment \n" +
                        "func(<variable>t</variable>* <typeName>T1</typeName>) function1(<variable>a</variable>, <variable>b</variable> <typeName>int</typeName>, <variable>c</variable> <typeName>T</typeName>) (<typeName>string</typeName>) {\n" +
                        "   var <variable>x</variable> <typeName>T1</typeName> = 10.10 + 20\n" +
                        "   return <variable>x</variable>\n" +
                        "}\n" +
                        "<error>function</error>\n";
    }

    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        final Map<String, TextAttributesKey> map = new HashMap<String, TextAttributesKey>();
//             map.put("string", BashSyntaxHighlighter.STRING);
//             map.put("heredoc", BashSyntaxHighlighter.HERE_DOC);
//             map.put("heredocStart", BashSyntaxHighlighter.HERE_DOC_START);
//             map.put("heredocEnd", BashSyntaxHighlighter.HERE_DOC_END);
//             map.put("backquote", BashSyntaxHighlighter.BACKQUOTE);
//             map.put("internalCmd", BashSyntaxHighlighter.INTERNAL_COMMAND);
//             map.put("externalCmd", BashSyntaxHighlighter.EXTERNAL_COMMAND);
//             map.put("subshellCmd", BashSyntaxHighlighter.SUBSHELL_COMMAND);
//             map.put("functionCall", BashSyntaxHighlighter.FUNCTION_CALL);
//             map.put("varDef", BashSyntaxHighlighter.VAR_DEF);
//             map.put("internalVar", BashSyntaxHighlighter.VAR_USE_BUILTIN);
//             map.put("composedVar", BashSyntaxHighlighter.VAR_USE_COMPOSED);
//
//             // we need this to be able to insert << in the text
//             // (relies on the current implementation of JetBrain's HighlightsExtractor class)
//             map.put("dummy", TextAttributesKey.find("dummy"));
//             return map;

        map.put("variable", GoSyntaxHighlighter.TYPE_NAME);
        map.put("typeName", GoSyntaxHighlighter.TYPE_NAME);
        map.put("error", CodeInsightColors.ERRORS_ATTRIBUTES);

        return map;

    }
}
