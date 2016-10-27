package main

type S struct {
	foo string
}

func main() {
	s := S{}
	<caret>s.x = "bar"
	print(s.foo)
}