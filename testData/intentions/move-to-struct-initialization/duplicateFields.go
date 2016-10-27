package main

type S struct {
	foo string
	bar string
}

func main() {
	s := S{}
	s.foo, s.foo<caret>, s.bar= "foo", "bar1", "bar"
	print(s.foo)
}