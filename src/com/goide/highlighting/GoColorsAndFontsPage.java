package com.goide.highlighting;

import com.goide.GoFileType;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
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
  };

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
    return "∫∫∫\n\n" +
           "package main\n" +
           "\n" +
           "import (\n" +
           "    \"fmt\"\n" +
           "    \"math\"\n" +
           ")\n" +
           "\n" +
           "type Abser interface {\n" +
           "    Abs() float64\n" +
           "}\n" +
           "\n" +
           "func main() {\n" +
           "    var a Abser;\n" +
           "    f := MyFloat(-math.Sqrt2);\n" +
           "    v := Vertex{3, 4};\n" +
           "\n" +
           "    a = f  // a MyFloat implements Abser\n" +
           "    a = &v // a *Vertex implements Abser\n" +
           "    a = v  // a Vertex, does NOT\n" +
           "    // implement Abser\n" +
           "\n" +
           "    fmt.Println(a.Abs())\n" +
           "}\n" +
           "\n" +
           "type MyFloat float64\n" +
           "\n" +
           "func (f MyFloat) Abs() float64 {\n" +
           "    if f < 0 {\n" +
           "        return float64(-f)\n" +
           "    }\n" +
           "    return float64(f)\n" +
           "}\n" +
           "\n" +
           "type Vertex struct {\n" +
           "    X, Y float64\n" +
           "}\n" +
           "\n" +
           "func (v *Vertex) Abs() float64 {\n" +
           "    return math.Sqrt(v.X*v.X + v.Y*v.Y)\n" +
           "}";
  }

  public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
    return null;
  }
}
