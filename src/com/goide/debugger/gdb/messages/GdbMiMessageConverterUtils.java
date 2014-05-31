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
