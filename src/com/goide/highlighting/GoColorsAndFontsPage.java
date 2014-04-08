package com.goide.highlighting;

import com.goide.GoFileType;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.util.containers.ContainerUtil;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Map;

import static com.goide.highlighting.GoSyntaxHighlightingColors.*;

public class GoColorsAndFontsPage implements ColorSettingsPage {
  private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
    new AttributesDescriptor("Line comment", LINE_COMMENT),
    new AttributesDescriptor("Block comment", BLOCK_COMMENT),
    new AttributesDescriptor("Keyword", KEYWORD),
    new AttributesDescriptor("Identifier", IDENTIFIER),
    new AttributesDescriptor("String", STRING),
    new AttributesDescriptor("Number", NUMBER),
    new AttributesDescriptor("Semicolon", SEMICOLON),
    new AttributesDescriptor("Colon", COLON),
    new AttributesDescriptor("Comma", COMMA),
    new AttributesDescriptor("Dot", DOT),
    new AttributesDescriptor("Operator", OPERATOR),
    new AttributesDescriptor("Brackets", BRACKETS),
    new AttributesDescriptor("Braces", BRACES),
    new AttributesDescriptor("Parentheses", PARENTHESES),
    new AttributesDescriptor("Bad character", BAD_CHARACTER),
    new AttributesDescriptor("Type specification", TYPE_SPECIFICATION),
    new AttributesDescriptor("Type reference", TYPE_REFERENCE),
  };
  private static final THashMap<String, TextAttributesKey> ATTRIBUTES_KEY_MAP = ContainerUtil.newTroveMap();
  static {
    ATTRIBUTES_KEY_MAP.put("tr", TYPE_REFERENCE);
    ATTRIBUTES_KEY_MAP.put("ts", TYPE_SPECIFICATION);
  }

  @NotNull
  public String getDisplayName() {
    return GoFileType.INSTANCE.getName();
  }

  public Icon getIcon() {
    return GoFileType.INSTANCE.getIcon();
  }

  @NotNull
  public AttributesDescriptor[] getAttributeDescriptors() {
    return DESCRIPTORS;
  }

  @NotNull
  public ColorDescriptor[] getColorDescriptors() {
    return ColorDescriptor.EMPTY_ARRAY;
  }

  @NotNull
  public SyntaxHighlighter getHighlighter() {
    return new GoSyntaxHighlighter();
  }

  @NotNull
  public String getDemoText() {
    return "package main\n" +
           "\n" +
           "import (\n" +
           "    \"fmt\"\n" +
           "    \"math\"\n" +
           ")\n" +
           "\n" +
           "type <ts>Abser</ts> interface {\n" +
           "    Abs() float64\n" +
           "}\n" +
           "\n" +
           "func main() {\n" +
           "    var a <tr>Abser</tr>;\n" +
           "    f := <tr>MyFloat</tr>(-math.Sqrt2);\n" +
           "    v := <tr>Vertex</tr>{3, 4};\n" +
           "\n" +
           "    a = f  // a MyFloat implements Abser\n" +
           "    a = &v // a *Vertex implements Abser\n" +
           "    a = v  // a Vertex, does NOT\n" +
           "    // implement Abser\n" +
           "\n" +
           "    fmt.Println(a.Abs())\n" +
           "}\n" +
           "\n" +
           "type <ts>MyFloat</ts> float64\n" +
           "\n" +
           "func (f <tr>MyFloat</tr>) Abs() float64 {\n" +
           "    if f < 0 {\n" +
           "        return float64(-f)\n" +
           "    }\n" +
           "    return float64(f)\n" +
           "}\n" +
           "\n" +
           "type <ts>Vertex</ts> struct {\n" +
           "    X, Y float64\n" +
           "}\n" +
           "\n" +
           "func (v *<tr>Vertex</tr>) Abs() float64 {\n" +
           "    return math.Sqrt(v.X*v.X + v.Y*v.Y)\n" +
           "}";
  }

  @NotNull
  public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
    return ATTRIBUTES_KEY_MAP;
  }
}
