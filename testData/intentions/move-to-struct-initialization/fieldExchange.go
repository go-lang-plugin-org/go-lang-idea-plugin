package main

type S struct {
	foo string
}

func main() {
	var b, s S
	s, b.foo = S{}, "foo"
	s.foo <caret>= "bar"
	print(s.foo)
}