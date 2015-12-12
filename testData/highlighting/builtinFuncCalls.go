package main

func foo(interface{}) {}

type stringType string
type mapType map[string]string

func main() {
	foo(make(<error descr="Cannot make stringType">stringType</error>))
	foo(<error descr="Missing len argument to make">make([]stringType)</error>)
	foo(make([]stringType, 2))
	foo(make([]stringType, 2, 3))
	foo(<error descr="Too many arguments to make">make([]stringType, 2, 3, 4)</error>)

	x := "a"
	y := 4
	foo(make([]stringType, <error descr="Non-integer size argument to make">x</error>))
	foo(make([]stringType, 4, <error descr="Non-integer capacity argument to make">"foo"</error>))

	foo(make(<-chan int))
	foo(make(chan int))
	foo(make(chan int, 4))
	foo(<error descr="Too many arguments to make">make(<-chan int, 4, 5)</error>)

	foo(make(map[string]string))
	foo(make(map[string]string, y))
}
