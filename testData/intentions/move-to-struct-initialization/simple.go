package main

type S struct {
	foo string
}

func main() {
	s := S{}
	s.foo <caret>= "bar"
	print(s.foo)
}