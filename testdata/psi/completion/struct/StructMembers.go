package main

type S struct {
	a int
	b, c float32
	e, f *S
}

func main() {
	var x S
	x.<caret>
}
/**---
a
b
c
e
f
