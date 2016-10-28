package main

type S struct {
	foo string
}

func main() {
	s := S{foo: "bar"}

	print(s.foo)
}