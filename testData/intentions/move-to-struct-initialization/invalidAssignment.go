package main

type S struct {
	foo string
	bar string
}

func str2() (string, string) {
	return "foo", "bar"
}

func main() {
	var a string
	s := S{}
	s.foo, s.bar<caret> = str2(), "bar"
	print(s.foo)
}