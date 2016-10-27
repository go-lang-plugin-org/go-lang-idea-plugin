package main

type S struct {
	foo string
}

func main() {
	var b, a string
	s := S{foo: "bar"}
	a, b = "a", "b"
	print(s.foo)
	print(b)
	print(a)
}