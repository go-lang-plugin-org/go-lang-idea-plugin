package main

func main() {
	client := &MyType{}
	_, err := cl<caret>ient.Foo()
	println(err)
}

type MyType struct {
	MyField int
}

func (c *MyType) Foo() (resp *MyType, err error) {
	return c, error("error")
}
