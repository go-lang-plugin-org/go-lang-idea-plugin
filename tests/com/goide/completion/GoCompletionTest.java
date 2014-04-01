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
  
    public void testStructTypes() throws Exception {
      doTestEquals("package foo; type AA struct {N AA}; func foo(a AA) {a.<caret>}", "N");
    }
  
    public void testStructTypes2() throws Exception {
      doTestEquals("package foo; type AA struct {N AA}; func foo(a *AA) {a.<caret>}", "N");
    }
  
    public void testImports() throws Exception {
      doCheckResult("package foo; import imp \"\"; func foo(a im<caret>) {}", "package foo; import imp \"\"; func foo(a imp.) {}");
    }
  
    public void testKeywords() {
      myFixture.testCompletionVariants(getTestName(true) + ".go", "const", "continue");
    }
  
    public void testMethods() throws Exception {
      doTestEquals("package foo; type T interface{}; func (t T) f() {t.<caret>}", "f");
    }
  
    public void testPreventStackOverflow() throws Exception {
      doTestEquals("package foo; type E struct {*E}; func (e E) foo() {}; func main() {e := E{}; e.<caret>}", "foo", "E");
    }
  
    public void testNestedStructures() throws Exception {
      doTestEquals("package foo; type E struct {}; type B struct {E}; func (e E) foo() {}; func (b B) bar(){}" +
                   "func main() {b := B{}; b.<caret>}", "foo", "bar", "E");
    }
  
    public void testNestedStructures2() throws Exception {
      doTestEquals("package foo; type E struct {}; type B struct {E}; func (e E) foo() {}; " +
                   "func main() {b := B{}; b.E.<caret>}", "foo");
    }
  
    public void testNestedStructures3() throws Exception {
      doTestEquals("package foo; type E struct {}; type B struct {E}; func (e E) foo() {}; " +
                   "func main() {B{}.E.<caret>}", "foo");
    }
  
    public void testInterfaceTypes() throws Exception {
      doTestInclude("package foo; type E interface {}; type B interface {<caret>}", "E");
    }
  
    public void testReceiverCompletion() throws Exception {
      doTestInclude("package foo; type E interface {}; func (e <caret>", "E");
    }
  
    public void testInterfaceTypesNoStruct() throws Exception {
      doTestExclude("package foo; type E struct {}; type B interface {<caret>}", "E");
    }
  
    public void testInnerTypes() throws Exception {
      doTestInclude("package foo; func foo() {type innerType struct {}; var i <caret>}", "innerType");
    }
  
    public void testChannelType() throws Exception {
      doTestInclude("package foo; func foo() {type T struct {cn *T}; var i T; i.<caret>}", "cn");
    }
  
    public void testInterfaceType() throws Exception {
      doTestInclude("package foo; func foo(i interface {Boo() int}) {i.<caret>}", "Boo");
    }
  
    public void testInterfaceType2() throws Exception {
      doTestInclude("package foo; type I interface {Boo() int}; func foo(i I) {i.<caret>}", "Boo");
    }
  
    public void testLabel() throws Exception {
      doTestInclude("package foo; func main() { goto <caret>; Label1: 1}", "Label1");
    }
  
    public void testPackageBeforeDot() throws Exception {
      doCheckResult("package foo; import imp \"\"; func foo(a im<caret>.SomeType) {}", "package foo; import imp \"\"; func foo(a imp.<caret>SomeType) {}");
    }
  
    public void testNoDuplicates() throws Exception {
      doTestInclude("package foo; type a struct {<caret>", "a");
      List<String> stringList = myFixture.getLookupElementStrings();
      assertNotNull(stringList);
      assertSize(1, ContainerUtil.filter(stringList, new TreePrintCondition.Include("a")));
    }
  
}
