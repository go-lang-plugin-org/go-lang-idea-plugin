package main

type S struct {
	foo string
}

func main() {
	var s S
	var a, b string
	a, s, b = "a", S{}, "b"
	s.foo <caret>= "bar"
	print(s.foo)
	print(a)
	print(b)
}