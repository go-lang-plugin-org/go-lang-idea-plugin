/*
 * Copyright 2013-2016 Sergey Ignatov, Alexander Zolotov, Florin Patan
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

package com.goide.regexp;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.lang.regexp.RegExpLanguageHost;
import org.intellij.lang.regexp.psi.RegExpChar;
import org.intellij.lang.regexp.psi.RegExpGroup;
import org.intellij.lang.regexp.psi.RegExpNamedGroupRef;
import org.intellij.lang.regexp.psi.RegExpSimpleClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

// see https://golang.org/pkg/regexp/syntax/
public class GoRegexHost implements RegExpLanguageHost {
  @Override
  public boolean characterNeedsEscaping(char c) {
    return false;
  }

  @Override
  public boolean supportsPerl5EmbeddedComments() {
    return false;
  }

  @Override
  public boolean supportsPossessiveQuantifiers() {
    return false;
  }

  @Override
  public boolean supportsPythonConditionalRefs() {
    return false;
  }

  @Override
  public boolean supportsNamedGroupSyntax(RegExpGroup group) {
    return group.isPythonNamedGroup(); // only (?P<name>) group is supported
  }

  @Override
  public boolean supportsNamedGroupRefSyntax(RegExpNamedGroupRef ref) {
    return false;
  }

  @Override
  public boolean supportsExtendedHexCharacter(RegExpChar regExpChar) {
    return true;
  }

  @Override
  public boolean isValidCategory(@NotNull String category) {
    return Lazy.KNOWN_PROPERTIES.contains(category);
  }

  @NotNull
  @Override
  public String[][] getAllKnownProperties() {
    return Lazy.KNOWN_PROPERTIES_ARRAY;
  }

  @Nullable
  @Override
  public String getPropertyDescription(@Nullable String name) {
    return name;
  }

  @NotNull
  @Override
  public String[][] getKnownCharacterClasses() {
    return Lazy.CHARACTER_CLASSES;
  }

  @Override
  public boolean supportsSimpleClass(RegExpSimpleClass simpleClass) {
    switch (simpleClass.getKind()) {
      case HORIZONTAL_SPACE:
      case NON_HORIZONTAL_SPACE:
      case NON_VERTICAL_SPACE:
      case XML_NAME_START:
      case NON_XML_NAME_START:
      case XML_NAME_PART:
      case NON_XML_NAME_PART:
      case UNICODE_GRAPHEME:
      case UNICODE_LINEBREAK:
        return false;

      case ANY:
      case DIGIT:
      case NON_DIGIT:
      case WORD:
      case NON_WORD:
      case SPACE:
      case NON_SPACE:
      case VERTICAL_SPACE:
        return true;
    }
    return false;
  }

  @Override
  public boolean supportsLiteralBackspace(RegExpChar aChar) {
    return false;
  }

  @Override
  public boolean supportsInlineOptionFlag(char flag, PsiElement context) {
    return StringUtil.containsChar("imsU", flag);
  }

  private interface Lazy {
    // SDK/unicode/tables.go
    Set<String> KNOWN_PROPERTIES = ContainerUtil.immutableSet("C", "Cc", "Cf", "Co", "Cs", "L", "Ll", "Lm", "Lo", "Lt", "Lu", "M", "Mc",
                                                              "Me", "Mn", "N", "Nd", "Nl", "No", "P", "Pc", "Pd", "Pe", "Pf", "Pi", "Po",
                                                              "Ps", "S", "Sc", "Sk", "Sm", "So", "Z", "Zl", "Zp", "Zs",

                                                              "Ahom", "Anatolian_Hieroglyphs", "Arabic", "Armenian", "Avestan", "Balinese",
                                                              "Bamum", "Bassa_Vah", "Batak", "Bengali", "Bopomofo", "Brahmi", "Braille",
                                                              "Buginese", "Buhid", "Canadian_Aboriginal", "Carian", "Caucasian_Albanian",
                                                              "Chakma", "Cham", "Cherokee", "Common", "Coptic", "Cuneiform", "Cypriot",
                                                              "Cyrillic", "Deseret", "Devanagari", "Duployan", "Egyptian_Hieroglyphs",
                                                              "Elbasan", "Ethiopic", "Georgian", "Glagolitic", "Gothic", "Grantha", "Greek",
                                                              "Gujarati", "Gurmukhi", "Han", "Hangul", "Hanunoo", "Hatran", "Hebrew",
                                                              "Hiragana", "Imperial_Aramaic", "Inherited", "Inscriptional_Pahlavi",
                                                              "Inscriptional_Parthian", "Javanese", "Kaithi", "Kannada", "Katakana",
                                                              "Kayah_Li", "Kharoshthi", "Khmer", "Khojki", "Khudawadi", "Lao", "Latin",
                                                              "Lepcha", "Limbu", "Linear_A", "Linear_B", "Lisu", "Lycian", "Lydian",
                                                              "Mahajani", "Malayalam", "Mandaic", "Manichaean", "Meetei_Mayek",
                                                              "Mende_Kikakui", "Meroitic_Cursive", "Meroitic_Hieroglyphs", "Miao", "Modi",
                                                              "Mongolian", "Mro", "Multani", "Myanmar", "Nabataean", "New_Tai_Lue", "Nko",
                                                              "Ogham", "Ol_Chiki", "Old_Hungarian", "Old_Italic", "Old_North_Arabian",
                                                              "Old_Permic", "Old_Persian", "Old_South_Arabian", "Old_Turkic", "Oriya",
                                                              "Osmanya", "Pahawh_Hmong", "Palmyrene", "Pau_Cin_Hau", "Phags_Pa",
                                                              "Phoenician", "Psalter_Pahlavi", "Rejang", "Runic", "Samaritan", "Saurashtra",
                                                              "Sharada", "Shavian", "Siddham", "SignWriting", "Sinhala", "Sora_Sompeng",
                                                              "Sundanese", "Syloti_Nagri", "Syriac", "Tagalog", "Tagbanwa", "Tai_Le",
                                                              "Tai_Tham", "Tai_Viet", "Takri", "Tamil", "Telugu", "Thaana", "Thai",
                                                              "Tibetan", "Tifinagh", "Tirhuta", "Ugaritic", "Vai", "Warang_Citi", "Yi");

    String[][] KNOWN_PROPERTIES_ARRAY = ContainerUtil.map2Array(KNOWN_PROPERTIES, new String[KNOWN_PROPERTIES.size()][2],
                                                                s -> new String[]{s, ""});

    String[][] CHARACTER_CLASSES = {
      // Empty strings
      {"A", "at beginning of text"},
      {"b", "at ASCII word boundary (\\w on one side and \\W, \\A, or \\z on the other)"},
      {"B", "not at ASCII word boundary"},
      {"z", "at end of text"},
      {"Z", "end of the text but for the final terminator, if any"},

      // Escape sequences
      {"a", "bell (== \007)"},
      {"f", "form feed (== \014)"},
      {"t", "horizontal tab (== \011)"},
      {"n", "newline (== \012)"},
      {"r", "carriage return (== \015)"},
      {"v", "vertical tab character (== \013)"},
      {"*", "literal *, for any punctuation character *"},
      {"Q", "nothing, but quotes all characters until \\E"},
      {"E", "nothing, but ends quoting started by \\Q"},

      // Perl character classes (all ASCII-only):
      {"d", "digits (== [0-9])"},
      {"D", "not digits (== [^0-9])"},
      {"s", "whitespace (== [\t\n\f\r ])"},
      {"S", "not whitespace (== [^\t\n\f\r ])"},
      {"w", "word characters (== [0-9A-Za-z_])"},
      {"W", "not word characters (== [^0-9A-Za-z_])"},
    };
  }
} 