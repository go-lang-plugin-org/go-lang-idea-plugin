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
	s := S{foo: str2(), bar: "bar"}

	print(s.foo)
}