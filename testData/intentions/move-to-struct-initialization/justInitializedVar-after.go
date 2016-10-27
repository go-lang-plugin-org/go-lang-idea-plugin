package main

type S struct {
	foo string
	bar string
}

func main() {
	s, str := S{foo: "foo"}, "bar"
	s.bar = str
	print(s.foo)
}