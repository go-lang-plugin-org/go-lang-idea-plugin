package main

type bar int

type foo struct {
	a bar
}

func (b bar) printValue() {
	println(b)
}

func main() {
	t := foo {
		a:4,
	}
	t.a.printValue()
	(-t.a).printValue()
}
