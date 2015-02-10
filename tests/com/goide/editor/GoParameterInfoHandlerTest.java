/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Mihai Toader
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

package com.goide.editor;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.psi.GoArgumentList;
import com.intellij.codeInsight.hint.ParameterInfoComponent;
import com.intellij.lang.parameterInfo.CreateParameterInfoContext;
import com.intellij.lang.parameterInfo.ParameterInfoUIContextEx;
import com.intellij.testFramework.utils.parameterInfo.MockCreateParameterInfoContext;
import com.intellij.testFramework.utils.parameterInfo.MockUpdateParameterInfoContext;

import java.io.IOException;


public class GoParameterInfoHandlerTest extends GoCodeInsightFixtureTestCase {
  private GoParameterInfoHandler myParameterInfoHandler;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    myParameterInfoHandler = new GoParameterInfoHandler();
  }

  public void testFuncParam() throws IOException {
    doTest(1, "<html>num int, <b>text string</b></html>");
  }

  public void testFuncParamMulti() throws IOException {
    doTest(4, "<html>a int, b int, c int, d string, <b>e string</b>, f string</html>");
  }

  public void testFuncParamNone() throws IOException {
    doTest(0, "");
  }

  public void testFuncParamEllipsis() throws IOException {
    doTest(5, "<html>num int, text string, <b>more ...int</b></html>");
  }

  public void testFuncEmbedInner() throws IOException {
    doTest(1, "<html>num int, <b>text string</b></html>");
  }

  public void testFuncEmbedOuter() throws IOException {
    doTest(2, "<html>a int, b int, <b>c int</b>, d int</html>");
  }

  public void testMethParam() throws IOException {
    doTest(1, "<html>num int, <b>text string</b></html>");
  }

  public void testMethParamNone() throws IOException {
    doTest(0, "");
  }

  public void testMethParamEllipsis() throws IOException {
    doTest(5, "<html>num int, text string, <b>more ...int</b></html>");
  }

  private void doTest(final int expectedParamIdx, final String expectedPresentation) throws IOException {
    // Given
    myFixture.configureByFile(getTestName(true) + ".go");
    // When
    final Object[] itemsToShow = getItemsToShow();
    final int paramIdx = getHighlightedItem();
    final String presentation = getPresentation(itemsToShow, paramIdx);
    // Then
    assertEquals(1, itemsToShow.length);
    assertEquals(expectedParamIdx, paramIdx);
    assertEquals(expectedPresentation, presentation);
  }

  private Object[] getItemsToShow() {
    final CreateParameterInfoContext createCtx = new MockCreateParameterInfoContext(
      myFixture.getEditor(), myFixture.getFile());
    final GoArgumentList psiElement = myParameterInfoHandler.findElementForParameterInfo(createCtx);
    assertNotNull(psiElement);
    myParameterInfoHandler.showParameterInfo(psiElement, createCtx);
    return createCtx.getItemsToShow();
  }

  private int getHighlightedItem() {
    final MockUpdateParameterInfoContext updateCtx = new MockUpdateParameterInfoContext(
      myFixture.getEditor(), myFixture.getFile());
    final GoArgumentList psiElement = myParameterInfoHandler.findElementForUpdatingParameterInfo(updateCtx);
    assertNotNull(psiElement);
    myParameterInfoHandler.updateParameterInfo(psiElement, updateCtx);
    return updateCtx.getCurrentParameter();
  }

  private String getPresentation(final Object[] itemsToShow, final int paramIdx) {
    final ParameterInfoUIContextEx uiCtx = ParameterInfoComponent.createContext(
      itemsToShow, myFixture.getEditor(), myParameterInfoHandler, paramIdx);
    return myParameterInfoHandler.updatePresentation(itemsToShow[0], uiCtx);
  }

  @Override
  protected String getBasePath() {
    return "parameterInfo";
  }
}
