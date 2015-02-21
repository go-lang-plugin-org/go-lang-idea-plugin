/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov, Mihai Toader
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

package com.goide.completion;

import com.intellij.testFramework.TreePrintCondition;
import com.intellij.util.containers.ContainerUtil;

import java.util.List;

public class GoCompletionTest extends GoCompletionTestBase {
  public void testLocalFunction() {
    doTestInclude("package foo; func foo() {}; func main() {<caret>}", "foo", "main");
  }

  public void testLocalType() {
    doTestInclude("package foo; type (T struct {}; T2 struct{}); func main(){var i <caret>}", "T", "T2");
  }

  public void testLocalVar() {
    doTestInclude("package foo; func main(){var i, j int; <caret>}", "i", "j");
  }

  public void testPackageLocalVar() {
    doTestInclude("package foo; var i, j int; func main(){<caret>}", "i", "j");
  }

  public void testLocalVarExclude() {
    doTestExclude("package foo; func main(){{var i, j int;}; <caret>}", "i", "j");
  }

  public void testParams() {
    doTestInclude("package foo; func main(i, j int){<caret>}", "i", "j");
  }

  public void testConsts() {
    doTestInclude("package foo; const i, j; func main(){<caret>}", "i", "j");
  }

  public void testNoCompletionInsideStrings() {
    doTestEquals("package foo; func main(){\"<caret>\"}");
  }

  public void testNoCompletionInsideComments() {
    doTestEquals("package foo; func main(){/*<caret>*/}");
  }

  public void testStructTypes() {
    doTestEquals("package foo; type AA struct {N AA}; func foo(a AA) {a.<caret>}", "N");
  }

  public void testStructTypes2() {
    doTestEquals("package foo; type AA struct {N AA}; func foo(a *AA) {a.<caret>}", "N");
  }

  public void testImports() {
    doCheckResult("package foo; import imp \"\"; func foo(a im<caret>) {}", "package foo; import imp \"\"; func foo(a imp.) {}");
  }

  public void testImportsForRefs() {
    doCheckResult("package foo; import imp \"\"; func foo() {im<caret>.foo}", "package foo; import imp \"\"; func foo() {imp.<caret>foo}");
  }

  public void testTopLevelKeywords() {
    myFixture.testCompletionVariants(getTestName(true) + ".go", "const", "func", "import", "type", "var");
  }

  public void testTopLevelKeywords_2() {
    myFixture.testCompletionVariants(getTestName(true) + ".go", "const", "func", "type", "var");
  }

  public void testBlockKeywords() {
    myFixture.testCompletionVariants(getTestName(true) + ".go", "for", "const", "var", "return", "if", "switch", "go", "defer", "select",
                                     "fallthrough", "goto", "main");
  }
  
  public void testBlockKeywordsInsideOneLineFunction() {
    myFixture.testCompletionVariants(getTestName(true) + ".go", "for", "const", "var", "return", "if", "switch", "go", "defer", "select",
                                     "fallthrough", "goto", "main");
  }

  public void testAddSpaceAfterKeyword() {
    doTestCompletion();
  }

  public void testTypeKeywords() {
    myFixture.testCompletionVariants(getTestName(true) + ".go", "struct", "interface", "chan", "map");
  }

  public void testTypeKeywordsInsideParentheses() {
    myFixture.testCompletionVariants(getTestName(true) + ".go", "chan", "map");
  }

  public void testSelectKeywordInsertHandler() {
    doTestCompletion();
  }

  public void testTypeKeywordInsertBraces() {
    doTestCompletion();
  }

  public void testTypeKeywordDoNotInsertBraces() {
    doTestCompletion();
  }

  public void testForStatementKeywords() {
    myFixture.testCompletionVariants(getTestName(true) + ".go", "bar", "break", "const", "continue", "defer", "for", "go", "if", "return",
                                     "fallthrough", "goto", "select", "switch", "var");
  }

  public void testChanKeyword() {
    doTestCompletion();
  }

  public void testChanKeyword_2() {
    doTestCompletion();
  }

  public void testChanKeyword_3() {
    doTestCompletion();
  }

  public void testMapKeyword() {
    doTestCompletion();
  }

  public void testMapKeyword_2() {
    doTestCompletion();
  }

  public void testMapKeywordInsertHandler() {
    doTestCompletion();
  }

  public void testMapKeywordInsertHandlerDoNotInsertBrackets() {
    doTestCompletion();
  }
  
  public void testElseKeyword() {
    doTestCompletion();
  }
  
  public void testElseKeywordRegression() {
    doTestEmptyCompletion();
  }
  
  public void testIfKeywordAfterElse() {
    doTestCompletion();
  }

  public void testForStatementKeywordsDoNotInsertSpace() {
    doTestCompletion();
  }

  public void testFunctionInDefer() {
    doTestCompletion();
  }

  public void testFunctionInGo() {
    doTestCompletion();
  }

  public void testPackageKeyword() {
    doTestCompletion();
  }
  
  public void testPackageKeywordInEmptyFile() {
    doTestCompletion();
  }

  public void testMethods() {
    doTestEquals("package foo; type T interface{}; func (t T) f() {t.<caret>}", "f");
  }

  public void testPreventStackOverflow() {
    doTestEquals("package foo; type E struct {*E}; func (e E) foo() {}; func main() {e := E{}; e.<caret>}", "foo", "E");
  }

  public void testNestedStructures() {
    doTestEquals("package foo; type E struct {}; type B struct {E}; func (e E) foo() {}; func (b B) bar(){}" +
                 "func main() {b := B{}; b.<caret>}", "foo", "bar", "E");
  }

  public void testNestedStructures2() {
    doTestEquals("package foo; type E struct {}; type B struct {E}; func (e E) foo() {}; " +
                 "func main() {b := B{}; b.E.<caret>}", "foo");
  }

  public void testNestedStructures3() {
    doTestEquals("package foo; type E struct {}; type B struct {E}; func (e E) foo() {}; " +
                 "func main() {B{}.E.<caret>}", "foo");
  }

  public void testInterfaceTypes() {
    doTestInclude("package foo; type E interface {}; type B interface {<caret>}", "E");
  }

  public void testReceiverCompletion() {
    doTestInclude("package foo; type E interface {}; func (e <caret>", "E");
  }

  public void testInterfaceTypesNoStruct() {
    doTestExclude("package foo; type E struct {}; type B interface {<caret>}", "E");
  }
  
  public void testOnlyTypesInParameters() {
    doTestExclude("package foo; const a int = 1; var b = 2; func main(<caret>) {}", "a", "b", "main");
  }

  public void testInnerTypes() {
    doTestInclude("package foo; func foo() {type innerType struct {}; var i <caret>}", "innerType");
  }

  public void testChannelType() {
    doTestInclude("package foo; func foo() {type T struct {cn *T}; var i T; i.<caret>}", "cn");
  }

  public void testInterfaceType() {
    doTestInclude("package foo; func foo(i interface {Boo() int}) {i.<caret>}", "Boo");
  }

  public void testInterfaceType2() {
    doTestInclude("package foo; type I interface {Boo() int}; func foo(i I) {i.<caret>}", "Boo");
  }

  public void testLabel() {
    doTestInclude("package foo; func main() { goto <caret>; Label1: 1}", "Label1");
  }
  
  public void testNoMainAnymore() {
    doTestEquals("package foo; func ma<caret> { }");
  }
  
  public void testNoChanOrMap() {
    doTestEquals("package foo; func ma(f int.<caret>) { }");
  }
  
  public void testOnTopLevel() {
    doTestEquals("package foo; func ma() { }\n<caret>", "var", "const", "func", "type");
  }

  public void testPackageBeforeDot() {
    doCheckResult("package foo; import imp \"\"; func foo(a im<caret>.SomeType) {}",
                  "package foo; import imp \"\"; func foo(a imp.<caret>SomeType) {}");
  }

  public void testNoUnderscore() {
    String theSame = "package foo; func foo() {_ := 1; <caret>}";
    doCheckResult(theSame, theSame);
  }

  public void testCaseInsensitiveVariable() {
    doCheckResult("package main; func test(BBA int){b<caret>}",
                  "package main; func test(BBA int){BBA<caret>}");
  }

  public void testRanges() {
    doTestInclude("package foo; func foo(a int) {for k := range <caret>}", "a");
  }

  public void testNoDuplicates() {
    doTestInclude("package foo; type a struct {<caret>", "a");
    List<String> stringList = myFixture.getLookupElementStrings();
    assertNotNull(stringList);
    assertSize(1, ContainerUtil.filter(stringList, new TreePrintCondition.Include("a")));
  }

  public void testTypeCastAsVar() throws Exception {
    doTestInclude("package main\n" +
                  "var fooVar int = 1\n" +
                  "func main() {\n" +
                  "    for _, v := range (*<caret>) {\n" +
                  "    }\n" +
                  "}", "fooVar");
  }

  public void testStructConstructions() throws Exception {
    doCheckResult("package main; func main() {WaitGr<caret>}; type WaitGroup struct {sema *uint32}", 
                  "package main; func main() {WaitGroup{<caret>}}; type WaitGroup struct {sema *uint32}");
  }

  public void testIntConversion() throws Exception {
    doCheckResult("package main; func main() {int<caret>}; type int int",
                  "package main; func main() {int(<caret>)}; type int int");
  }

  private void doTestCompletion() {
    myFixture.testCompletion(getTestName(true) + ".go", getTestName(true) + "_after.go");
  }
  
  private void doTestEmptyCompletion() {
    myFixture.testCompletionVariants(getTestName(true) + ".go");
  }
}
