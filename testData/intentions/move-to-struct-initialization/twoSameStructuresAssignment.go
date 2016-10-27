package main

type S struct {
	foo string
}

func main() {
	var s, b S
	s, b = S{}, S{}
	s.foo, b.foo<caret> = "bar", "foo"
	print(s.foo)
	print(b.foo)
}