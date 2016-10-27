package main

type S struct {
	bar, foo string
}

func main() {
	s := S{}
	s.foo <caret>= "bar"
	print(s.foo)
}