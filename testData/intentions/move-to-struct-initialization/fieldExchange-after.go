package main

type S struct {
	foo string
}

func main() {
	var b, s S
	s, b.foo = S{foo: "bar"}, "foo"

	print(s.foo)
}