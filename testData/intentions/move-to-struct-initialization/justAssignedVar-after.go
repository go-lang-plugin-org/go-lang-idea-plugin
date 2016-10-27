package main

type S struct {
	foo string
	bar string
}

func main() {
	var s S
	var str string
	s, str = S{foo: "foo"}, "bar"
	s.bar = str
	print(s.foo)
	print(str)
}