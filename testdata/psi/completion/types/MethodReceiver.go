package main

type Test1 int
type Test2 int
type Test3 Test4
type Test4 interface {}
type Test5 *int

func (a T<caret>) foo() {
}
/**---
Test1
Test2
