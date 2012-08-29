package main

type Foo struct {
	a int
}

func (obj *Foo) SetA(a int) {
	obj.a = a
}

func (obj *Foo) Test() {
	obj.<caret>
}
/**---
a
SetA
Test
