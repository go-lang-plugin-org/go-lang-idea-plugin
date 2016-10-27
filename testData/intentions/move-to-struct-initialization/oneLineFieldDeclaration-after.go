package main

type S struct {
	bar, foo string
}

func main() {
	s := S{foo: "bar"}

	print(s.foo)
}