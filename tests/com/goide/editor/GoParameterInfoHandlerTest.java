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

package com.goide.editor;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.psi.GoArgumentList;
import com.intellij.codeInsight.hint.ParameterInfoComponent;
import com.intellij.lang.parameterInfo.CreateParameterInfoContext;
import com.intellij.lang.parameterInfo.ParameterInfoUIContextEx;
import com.intellij.testFramework.utils.parameterInfo.MockCreateParameterInfoContext;
import com.intellij.testFramework.utils.parameterInfo.MockUpdateParameterInfoContext;
import org.jetbrains.annotations.NotNull;

public class GoParameterInfoHandlerTest extends GoCodeInsightFixtureTestCase {
  private GoParameterInfoHandler myParameterInfoHandler;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    myParameterInfoHandler = new GoParameterInfoHandler();
  }

  // @formatter:off
  public void testUnnamedParameters()         { doTest("<html><b>string</b>, interface{}</html>"); }
  public void testUnnamedAndNamedParameters() { doTest("<html><b>a string</b>, interface{}</html>"); }
  public void testFuncParamNone()             { doTest("<html>&lt;no parameters&gt;</html>"); }
  public void testChainedCall()               { doTest("<html><b>param1 string</b>, param2 int</html>"); }
  public void testMethParamNone()             { doTest("<html>&lt;no parameters&gt;</html>"); }
  public void testFieldMethCall()             { doTest("<html><b>a int</b>, b int</html>"); }
  public void testFuncTypes()                 { doTest("<html><b>string</b></html>"); }
  public void testFunctionTypeByRef()         { doTest("<html><b>intParam int</b></html>"); }
  public void testFuncParam()                 { doTest(1, "<html>num int, <b>text string</b></html>"); }
  public void testFuncParamMulti()            { doTest(4, "<html>a int, b int, c int, d string, <b>e string</b>, f string</html>"); }
  public void testFuncParamEllipsis()         { doTest(5, "<html>num int, text string, <b>more ...int</b></html>"); }
  public void testFuncEmbedInner()            { doTest(1, "<html>num int, <b>text string</b></html>"); }
  public void testFuncEmbedOuter()            { doTest(2, "<html>a int, b int, <b>c int</b>, d int</html>"); }
  public void testMethParam()                 { doTest(1, "<html>num int, <b>text string</b></html>"); }
  public void testMethParamEllipsis()         { doTest(5, "<html>num int, text string, <b>more ...int</b></html>"); }
  public void testClosure()                   { doTest(1, "<html>param1 string, <b>param2 string</b></html>"); }

  private void doTest(@NotNull String expectedPresentation) { doTest(0, expectedPresentation); }
  // @formatter:on

  private void doTest(int expectedParamIdx, @NotNull String expectedPresentation) {
    // Given
    myFixture.configureByFile(getTestName(true) + ".go");
    // When
    Object[] itemsToShow = getItemsToShow();
    int paramIdx = getHighlightedItem();
    String presentation = getPresentation(itemsToShow, paramIdx);
    // Then
    assertEquals(1, itemsToShow.length);
    assertEquals(expectedParamIdx, paramIdx);
    assertEquals(expectedPresentation, presentation);
  }

  private Object[] getItemsToShow() {
    CreateParameterInfoContext createCtx = new MockCreateParameterInfoContext(myFixture.getEditor(), myFixture.getFile());
    GoArgumentList psiElement = myParameterInfoHandler.findElementForParameterInfo(createCtx);
    assertNotNull(psiElement);
    myParameterInfoHandler.showParameterInfo(psiElement, createCtx);
    return createCtx.getItemsToShow();
  }

  private int getHighlightedItem() {
    MockUpdateParameterInfoContext updateCtx = new MockUpdateParameterInfoContext(myFixture.getEditor(), myFixture.getFile());
    GoArgumentList psiElement = myParameterInfoHandler.findElementForUpdatingParameterInfo(updateCtx);
    assertNotNull(psiElement);
    myParameterInfoHandler.updateParameterInfo(psiElement, updateCtx);
    return updateCtx.getCurrentParameter();
  }

  private String getPresentation(Object[] itemsToShow, int paramIdx) {
    ParameterInfoUIContextEx uiCtx =
      ParameterInfoComponent.createContext(itemsToShow, myFixture.getEditor(), myParameterInfoHandler, paramIdx);
    return GoParameterInfoHandler.updatePresentation(itemsToShow[0], uiCtx);
  }

  @NotNull
  @Override
  protected String getBasePath() {
    return "parameterInfo";
  }
}
