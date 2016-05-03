package main

type MyType struct {
	Err   string
}

type t MyType
type d t

func (a t) foo(){}
func (a d) too(){}

func main()  {
	e := MyType{
		Err: "hello",
	}
	out := d(e)
	println(out.Err)
}

type a int
func (a) foo() {}
type b int
func (b) bar() {}

type c struct {
	a
	b
}

func _() {
	c := new(c)
	println(c.foo())
	println(c.bar())
}