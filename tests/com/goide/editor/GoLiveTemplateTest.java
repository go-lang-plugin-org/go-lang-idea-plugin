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

package com.goide.editor;

import com.goide.GoCodeInsightFixtureTestCase;

public class GoLiveTemplateTest extends GoCodeInsightFixtureTestCase {
  public void testErrOnStatementBeginning() {
    myFixture.configureByText("a.go", "package main; func main() { <caret> }");
    myFixture.type("err\t");
    myFixture.checkResult("package main; func main() {\n" +
                          "\tif err != nil {\n" +
                          "\t\t<caret>\n" +
                          "\t} }");
  }

  public void testErrAfterIf() {
    myFixture.configureByText("a.go", "package main; func main() { if <caret> }");
    myFixture.type("err\t");
    myFixture.checkResult("package main; func main() { if err\t<caret> }");
  }

  public void testJsonInTag() {
    myFixture.configureByText("a.go", "package main; type foo struct { MyFieldDeclaration int <caret> }");
    myFixture.type("json\t");
    myFixture.checkResult("package main; type foo struct { MyFieldDeclaration int `json:\"my_field_declaration\"` }");
  }

  public void testJsonInTagAfterComplexType() {
    myFixture.configureByText("a.go", "package main; type foo struct { a []int <caret> }");
    myFixture.type("json\t");
    myFixture.checkResult("package main; type foo struct { a []int `json:\"a\"` }");
  }

  public void testXmlInTagOfAnonymousField() {
    myFixture.configureByText("a.go", "package main; type foo struct { int <caret> }");
    myFixture.type("xml\t");
    myFixture.checkResult("package main; type foo struct { int `xml:\"<caret>\"` }");
  }

  public void testXmlInTagLiteral() {
    myFixture.configureByText("a.go", "package main; type foo struct { a int `<caret>` }");
    myFixture.type("xml\t");
    myFixture.checkResult("package main; type foo struct { a int `xml:\"a\"` }");
  }

  public void testJsonNotInTag() {
    myFixture.configureByText("a.go", "package main; func main() { <caret> }");
    myFixture.type("json\t");
    myFixture.checkResult("package main; func main() { json\t<caret> }");
  }

  public void testJsonNotInTag_2() {
    myFixture.configureByText("a.go", "package main; type foo struct { int \n<caret> }");
    myFixture.type("json\t");
    myFixture.checkResult("package main; type foo struct { int \njson\t<caret> }");
  }

  public void testForInSignature() {
    myFixture.configureByText("a.go", "package main; func <caret>");
    myFixture.type("for\t");
    myFixture.checkResult("package main; func for\t<caret>");
  }

  public void testForInBlock() {
    myFixture.configureByText("a.go", "package main; func main() { <caret> }");
    myFixture.type("for\t");
    myFixture.checkResult("package main; func main() {\n" +
                          "\tfor ; ;  {\n" +
                          "\t\t\n" +
                          "\t} }");
  }

  public void testVarDeclarationInBlock() {
    myFixture.configureByText("a.go", "package main; func main() { <caret> }");
    myFixture.type(":\t");
    myFixture.checkResult("package main; func main() {\n\tname :=  }");
  }

  public void testVarDeclarationInLabel() {
    myFixture.configureByText("a.go", "package main; func main() { name <caret> }");
    myFixture.type(":\t");
    myFixture.checkResult("package main; func main() { name :\t<caret> }");
  }

  public void testMainAfterPackageClause() {
    myFixture.configureByText("a.go", "package mai<caret>");
    myFixture.type("n\t");
    myFixture.checkResult("package main\t<caret>");
  }
}
