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

package com.goide.parser;

import com.goide.GoTypes;
import com.intellij.lang.LighterASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.WhitespacesBinders;
import com.intellij.lang.impl.PsiBuilderAdapter;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.resolve.FileContextUtil;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.indexing.IndexingDataKeys;
import gnu.trove.TObjectIntHashMap;
import gnu.trove.TObjectIntProcedure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;

public class GoParserUtil extends GeneratedParserUtilBase {
  private static final Key<TObjectIntHashMap<String>> MODES_KEY = Key.create("MODES_KEY");

  @NotNull
  private static TObjectIntHashMap<String> getParsingModes(@NotNull PsiBuilder builder_) {
    TObjectIntHashMap<String> flags = builder_.getUserDataUnprotected(MODES_KEY);
    if (flags == null) builder_.putUserDataUnprotected(MODES_KEY, flags = new TObjectIntHashMap<String>());
    return flags;
  }

  public static boolean consumeBlock(PsiBuilder builder_, @SuppressWarnings("UnusedParameters") int level) {
    PsiFile file = builder_.getUserDataUnprotected(FileContextUtil.CONTAINING_FILE_KEY);
    if (file == null) return false;
    VirtualFile data = file.getUserData(IndexingDataKeys.VIRTUAL_FILE);
    if (data == null) return false;
    int i = 0;
    PsiBuilder.Marker m = builder_.mark();
    do {
      IElementType type = builder_.getTokenType();
      i += type == GoTypes.LBRACE ? 1 : type == GoTypes.RBRACE ? -1 : 0;  
      builder_.advanceLexer();
    }
    while (i != 0 && !builder_.eof());
    boolean result = i == 0;
    if (result) {
      m.drop();
    }
    else {
      m.rollbackTo();
    }
    return result;  
  }
  
  public static boolean emptyImportList(PsiBuilder builder_, @SuppressWarnings("UnusedParameters") int level) {
    PsiBuilder.Marker marker = getCurrentMarker(builder_ instanceof PsiBuilderAdapter ? ((PsiBuilderAdapter)builder_).getDelegate() : builder_);
    if (marker != null) {
      marker.setCustomEdgeTokenBinders(WhitespacesBinders.GREEDY_LEFT_BINDER, null);
    }
    return true;
  }

  public static boolean isModeOn(@NotNull PsiBuilder builder_, @SuppressWarnings("UnusedParameters") int level, String mode) {
    return getParsingModes(builder_).get(mode) > 0;
  }

  public static boolean withOn(PsiBuilder builder_, int level_, String mode, Parser parser) {
    return withImpl(builder_, level_, mode, true, parser, parser);
  }

  public static boolean withOff(PsiBuilder builder_, int level_, Parser parser, String... modes) {
    final TObjectIntHashMap<String> map = getParsingModes(builder_);

    TObjectIntHashMap<String> prev = new TObjectIntHashMap<String>();
    
    for (String mode : modes) {
      int p = map.get(mode);
      if (p > 0) {
        map.put(mode, 0);
        prev.put(mode, p);
      }
    }
    
    boolean result = parser.parse(builder_, level_);
    
    prev.forEachEntry(new TObjectIntProcedure<String>() {
      @Override
      public boolean execute(String mode, int p) {
        map.put(mode, p);
        return true;
      }
    });
    
    return result;
  }

  private static boolean withImpl(PsiBuilder builder_, int level_, String mode, boolean onOff, Parser whenOn, Parser whenOff) {
    TObjectIntHashMap<String> map = getParsingModes(builder_);
    int prev = map.get(mode);
    boolean change = ((prev & 1) == 0) == onOff;
    if (change) map.put(mode, prev << 1 | (onOff ? 1 : 0));
    boolean result = (change ? whenOn : whenOff).parse(builder_, level_);
    if (change) map.put(mode, prev);
    return result;
  }

  public static boolean isModeOff(@NotNull PsiBuilder builder_, @SuppressWarnings("UnusedParameters") int level, String mode) {
    return getParsingModes(builder_).get(mode) == 0;
  }

  public static boolean prevIsArrayType(@NotNull PsiBuilder builder_, @SuppressWarnings("UnusedParameters") int level) {
    LighterASTNode marker = builder_.getLatestDoneMarker();
    IElementType type = marker != null ? marker.getTokenType() : null;
    return type == GoTypes.ARRAY_OR_SLICE_TYPE || type == GoTypes.MAP_TYPE;
  }
  
  public static boolean keyOrValueExpression(@NotNull PsiBuilder builder_, int level) {
    PsiBuilder.Marker m = enter_section_(builder_);
    boolean r = GoParser.Expression(builder_, level + 1, -1);
    if (!r) r = GoParser.LiteralValue(builder_, level + 1);
    IElementType type = r && builder_.getTokenType() == GoTypes.COLON ? GoTypes.KEY : GoTypes.VALUE;
    exit_section_(builder_, m, type, r);
    return r;
  }

  public static boolean enterMode(@NotNull PsiBuilder builder_, @SuppressWarnings("UnusedParameters") int level, String mode) {
    TObjectIntHashMap<String> flags = getParsingModes(builder_);
    if (!flags.increment(mode)) flags.put(mode, 1);
    return true;
  }

  public static boolean exitMode(@NotNull PsiBuilder builder_, @SuppressWarnings("UnusedParameters") int level, String mode, boolean safe) {
    TObjectIntHashMap<String> flags = getParsingModes(builder_);
    int count = flags.get(mode);
    if (count == 1) flags.remove(mode);
    else if (count > 1) flags.put(mode, count - 1);
    else if (!safe) builder_.error("Could not exit inactive '" + mode + "' mode at offset " + builder_.getCurrentOffset());
    return true;
  }

  public static boolean exitMode(@NotNull PsiBuilder builder_, @SuppressWarnings("UnusedParameters") int level, String mode) {
    return exitMode(builder_, level,mode, false);
  }
  
  public static boolean exitModeSafe(@NotNull PsiBuilder builder_, @SuppressWarnings("UnusedParameters") int level, String mode) {
    return exitMode(builder_, level,mode, true);
  }

  public static boolean isBuiltin(@NotNull PsiBuilder builder_, @SuppressWarnings("UnusedParameters") int level) {
    LighterASTNode marker = builder_.getLatestDoneMarker();
    if (marker == null) return false;
    String text = String.valueOf(builder_.getOriginalText().subSequence(marker.getStartOffset(), marker.getEndOffset()));
    return "make".equals(text) || "new".equals(text);
  }

  @Nullable
  private static PsiBuilder.Marker getCurrentMarker(@NotNull PsiBuilder builder_) {
    try {
      for (Field field : builder_.getClass().getDeclaredFields()) {
        if ("MyList".equals(field.getType().getSimpleName())) {
          field.setAccessible(true);
          List production = (List)field.get(builder_);
          return (PsiBuilder.Marker)ContainerUtil.getLastItem(production);
        }
      }
    }
    catch (Exception ignored) {}
    return null;
  }
}
