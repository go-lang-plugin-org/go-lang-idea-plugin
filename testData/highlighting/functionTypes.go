package main

import "fmt"

type t struct {
}

func (t t) foo() {
}

func startConvert() (fin, fill t, e error) {
	return t{}, t{}, nil
}

func main() {
	a1, b1, e := startConvert()
	a1.foo()
	b1.foo()
	fmt.Println(e)
}