package main

type S struct {
	foo string
	bar string
}

func main() {
	s := S{foo: "oof", bar: "bar"}
	s.foo = "foo"
	print(s.foo)
}