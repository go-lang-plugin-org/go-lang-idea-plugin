package main

type S struct {
	foo string
	bar string
}

func main() {
	var a int
	s := S{foo: "foo", bar: "bar"}
	a = 3
	print(s.foo)
	print(a)
}