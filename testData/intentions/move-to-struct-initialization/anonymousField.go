package main

type S struct {
	string
}

func main() {
	s := S{}
	_, s.string = "foo", "bar"<caret>
	print(s.string)
}