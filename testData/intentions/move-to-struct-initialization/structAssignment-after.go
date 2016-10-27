package main

type S struct {
	foo string
}

func main() {
	var s S
	s = S{foo: "bar"}

	print(s.foo)
}