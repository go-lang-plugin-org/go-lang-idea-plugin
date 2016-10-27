package main

type S struct {
	foo string
}

func main() {
	s := S{foo: "foo"}
	<caret>s.foo = "bar"
	print(s.foo)
}