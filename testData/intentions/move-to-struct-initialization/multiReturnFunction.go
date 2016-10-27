package main

type S struct {
	foo int
	bar int
}

func int2() (int, int) {
	return 1, 2
}

func main() {
	s := S{}
	<caret>s.foo, s.bar= int2()
	print(s.foo)
}