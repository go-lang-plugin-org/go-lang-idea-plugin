package com.goide.util;

import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.text.CharSequenceHashingStrategy;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoUtil {
  public static final Pattern FULL = Pattern.compile("\\w+_(\\w+)_(\\w+)");
  public static final Pattern SHORT = Pattern.compile("\\w+_(\\w+)");
  public static final Set<CharSequence> LINUX = set("linux", "no", "unix", "posix", "notwin");
  public static final Set<CharSequence> MAC = set("darwin", "no", "unix", "posix", "notwin");
  public static final Set<CharSequence> WINDOWS = set("windows", "no");
  public static final Set<CharSequence> OS = set("openbsd", "plan9", "unix", "linux", "netbsd", "darwin", "dragonfly", "bsd", "windows", 
                                                 "posix", "freebsd", "notwin");

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
      if (!OS.contains(os)) return true;
      return os(os) && (SystemInfo.is64Bit && arch.contains("64") || SystemInfo.is32Bit && arch.contains("386"));
    }
    matcher = SHORT.matcher(name);
    if (matcher.matches()) {
      String os = matcher.group(1);
      if (!OS.contains(os)) return true;
      return os(os);
    }
    return true;
  }

  private static boolean os(@NotNull String os) {
    return SystemInfo.isLinux ? LINUX.contains(os) :
           SystemInfo.isMac ? MAC.contains(os) :
           !SystemInfo.isWindows || WINDOWS.contains(os);
  }

  @NotNull
  private static THashSet<CharSequence> set(@NotNull String... strings) {
    return ContainerUtil.newTroveSet(CharSequenceHashingStrategy.CASE_INSENSITIVE, strings);
  }
}