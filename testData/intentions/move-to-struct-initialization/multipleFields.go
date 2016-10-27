package main

type S struct {
	foo string
	bar string
}

type B struct {
	x int
}

func main() {
	s, b := S{}, B{}
	s.foo, b.x, s.bar<caret>= "foo", 3, "bar"
	print(s.foo)
}