package main

var Foo1 = <error descr="Cyclic definition detected">Foo1</error>
const Bar1 = <error descr="Cyclic definition detected">Bar1</error>

var (
	Foo int = 1 + <error descr="Cyclic definition detected">Foo</error>
)

const (
	Bar, Demo int = 1 + <error descr="Cyclic definition detected">Bar</error> + 1 + <error descr="Cyclic definition detected">Bar</error>, 1
)

func _() {
	_, _, _ = Foo, Bar, Demo
}
