package com.goide.util;

import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.text.CharSequenceHashingStrategy;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoUtil {
  public static final Pattern FULL = Pattern.compile("\\w+_(\\w+)_(\\w+)");
  public static final Pattern SHORT = Pattern.compile("\\w+_(\\w+)");
  public static final THashSet<CharSequence> LINUX = ContainerUtil.newTroveSet(CharSequenceHashingStrategy.CASE_INSENSITIVE, "linux", "no", "unix", "posix", "notwin");
  public static final THashSet<CharSequence> MAC = ContainerUtil.newTroveSet(CharSequenceHashingStrategy.CASE_INSENSITIVE, "darwin", "no", "unix", "posix", "notwin");
  public static final THashSet<CharSequence> WINDOWS = ContainerUtil.newTroveSet(CharSequenceHashingStrategy.CASE_INSENSITIVE, "windows", "no");

  @NotNull
  public static String replaceLast(@NotNull String src, @NotNull String from) {
    return src.endsWith(from) ? src.substring(0, src.length() - from.length()) : src;
  }

  @NotNull
  public static String replaceFirst(@NotNull String src, @NotNull String from) {
    return src.startsWith(from) ? src.substring(from.length()) : src;
  }

  public static boolean allowed(@NotNull PsiFile file) {
    String name = replaceLast(FileUtil.getNameWithoutExtension(file.getName()), "_test");
    Matcher matcher = FULL.matcher(name);
    if (matcher.matches()) {
      String os = matcher.group(1);
      String arch = matcher.group(2);
      return os(os) && (SystemInfo.is64Bit && arch.contains("64") || SystemInfo.is32Bit && arch.contains("386"));
    }
    matcher = SHORT.matcher(name);
    if (matcher.matches()) {
      String os = matcher.group(1);
      return os(os);
    }
    return true;
  }

  private static boolean os(@NotNull String os) {
    return SystemInfo.isLinux ? LINUX.contains(os) : 
           SystemInfo.isMac ? MAC.contains(os) :
           !SystemInfo.isWindows || WINDOWS.contains(os);
  }
}