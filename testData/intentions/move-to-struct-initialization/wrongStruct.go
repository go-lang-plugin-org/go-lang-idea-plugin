package main

type S struct {
	foo string
}

type B struct {
	bar string
}

func main() {
	var s S
	b := B{}
	s.foo <caret>= "bar"
	print(b.foo)
}