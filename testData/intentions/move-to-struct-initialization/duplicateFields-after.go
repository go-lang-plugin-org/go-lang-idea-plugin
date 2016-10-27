package main

type S struct {
	foo string
	bar string
}

func main() {
	s := S{bar: "bar"}
	s.foo, s.foo = "foo", "bar1"
	print(s.foo)
}