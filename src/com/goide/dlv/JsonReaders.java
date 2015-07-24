/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
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

package com.goide.dlv;

import com.google.gson.stream.JsonToken;
import com.intellij.util.ArrayUtilRt;
import gnu.trove.TDoubleArrayList;
import gnu.trove.THashMap;
import gnu.trove.TIntArrayList;
import gnu.trove.TLongArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jsonProtocol.StringIntPair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class JsonReaders {
  private JsonReaders() {
  }

  private static void checkIsNull(JsonReaderEx reader, String fieldName) {
    if (reader.peek() == JsonToken.NULL) {
      throw new RuntimeException("Field is not nullable" + (fieldName == null ? "" : (": " + fieldName)));
    }
  }

  public static String readRawString(JsonReaderEx reader) {
    return reader.nextString(true);
  }

  // Don't use Guava CaseFormat.*! ObjectWithURL must be converted to OBJECT_WITH_URL
  public static String convertRawEnumName(@NotNull String enumValue) {
    int n = enumValue.length();
    StringBuilder builder = new StringBuilder(n + 4);
    boolean prevIsLowerCase = false;
    for (int i = 0; i < n; i++) {
      char c = enumValue.charAt(i);
      if (c == '-' || c == ' ') {
        builder.append('_');
        continue;
      }

      if (Character.isUpperCase(c)) {
        // second check handle "CSPViolation" (transform to CSP_VIOLATION)
        if (prevIsLowerCase || (i != 0 && (i + 1) < n && Character.isLowerCase(enumValue.charAt(i + 1)))) {
          builder.append('_');
        }
        builder.append(c);
        prevIsLowerCase = false;
      }
      else {
        builder.append(Character.toUpperCase(c));
        prevIsLowerCase = true;
      }
    }
    return builder.toString();
  }

  public static <T extends Enum<T>> T readEnum(@NotNull JsonReaderEx reader, @NotNull Class<T> enumClass) {
    if (reader.peek() == JsonToken.NULL) {
      reader.skipValue();
      return null;
    }

    try {
      return Enum.valueOf(enumClass, convertRawEnumName(reader.nextString()));
    }
    catch (IllegalArgumentException ignored) {
      return Enum.valueOf(enumClass, "NO_ENUM_CONST");
    }
  }

  public static Object read(JsonReaderEx reader) {
    switch (reader.peek()) {
      case BEGIN_ARRAY:
        return nextList(reader);

      case BEGIN_OBJECT:
        reader.beginObject();
        return nextObject(reader);

      case STRING:
        return reader.nextString();

      case NUMBER:
        return reader.nextDouble();

      case BOOLEAN:
        return reader.nextBoolean();

      case NULL:
        reader.nextNull();
        return null;

      default: throw new IllegalStateException();
    }
  }

  public static Map<String, Object> nextObject(JsonReaderEx reader) {
    Map<String, Object> map = new THashMap<String, Object>();
    while (reader.hasNext()) {
      map.put(reader.nextName(), read(reader));
    }
    reader.endObject();
    return map;
  }

  public static <T> List<T> nextList(JsonReaderEx reader) {
    reader.beginArray();
    if (!reader.hasNext()) {
      reader.endArray();
      return Collections.emptyList();
    }

    List<T> list = new ArrayList<T>();
    do {
      //noinspection unchecked
      list.add((T)read(reader));
    }
    while (reader.hasNext());
    reader.endArray();
    return list;
  }

  public static List<String> readRawStringArray(JsonReaderEx reader) {
    reader.beginArray();
    if (!reader.hasNext()) {
      reader.endArray();
      return Collections.emptyList();
    }

    List<String> list = new ArrayList<String>();
    do {
      //noinspection unchecked
      list.add(reader.nextString(true));
    }
    while (reader.hasNext());
    reader.endArray();
    return list;
  }

  public static long[] readLongArray(JsonReaderEx reader) {
    checkIsNull(reader, null);
    reader.beginArray();
    if (!reader.hasNext()) {
      reader.endArray();
      return ArrayUtilRt.EMPTY_LONG_ARRAY;
    }

    TLongArrayList result = new TLongArrayList();
    do {
      result.add(reader.nextLong());
    }
    while (reader.hasNext());
    reader.endArray();
    return result.toNativeArray();
  }

  public static double[] readDoubleArray(JsonReaderEx reader) {
    checkIsNull(reader, null);
    reader.beginArray();
    if (!reader.hasNext()) {
      reader.endArray();
      return new double[]{0};
    }

    TDoubleArrayList result = new TDoubleArrayList();
    do {
      result.add(reader.nextDouble());
    }
    while (reader.hasNext());
    reader.endArray();
    return result.toNativeArray();
  }

  public static int[] readIntArray(JsonReaderEx reader) {
    checkIsNull(reader, null);
    reader.beginArray();
    if (!reader.hasNext()) {
      reader.endArray();
      return ArrayUtilRt.EMPTY_INT_ARRAY;
    }

    TIntArrayList result = new TIntArrayList();
    do {
      result.add(reader.nextInt());
    }
    while (reader.hasNext());
    reader.endArray();
    return result.toNativeArray();
  }

  public static List<StringIntPair> readIntStringPairs(JsonReaderEx reader) {
    checkIsNull(reader, null);
    reader.beginArray();
    if (!reader.hasNext()) {
      reader.endArray();
      return Collections.emptyList();
    }

    List<StringIntPair> result = new ArrayList<StringIntPair>();
    do {
      reader.beginArray();
      result.add(new StringIntPair(reader.nextInt(), reader.nextString()));
      reader.endArray();
    }
    while (reader.hasNext());
    reader.endArray();
    return result;
  }

  public static boolean findBooleanField(String name, JsonReaderEx reader) {
    reader.beginObject();
    while (reader.hasNext()) {
      if (reader.nextName().equals(name)) {
        return reader.nextBoolean();
      }
      else {
        reader.skipValue();
      }
    }
    return false;
  }
}