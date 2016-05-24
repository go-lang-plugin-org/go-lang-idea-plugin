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

package com.goide.marker;

import com.goide.GoCodeInsightFixtureTestCase;
import com.intellij.codeInsight.daemon.GutterMark;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GoRecursiveMethodCallMarkerInfoTest extends GoCodeInsightFixtureTestCase {
  public void testRecursive() { doTest(); }
  public void testGo()        { doTest(); }

  @NotNull
  @Override
  protected String getBasePath() {
    return "marker";
  }

  private void doTest() {
    myFixture.configureByFile(getTestName(false) + ".go");
    List<String> textList = ContainerUtil.newArrayList();
    for (GutterMark gutter : myFixture.findGuttersAtCaret()) {
      String text = gutter.getTooltipText();
      textList.add(text);
      if ("Recursive call".equals(text) && AllIcons.Gutter.RecursiveMethod.equals(gutter.getIcon())) return;
    }
    fail("Not found gutter mark: " + "Recursive call" + " " + AllIcons.Gutter.RecursiveMethod + "\nin\n" + StringUtil.join(textList, "\n"));
  }
}