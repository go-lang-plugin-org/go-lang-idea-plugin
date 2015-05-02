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

package com.goide.util;

import com.goide.GoCodeInsightFixtureTestCase;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.*;

public class GoBuildMatcherTest extends GoCodeInsightFixtureTestCase {

  private Set<CharSequence> os = new HashSet<CharSequence>(Arrays.asList("darwin", "linux", "windows", "plan9"));
  private Set<CharSequence> arch = new HashSet<CharSequence>(Arrays.asList("386", "amd64", "arm"));
  private Set<CharSequence> tags = new HashSet<CharSequence>(Arrays.asList("foo"));

  public void testMatchFile() {
    GoBuildMatcher matcher;

    matcher = new GoBuildMatcher(os, arch, null, "plan9", "arm");
    checkMatchFile(matcher, true, "foo_arm.go", "");
    checkMatchFile(matcher, false, "foo1_arm.go", "// +build linux");
    checkMatchFile(matcher, false, "foo_darwin.go", "");
    checkMatchFile(matcher, true, "foo.go", "");
    checkMatchFile(matcher, false, "foo1.go", "// +build linux");

    matcher = new GoBuildMatcher(os, arch, null, "android", "arm");
    checkMatchFile(matcher, true, "foo_linux.go", "");
    checkMatchFile(matcher, true, "foo_android.go", "");
    checkMatchFile(matcher, false, "foo_plan9.go", "");
    checkMatchFile(matcher, true, "android.go", "");
    checkMatchFile(matcher, true, "plan9.go", "");
    checkMatchFile(matcher, true, "plan9_test.go", "");
  }

  public void testGoodOSArchFile() {
    GoBuildMatcher matcher = new GoBuildMatcher(os, arch, null, "linux", "amd64");

    checkGoodOSArchFile(matcher, true, Arrays.asList(
      "file.go",
      "file_foo.go",
      "file_linux.go",
      "file_amd64.go",
      "file_linux_test.go",
      "file_amd64_test.go",
      "file_foo_linux.go",
      "file_foo_linux_test.go",
      "file_foo_amd64.go",
      "file_foo_amd64_test.go",
      "file_linux_amd64.go",
      "file_linux_amd64_test.go"
    ));

    checkGoodOSArchFile(matcher, false, Arrays.asList(
      "file_386.go",
      "file_windows.go",
      "file_windows_test.go",
      "file_linux_386.go",
      "file_linux_386_test.go",
      "file_windows_amd64.go",
      "file_windows_amd64_test.go",
      "file_darwin_arm.go",
      "file_foo_arm.go"
    ));
  }

  public void testMatch() {
    GoBuildMatcher matcher;

    matcher = new GoBuildMatcher(os, arch, null, "linux", "amd64");
    checkMatch(matcher, false, "");
    checkMatch(matcher, false, "!!");
    checkMatch(matcher, true, "linux,amd64");
    checkMatch(matcher, true, "linux,amd64,!foo");
    checkMatch(matcher, false, "linux,amd64,foo");
    checkMatch(matcher, true, "!windows,!foo");

    matcher = new GoBuildMatcher(os, arch, tags, "linux", "amd64");
    checkMatch(matcher, true, "linux,amd64");
    checkMatch(matcher, true, "linux,amd64,foo");
    checkMatch(matcher, false, "linux,amd64,!foo");
    checkMatch(matcher, true, "linux,amd64,!bar");
    checkMatch(matcher, false, "linux,amd64,bar");
  }

  private static void checkGoodOSArchFile(GoBuildMatcher matcher, boolean expected, List<String> names) {
    try {
      Method m = GoBuildMatcher.class.getDeclaredMethod("goodOSArchFile", String.class);
      m.setAccessible(true);

      for (String name : names) {
        Boolean res = (Boolean)m.invoke(matcher, name);
        if (expected) {
          assertTrue(name, res);
        } else {
          assertFalse(name, res);
        }
      }
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  private static void checkMatch(GoBuildMatcher matcher, boolean expected, String name) {
    try {
      Method m = GoBuildMatcher.class.getDeclaredMethod("match", String.class);
      m.setAccessible(true);

      Boolean res = (Boolean)m.invoke(matcher, name);
      if (expected) {
        assertTrue(name, res);
      } else {
        assertFalse(name, res);
      }
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  private void checkMatchFile(GoBuildMatcher matcher, boolean expected, @NotNull String fileName, @NotNull String text) {
    myFixture.configureByText(fileName, text + "\n\n package main\n\n func main() {}");
    PsiFile file =  myFixture.getFile();
    assertNotNull(file);

    assertEquals(fileName + ":" + text, expected, matcher.matchFile(file));
  }
}
