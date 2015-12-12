package main

func main() {
	client := &MyType{}
	_, err := client.Foo()
	println(er<caret>r)
}

type MyType struct {
	MyField int
}

func (c *MyType) Foo() (resp *MyType, err error) {
	return c, error("error")
}
