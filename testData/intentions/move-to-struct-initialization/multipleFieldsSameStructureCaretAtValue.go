package main

type S struct {
	foo string
	bar string
}

func main() {
	var a int
	s := S{}
	s.foo, a, s.bar = "foo", 3, "bar"<caret>
	print(s.foo)
	print(a)
}