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
