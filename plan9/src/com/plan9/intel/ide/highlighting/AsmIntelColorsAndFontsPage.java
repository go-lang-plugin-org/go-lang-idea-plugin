/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Florin Patan, Stuart Carnie
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

package com.plan9.intel.ide.highlighting;

import com.plan9.intel.Icons;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.openapi.util.io.StreamUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static com.plan9.intel.ide.highlighting.AsmIntelSyntaxHighlightingColors.*;

public class AsmIntelColorsAndFontsPage implements ColorSettingsPage {
  private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
    new AttributesDescriptor("Keyword", KEYWORD),
    new AttributesDescriptor("Line Comment", LINE_COMMENT),
    new AttributesDescriptor("Instruction", INSTRUCTION),
    new AttributesDescriptor("Pseudo Instruction", PSEUDO_INSTRUCTION),
    new AttributesDescriptor("String", STRING),
    new AttributesDescriptor("Label", LABEL),
    new AttributesDescriptor("Flags", FLAG),
    new AttributesDescriptor("Registers", REGISTER),
    new AttributesDescriptor("Parenthesis", PARENTHESIS),
    new AttributesDescriptor("Operator", OPERATOR),
    new AttributesDescriptor("Identifier", IDENTIFIER),
  };

  @Nullable
  @Override
  public Icon getIcon() {
    return Icons.FILE;
  }

  @NotNull
  @Override
  public SyntaxHighlighter getHighlighter() {
    return new AsmIntelSyntaxHighlighter();
  }

  private static String DEMO_TEXT;

  @NotNull
  @Override
  public String getDemoText() {
    if (DEMO_TEXT == null) {
      InputStream stream = getClass().getClassLoader().getResourceAsStream ("colorscheme/highlighterDemoText.s");
      try {
        DEMO_TEXT = StreamUtil.readText(stream, "UTF-8");
      }
      catch (IOException e) {
      }
    }

    return DEMO_TEXT;
  }

  @Nullable
  @Override
  public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
    return null;
  }

  @NotNull
  @Override
  public AttributesDescriptor[] getAttributeDescriptors() {
    return DESCRIPTORS;
  }

  @NotNull
  @Override
  public ColorDescriptor[] getColorDescriptors() {
    return ColorDescriptor.EMPTY_ARRAY;
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return "x86 Assembler";
  }
}
