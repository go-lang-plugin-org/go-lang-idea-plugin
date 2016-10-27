package main

type S struct {
	foo string
	bar string
}

func main() {
	s := S{foo: "oof"}
	s.foo, s.bar = "foo", "bar"<caret>
	print(s.foo)
}