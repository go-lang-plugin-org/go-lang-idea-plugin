package main

type S struct {
	string
}

func main() {
	s := S{string: "bar"}
	(_) = "foo"
	print(s.string)
}