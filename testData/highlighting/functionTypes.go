package main

import "fmt"

type t struct {
}

func (t t) foo() {
}

func startConvert() (<warning descr="Unused named return parameter 'fin'">fin</warning>, <warning descr="Unused named return parameter 'fill'">fill</warning> t, <warning descr="Unused named return parameter 'e'">e</warning> error) {
	return t{}, t{}, nil
}

func main() {
	a1, b1, e := startConvert()
	a1.foo()
	b1.foo()
	fmt.Println(e)
}