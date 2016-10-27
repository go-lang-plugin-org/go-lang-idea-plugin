package main

type S struct {
	foo string
}

func main() {
	s, b := S{}, S{foo: "foo"}
	s.foo = "bar"
	print(s.foo)
	print(b.foo)
}