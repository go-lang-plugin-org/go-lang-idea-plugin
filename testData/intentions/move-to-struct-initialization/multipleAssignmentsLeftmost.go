package main

type S struct {
	foo string
}

func main() {
	var b, a string
	s := S{}
	<caret>s.foo, b, a = "bar", "b", "a"
	print(s.foo)
	print(b)
	print(a)
}