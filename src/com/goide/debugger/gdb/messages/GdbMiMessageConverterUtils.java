/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Florin Patan
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

package com.goide.debugger.gdb.messages;

import com.goide.debugger.gdb.gdbmi.GdbMiValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility functions for use with the message converter.
 */
@SuppressWarnings("unused")
public class GdbMiMessageConverterUtils {
  /**
   * Converts a hexadecimal string to a long.
   */
  @Nullable
  public static Long hexStringToLong(@NotNull GdbMiValue value) {
    Long longValue = null;
    if (value.type == GdbMiValue.Type.String && value.string.substring(0, 2).equals("0x")) {
      longValue = Long.parseLong(value.string.substring(2), 16);
    }
    return longValue;
  }

  /**
   * Returns true if value is equal to "all".
   */
  @NotNull
  public static Boolean valueIsAll(@NotNull GdbMiValue value) {
    return value.type == GdbMiValue.Type.String && value.string.equals("all");
  }

  /**
   * Returns null if value is equal to "all", or otherwise requests normal processing for the
   * value.
   */
  @Nullable
  public static Object passThroughIfNotAll(@NotNull GdbMiValue value) {
    if (valueIsAll(value)) {
      return null;
    }
    return GdbMiMessageConverter.ValueProcessorPassThrough;
  }

  /**
   * Returns null if value is equal to "??", or otherwise requests normal processing for the
   * value.
   */
  @Nullable
  public static Object passThroughIfNotQQ(@NotNull GdbMiValue value) {
    if (value.type == GdbMiValue.Type.String && value.string.equals("??")) {
      return null;
    }
    return GdbMiMessageConverter.ValueProcessorPassThrough;
  }
}
