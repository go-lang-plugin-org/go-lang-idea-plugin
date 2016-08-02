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

package com.goide;

import com.intellij.codeInsight.documentation.DocumentationManager;
import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

@SdkAware
public class GoDocumentationProviderTest extends GoCodeInsightFixtureTestCase {

  public void testPrintln()                           { doTest(); }
  public void testFprintln()                          { doTest(); }
  public void testMethod()                            { doTest(); }
  public void testVariable()                          { doTest(); }
  public void testVariableInSwitch()                  { doTest(); }
  public void testEscape()                            { doTest(); }
  public void testEscapeReturnValues()                { doTest(); }
  public void testPackageWithDoc()                    { doTest(); }
  public void testPackage()                           { doTest(); }
  public void testPackageOnQualifier()                { doTest(); }
  public void testPackageOnImportAlias()              { doTest(); }
  public void testTypeResultDefinition()              { doTest(); }
  public void testMultilineTypeListDefinition()       { doTest(); }
  public void testMultilineVariable_1()               { doTest(); }
  public void testMultilineVariable_2()               { doTest(); }
  public void testMultilineVariable_3()               { doTest(); }
  public void testMultilineVariable_4()               { doTest(); }
  public void testMultilineVariable_5()               { doTest(); }
  public void testFieldDeclaration()                  { doTest(); }
  public void testSignature()                         { doTest(); }
  public void testStruct()                            { doTest(); }
  public void testTypeSpec()                          { doTest(); }
  public void testTypeTopDefinition()                 { doTest(); }
  public void testTypeInnerDefinitionWithoutComment() { doTest(); }
  public void testConstants()                         { doTest(); }
  public void testVarShortDefinition()                { doTest(); }
  public void testSpecType()                          { doTest(); }
  public void testPointer()                           { doTest(); }
  public void testFunctionType()                      { doTest(); }
  public void testParameter()                         { doTest(); }
  public void testResultParameter()                   { doTest(); }
  public void testReceiver()                          { doTest(); }
  public void testStructWithAnon()                    { doTest(); }

  public void testMultiBlockDoc()                     { doConverterTest(); }
  public void testIndentedBlock()                     { doConverterTest(); }
  public void testCommentEndsWithIndentedBlock()      { doConverterTest(); }
  public void testQuotedStrings()                     { doConverterTest(); }
  public void testLinks()                             { doConverterTest(); }

  @NotNull
  @Override
  protected String getBasePath() {
    return "doc";
  }

  private void doConverterTest() {
    try {
      List<String> lines = FileUtil.loadLines(getTestDataPath() + "/" + getTestName(true) + "_source.txt");
      assertSameLinesWithFile(getTestDataPath() + "/" + getTestName(true) + "_after.txt", new GoCommentsConverter().textToHtml(lines));
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void doTest() {
    myFixture.configureByFile(getTestName(true) + ".go");
    Editor editor = myFixture.getEditor();
    PsiFile file = myFixture.getFile();
    PsiElement originalElement = file.findElementAt(editor.getCaretModel().getOffset());
    assertNotNull(originalElement);

    PsiElement docElement = DocumentationManager.getInstance(getProject()).findTargetElement(editor, file);
    DocumentationProvider documentationProvider = DocumentationManager.getProviderFromElement(originalElement);
    String actualDoc = StringUtil.notNullize(documentationProvider.generateDoc(docElement, originalElement));
    
    String quickNavigateInfo = documentationProvider.getQuickNavigateInfo(docElement, originalElement);
    actualDoc += "\n=====\n" + StringUtil.notNullize(quickNavigateInfo, "No navigation info");
    
    List<String> urls = documentationProvider.getUrlFor(docElement, originalElement);
    actualDoc += "\n=====\n" + (urls != null ? StringUtil.join(urls, "\n") : "No urls");
    
    String localUrl = GoDocumentationProvider.getLocalUrlToElement(docElement);
    actualDoc += "\n=====\n" + StringUtil.notNullize(localUrl, "No local urls");
    assertSameLinesWithFile(getTestDataPath() + "/" + getTestName(true) + ".txt", actualDoc);
    
    if (localUrl != null) {
      PsiElement elementForLink = documentationProvider.getDocumentationElementForLink(myFixture.getPsiManager(), localUrl, originalElement);
      assertNotNull("Link '" + localUrl + "' wasn't resolved", elementForLink);
      assertEquals("Link was resolved to unexpected element.\nExpected: " + docElement.getText() + "\n" +
                   "Actual: " + elementForLink.getText(), docElement, elementForLink);
    }
  }
}
