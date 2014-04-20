package com.goide.util;

import org.jetbrains.annotations.NotNull;

public class GoUtil {
  @NotNull
  public static String replaceLast(@NotNull String src, @NotNull String from) {
    return src.endsWith(from) ? src.substring(0, src.length() - from.length()) : src;
  }

  @NotNull
  public static String replaceFirst(@NotNull String src, @NotNull String from) {
    return src.startsWith(from) ? src.substring(from.length()) : src;
  }
}