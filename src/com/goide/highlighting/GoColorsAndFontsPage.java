/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.goide.highlighting;

import com.goide.GoFileType;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.util.containers.ContainerUtil;
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
    new AttributesDescriptor("Builtin type", BUILTIN_TYPE_REFERENCE),
    new AttributesDescriptor("Package exported interface", PACKAGE_EXPORTED_INTERFACE),
    new AttributesDescriptor("Package exported struct", PACKAGE_EXPORTED_STRUCT),
    new AttributesDescriptor("Package exported constant", PACKAGE_EXPORTED_CONSTANT),
    new AttributesDescriptor("Package exported variable", PACKAGE_EXPORTED_VARIABLE),
    new AttributesDescriptor("Package exported function", PACKAGE_EXPORTED_FUNCTION),
    new AttributesDescriptor("Package local interface", PACKAGE_LOCAL_INTERFACE),
    new AttributesDescriptor("Package local struct", PACKAGE_LOCAL_STRUCT),
    new AttributesDescriptor("Package local constant", PACKAGE_LOCAL_CONSTANT),
    new AttributesDescriptor("Package local variable", PACKAGE_LOCAL_VARIABLE),
    new AttributesDescriptor("Package local function", PACKAGE_LOCAL_FUNCTION),
    new AttributesDescriptor("Struct exported member", STRUCT_EXPORTED_MEMBER),
    new AttributesDescriptor("Struct exported method", STRUCT_EXPORTED_METHOD),
    new AttributesDescriptor("Struct local member", STRUCT_LOCAL_MEMBER),
    new AttributesDescriptor("Struct local method", STRUCT_LOCAL_METHOD),
    new AttributesDescriptor("Method receiver", METHOD_RECEIVER),
    new AttributesDescriptor("Function parameter", FUNCTION_PARAMETER),
    new AttributesDescriptor("Local constant", LOCAL_CONSTANT),
    new AttributesDescriptor("Local variable", LOCAL_VARIABLE),
    new AttributesDescriptor("Scope declared variable", SCOPE_VARIABLE),
    new AttributesDescriptor("Label", LABEL)
  };
  private static final Map<String, TextAttributesKey> ATTRIBUTES_KEY_MAP = ContainerUtil.newTroveMap();
  static {
    ATTRIBUTES_KEY_MAP.put("tr", TYPE_REFERENCE);
    ATTRIBUTES_KEY_MAP.put("ts", TYPE_SPECIFICATION);
    ATTRIBUTES_KEY_MAP.put("bt", BUILTIN_TYPE_REFERENCE);
    ATTRIBUTES_KEY_MAP.put("kw", KEYWORD);
    ATTRIBUTES_KEY_MAP.put("pei", PACKAGE_EXPORTED_INTERFACE);
    ATTRIBUTES_KEY_MAP.put("pes", PACKAGE_EXPORTED_STRUCT);
    ATTRIBUTES_KEY_MAP.put("pec", PACKAGE_EXPORTED_CONSTANT);
    ATTRIBUTES_KEY_MAP.put("pev", PACKAGE_EXPORTED_VARIABLE);
    ATTRIBUTES_KEY_MAP.put("pef", PACKAGE_EXPORTED_FUNCTION);
    ATTRIBUTES_KEY_MAP.put("pli", PACKAGE_LOCAL_INTERFACE);
    ATTRIBUTES_KEY_MAP.put("pls", PACKAGE_LOCAL_STRUCT);
    ATTRIBUTES_KEY_MAP.put("plc", PACKAGE_LOCAL_CONSTANT);
    ATTRIBUTES_KEY_MAP.put("plv", PACKAGE_LOCAL_VARIABLE);
    ATTRIBUTES_KEY_MAP.put("plf", PACKAGE_LOCAL_FUNCTION);
    ATTRIBUTES_KEY_MAP.put("sem", STRUCT_EXPORTED_MEMBER);
    ATTRIBUTES_KEY_MAP.put("sef", STRUCT_EXPORTED_METHOD);
    ATTRIBUTES_KEY_MAP.put("slm", STRUCT_LOCAL_MEMBER);
    ATTRIBUTES_KEY_MAP.put("slf", STRUCT_LOCAL_METHOD);
    ATTRIBUTES_KEY_MAP.put("mr", METHOD_RECEIVER);
    ATTRIBUTES_KEY_MAP.put("fp", FUNCTION_PARAMETER);
    ATTRIBUTES_KEY_MAP.put("lc", LOCAL_CONSTANT);
    ATTRIBUTES_KEY_MAP.put("lv", LOCAL_VARIABLE);
    ATTRIBUTES_KEY_MAP.put("sv", SCOPE_VARIABLE);
    ATTRIBUTES_KEY_MAP.put("ll", LABEL);
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
    return "/*\n" +
           " * Go highlight sample \n" +
           " */\n" +
           "\n" +
           "// Package main\n"+
           "package main\n" +
           "\n" +
           "import \"fmt\"\n" +
           "\n" +
           "type (\n" +
           "    <pei>PublicInterface</pei> interface {\n" +
           "        <pef>PublicFunc</pef>() <bt>int</bt>\n" +
           "        <plf>privateFunc</plf>() <bt>int</bt>\n" +
           "    }\n" +
           "\n" +
           "    <pli>private</pli> interface {\n" +
           "        <pef>PublicFunc</pef>() <bt>int</bt>\n" +
           "        <plf>privateFunc</plf>() <bt>int</bt>\n" +
           "    }\n" +
           "\n" +
           "    <pes>PublicStruct</pes> struct {\n" +
           "        <sem>PublicField</sem>  <bt>int</bt>\n" +
           "        <slm>privateField</slm> <bt>int</bt>\n" +
           "    }\n" +
           "\n" +
           "    <pls>privateStruct</pls> struct {\n" +
           "        <sem>PublicField</sem>  <bt>int</bt>\n" +
           "        <slm>privateField</slm> <bt>int</bt>\n" +
           "    }\n" +
           "\n" +
           "    <ts>demoInt</ts> <bt>int</bt>\n" +
           ")\n" +
           "\n" +
           "const (\n" +
           "    <pec>PublicConst</pec>  = 1\n" +
           "    <plc>privateConst</plc> = 2\n" +
           ")\n" +
           "\n" +
           "var (\n" +
           "    <pev>PublicVar</pev>  = 1\n" +
           "    <plv>privateVar</plv> = 2\n" +
           ")\n" +
           "\n" +
           "func PublicFunc() <bt>int</bt> {\n" +
           "    const <lc>LocalConst</lc> = 1\n" +
           "    <lv>localVar</lv> := <pev>PublicVar</pev>\n" +
           "    return <lv>localVar</lv>\n" +
           "}\n" +
           "\n" +
           "func privateFunc() (<bt>int</bt>, <bt>int</bt>) {\n" +
           "    const <lc>localConst</lc> = 2\n" +
           "    <lv>LocalVar</lv> := <plv>privateVar</plv>\n" +
           "    return <lv>LocalVar</lv>, <pev>PublicVar</pev>\n" +
           "}\n" +
           "\n" +
           "func (<mr>ps</mr> <tr>PublicStruct</tr>) <pef>PublicFunc</pef>() <bt>int</bt> {\n" +
           "    return <mr>ps</mr>.<slf>privateField<slf>\n" +
           "}\n" +
           "\n" +
           "func (<mr>ps</mr> <tr>PublicStruct</tr>) <plf>privateFunc</plf>() {\n" +
           "    return <mr>ps</mr>.<sef>PublicField</sef>\n" +
           "}\n" +
           "\n" +
           "func (<mr>ps</mr> <tr>privateStruct</tr>) <pef>PublicFunc</pef>() <bt>int</bt> {\n" +
           "    return <mr>ps</mr>.<slf>privateField</slf>\n" +
           "}\n" +
           "\n" +
           "func (<mr>ps</mr> <tr>privateStruct</tr>) <plf>privateFunc</plf>() {\n" +
           "    return <mr>ps</mr>.<sef>PublicField</sef>\n" +
           "}\n" +
           "\n" +
           "func <plf>variableFunc</plf>(<fp>demo1</fp> <bt>int</bt>) {\n" +
           "    <fp>demo1</fpm> = 3\n" +
           "    <lv>a</lv> := <tr>PublicStruct</tr>{}\n" +
           "    <lv>a</lv>.<slf>privateFunc</slf>()\n" +
           "    <lv>demo2</lv> := 4\n" +
           "    if <sv>demo1</sv>, <sv>demo2</sv> := <plf>privateFunc</plf>(); <pv>demo1</pv> != 3 {\n" +
           "        _ = <sv>demo1</sv>\n" +
           "        _ = <sv>demo2</sv>\n" +
           "        return\n" +
           "    }\n" +
           "<ll>demoLabel</ll>:\n" +
           "    for <sv>demo1</sv> := range []<bt>int</bt>{1, 2, 3, 4} {\n" +
           "        _ = <sv>demo1</sv>\n" +
           "        continue <ll>demoLabel</ll>\n" +
           "    }\n" +
           "\n" +
           "    switch {\n" +
           "    case 1 == 2:\n" +
           "        <sv>demo1</sv>, <sv>demo2</sv> := <plf>privateFunc</plf>()\n" +
           "        _ = <sv>demo1</sv>\n" +
           "        _ = <sv>demo2</sv>\n" +
           "    default:\n" +
           "        _ = <fp>demo1</fp>\n" +
           "    }\n" +
           "\n" +
           "    <lv>b</lv> := func() <bt>int</bt> {\n" +
           "        return 1\n" +
           "    }\n" +
           "    _ = <lv>b</lv>()\n" +
           "    _ = <pef>PublicFunc</pef>()\n" +
           "    _ = <plf>variableFunc</plf>(1)\n" +
           "    _ = <fp>demo1</fp>\n" +
           "    _ = <lv>demo2</lv>\n" +
           "\n" +
           "}\n" +
           "\n" +
           "func main() {\n" +
           "    fmt.<pef>Println</pef>(\"demo\")\n" +
           "    <plf>variableFunc</plf>(1)\n" +
           "    <lv>c</lv> := <bt>bt</bt>\n" +
           "    <lv>d</lv> := <bt>nil</bt>\n" +
           "    _, _ = <lv>c</lv>, <lv>d</lv>\n" +
           "}\n";
  }

  @NotNull
  public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
    return ATTRIBUTES_KEY_MAP;
  }
}
