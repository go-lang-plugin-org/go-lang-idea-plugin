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
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class GoBuildMatcherTest extends GoCodeInsightFixtureTestCase {
  private static final Set<String> OS = ContainerUtil.newHashSet("darwin", "linux", "windows", "plan9");
  private static final Set<String> ARCH = ContainerUtil.newHashSet("386", "amd64", "arm");
  private static final Set<String> TAGS = ContainerUtil.newHashSet("foo");

  public void testMatchFile() {
    GoBuildMatcher matcher;

    matcher = new GoBuildMatcher(OS, ARCH, null, "plan9", "arm");
    checkMatchFile(matcher, true, "foo_arm.go", "");
    checkMatchFile(matcher, false, "foo1_arm.go", "// +build linux");
    checkMatchFile(matcher, false, "foo_darwin.go", "");
    checkMatchFile(matcher, true, "foo.go", "");
    checkMatchFile(matcher, false, "foo1.go", "// +build linux");

    matcher = new GoBuildMatcher(OS, ARCH, null, "android", "arm");
    checkMatchFile(matcher, true, "foo_linux.go", "");
    checkMatchFile(matcher, true, "foo_android.go", "");
    checkMatchFile(matcher, false, "foo_plan9.go", "");
    checkMatchFile(matcher, true, "android.go", "");
    checkMatchFile(matcher, true, "android.go", "// +build linux\n");
    checkMatchFile(matcher, true, "plan9.go", "");
    checkMatchFile(matcher, true, "plan9_test.go", "");
  }

  public void testMatchFileName() {
    GoBuildMatcher matcher = new GoBuildMatcher(OS, ARCH, null, "linux", "amd64");

      assertTrue(matcher.matchFileName("file.go"));
      assertTrue(matcher.matchFileName("file_foo.go"));
      assertTrue(matcher.matchFileName("file_linux.go"));
      assertTrue(matcher.matchFileName("file_amd64.go"));
      assertTrue(matcher.matchFileName("file_linux_test.go"));
      assertTrue(matcher.matchFileName("file_amd64_test.go"));
      assertTrue(matcher.matchFileName("file_foo_linux.go"));
      assertTrue(matcher.matchFileName("file_foo_linux_test.go"));
      assertTrue(matcher.matchFileName("file_foo_amd64.go"));
      assertTrue(matcher.matchFileName("file_foo_amd64_test.go"));
      assertTrue(matcher.matchFileName("file_linux_amd64.go"));
      assertTrue(matcher.matchFileName("file_linux_amd64_test.go"));

      assertFalse(matcher.matchFileName("file_386.go"));
      assertFalse(matcher.matchFileName("file_windows.go"));
      assertFalse(matcher.matchFileName("file_windows_test.go"));
      assertFalse(matcher.matchFileName("file_linux_386.go"));
      assertFalse(matcher.matchFileName("file_linux_386_test.go"));
      assertFalse(matcher.matchFileName("file_windows_amd64.go"));
      assertFalse(matcher.matchFileName("file_windows_amd64_test.go"));
      assertFalse(matcher.matchFileName("file_darwin_arm.go"));
      assertFalse(matcher.matchFileName("file_foo_arm.go"));
  }
  
  public void testMatchBuildFlags() {
    GoBuildMatcher matcher = new GoBuildMatcher(OS, ARCH, null, "linux", "amd64");
    assertFalse(matcher.matchBuildFlag(""));
    assertFalse(matcher.matchBuildFlag("!!"));
    assertTrue(matcher.matchBuildFlag("linux,amd64"));
    assertTrue(matcher.matchBuildFlag("linux,amd64,!foo"));
    assertFalse(matcher.matchBuildFlag("linux,amd64,foo"));
    assertTrue(matcher.matchBuildFlag("!windows,!foo"));
  }

  public void testMatchSupportedTags() {
    GoBuildMatcher matcher = new GoBuildMatcher(OS, ARCH, TAGS, "linux", "amd64");
    assertTrue(matcher.matchBuildFlag("linux,amd64"));
    assertTrue(matcher.matchBuildFlag("linux,amd64,foo"));
    assertFalse(matcher.matchBuildFlag("linux,amd64,!foo"));
    assertTrue(matcher.matchBuildFlag("linux,amd64,!bar"));
    assertFalse(matcher.matchBuildFlag("linux,amd64,bar"));
  }
  
  private void checkMatchFile(GoBuildMatcher matcher, boolean expected, @NotNull String fileName, @NotNull String text) {
    myFixture.configureByText(fileName, text + "\n\n package main\n\n func main() {}");
    assertEquals(fileName + ":" + text, expected, matcher.matchFile(myFixture.getFile()));
  }
}
