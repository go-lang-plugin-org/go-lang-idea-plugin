package main

type S struct {
	foo string
}

func main() {
	var s S
	s = S{}
	s.foo <caret>= "bar"
	print(s.foo)
}