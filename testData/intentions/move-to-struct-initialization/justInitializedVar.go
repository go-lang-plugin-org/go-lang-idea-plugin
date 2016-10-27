package main

type S struct {
	foo string
	bar string
}

func main() {
	s, str := S{}, "bar"
	s.foo<caret>, s.bar = "foo", str
	print(s.foo)
}