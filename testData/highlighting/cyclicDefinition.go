package main

var (
	Foo int = 1 + <error descr="Cyclic definition detected">Foo</error>
)

const (
	Bar, Demo int = 1 + <error descr="Cyclic definition detected">Bar</error> + 1 + <error descr="Cyclic definition detected">Bar</error>, 1
)

func _() {
	_, _, _ = Foo, Bar, Demo
}
