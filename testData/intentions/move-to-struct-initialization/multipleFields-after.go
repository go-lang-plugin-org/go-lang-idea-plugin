package main

type S struct {
	foo string
	bar string
}

type B struct {
	x int
}

func main() {
	s, b := S{foo: "foo", bar: "bar"}, B{}
	b.x = 3
	print(s.foo)
}