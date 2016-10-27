package main

type S struct {
	foo string
}

func main() {
	var b, a string
	s := S{}
	a, b, <caret>s.foo = "a", "b", "bar"
	print(s.foo)
	print(b)
	print(a)
}