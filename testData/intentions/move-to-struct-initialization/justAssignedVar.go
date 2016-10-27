package main

type S struct {
	foo string
	bar string
}

func main() {
	var s S
	var str string
	s, str = S{}, "bar"
	s.foo<caret>, s.bar = "foo", str
	print(s.foo)
	print(str)
}