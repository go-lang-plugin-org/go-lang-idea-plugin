package main

type S struct {
	foo string
}

func main() {
	var s S
	var a, b string
	a, s, b = "a", S{foo: "bar"}, "b"

	print(s.foo)
	print(a)
	print(b)
}