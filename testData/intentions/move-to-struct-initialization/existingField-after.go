package main

type S struct {
	foo string
	b string
}

func main() {
	s := S{b: "a", foo: "bar"}

	print(s.foo)
}