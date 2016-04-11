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

package com.goide.completion;

import com.intellij.codeInsight.completion.CompletionType;

public class GoTestNameCompletionTest extends GoCompletionTestBase {
  public void testShowFromSimilarFileFirst() {
    myFixture.addFileToProject("foo.go", "package main; func FooPublic() {}");
    myFixture.addFileToProject("a.go", "package main; func BarPublic() {}");
    doTestVariants("package main; func <caret>", CompletionType.BASIC, 1, CheckType.ORDERED_EQUALS, "BenchmarkBarPublic",
                   "ExampleBarPublic", "TestBarPublic", "BenchmarkFooPublic", "ExampleFooPublic", "TestFooPublic");
  }

  public void testForFunctions() {
    myFixture.addFileToProject("foo.go", "package main; func Public() {}; func private() {}; func _() {}");
    doTestEquals("package main; func <caret>", "BenchmarkPublic", "ExamplePublic", "TestPublic");
  }

  public void testForMethods() {
    myFixture.addFileToProject("foo.go",
                               "package main; type Foo struct {}; func (Foo) Public() {}; func (Foo) private() {}; func (Foo) _() {}");
    doTestEquals("package main; func <caret>", "BenchmarkFoo_Public", "ExampleFoo_Public", "TestFoo_Public");
  }

  public void testAlreadyImplementTests() {
    myFixture.addFileToProject("foo.go", "package main; func Public() {}; func private() {}; func _() {}");
    myFixture.addFileToProject("foo_test.go", "package main; func TestPublic() {}; func ExampleFunc() {}");
    doTestEquals("package main; func <caret>", "BenchmarkPublic", "ExamplePublic", "TestPublic2");
  }

  public void testWithoutSignature() {
    myFixture.addFileToProject("foo.go", "package main; func Public() {}; func private() {}; func _() {}");
    doCheckResult("package main; func TestP<caret>", "package main;\n" +
                                                     "\n" +
                                                     "import \"testing\"\n" +
                                                     "\n" +
                                                     "func TestPublic(t *testing.T) {\n" +
                                                     "\t<caret>\n" +
                                                     "}");
  }

  public void testWithoutSignatureWithBlock() {
    myFixture.addFileToProject("foo.go", "package main; func Public() {}; func private() {}; func _() {}");
    doCheckResult("package main; func TestP<caret>{}", "package main;\n" +
                                                       "\n" +
                                                       "import \"testing\"\n" +
                                                       "\n" +
                                                       "func TestPublic(t *testing.T) {\n" +
                                                       "\t<caret>\n" +
                                                       "}");
  }

  public void testWithInvalidSignatureWithoutParameters() {
    myFixture.addFileToProject("foo.go", "package main; func Public() {}; func private() {}; func _() {}");
    doCheckResult("package main; func TestP<caret>(){}", "package main;\n" +
                                                         "\n" +
                                                         "import \"testing\"\n" +
                                                         "\n" +
                                                         "func TestPublic(t *testing.T) {\n" +
                                                         "\t<caret>\n" +
                                                         "}");
  }

  public void testWithInvalidSignature() {
    myFixture.addFileToProject("foo.go", "package main; func Public() {}; func private() {}; func _() {}");
    doCheckResult("package main; func TestP<caret>(foo Bar){}", "package main;\n" +
                                                                "\n" +
                                                                "import \"testing\"\n" +
                                                                "\n" +
                                                                "func TestPublic(foo Bar) {\n" +
                                                                "\t<caret>\n" +
                                                                "}");
  }

  public void testWithoutBody() {
    myFixture.addFileToProject("foo.go", "package main; func Public() {}; func private() {}; func _() {}");
    doCheckResult("package main; func TestP<caret>()", "package main;\n" +
                                                       "\n" +
                                                       "import \"testing\"\n" +
                                                       "\n" +
                                                       "func TestPublic(t *testing.T) {\n" +
                                                       "\t<caret>\n" +
                                                       "}");
  }

  public void testWithIncompleteBody() {
    myFixture.addFileToProject("foo.go", "package main; func Public() {}; func private() {}; func _() {}");
    doCheckResult("package main; func TestP<caret>() {", "package main;\n" +
                                                         "\n" +
                                                         "import \"testing\"\n" +
                                                         "\n" +
                                                         "func TestPublic(t *testing.T) {\n" +
                                                         "\t<caret>\n" +
                                                         "}");
  }

  public void testWithEmptyBody() {
    myFixture.addFileToProject("foo.go", "package main; func Public() {}; func private() {}; func _() {}");
    doCheckResult("package main; func TestP<caret>(){}", "package main;\n" +
                                                         "\n" +
                                                         "import \"testing\"\n" +
                                                         "\n" +
                                                         "func TestPublic(t *testing.T) {\n" +
                                                         "\t<caret>\n" +
                                                         "}");
  }

  public void testWithoutStatements() {
    myFixture.addFileToProject("foo.go", "package main; func Public() {}; func private() {}; func _() {}");
    doCheckResult("package main; func TestP<caret>() {\n}", "package main;\n" +
                                                            "\n" +
                                                            "import \"testing\"\n" +
                                                            "\n" +
                                                            "func TestPublic(t *testing.T) {\n" +
                                                            "\t<caret>\n" +
                                                            "}");
  }

  public void testWithStatements() {
    myFixture.addFileToProject("foo.go", "package main; func Public() {}; func private() {}; func _() {}");
    doCheckResult("package main; func TestP<caret>() { println(); println(); }",
                  "package main;\n" +
                  "\n" +
                  "import \"testing\"\n" +
                  "\n" +
                  "func TestPublic(t *testing.T) { <caret>println(); println(); }");
  }

  public void testCrossTestPackage() {
    myFixture.addFileToProject("foo.go", "package foo; func Public() {}; func private() {}; func _() {}");
    doCheckResult("package foo_test; func TestP<caret>() { }", "package foo_test;\n\n" +
                                                               "import \"testing\"\n\n" +
                                                               "func TestPublic(t *testing.T) {\n" +
                                                               "\t\n" +
                                                               "}");
  }

  public void testDoNotSuggestDuplicatedFunction() {
    myFixture.addFileToProject("foo.go", "package foo\n\nfunc Public() {} func private() {}; func _() {}");
    doCheckResult("package foo; func TestPublic() {}\n\nfunc TestP<caret>() { }", "package foo;\n\n" +
                                                                                  "import \"testing\"\n\n" +
                                                                                  "func TestPublic() {}\n\n" +
                                                                                  "func TestPublic2(t *testing.T) {\n" +
                                                                                  "\t\n" +
                                                                                  "}");
  }

  public void testDoNotSuggestDuplicatedFunctionForTestPackages() {
    myFixture.addFileToProject("foo.go", "package foo\n\nfunc Public() {} func private() {}; func _() {}");
    doCheckResult("package foo_test; func TestPublic() {}\n\nfunc TestP<caret>() { }", "package foo_test;\n\n" +
                                                                                       "import \"testing\"\n\n" +
                                                                                       "func TestPublic() {}\n\n" +
                                                                                       "func TestPublic2(t *testing.T) {\n" +
                                                                                       "\t\n" +
                                                                                       "}");
  }

  @Override
  protected String getDefaultFileName() {
    return "a_test.go";
  }
}
