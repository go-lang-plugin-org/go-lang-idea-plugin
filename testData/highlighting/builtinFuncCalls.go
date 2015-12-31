package main

import "C"

func foo(interface{}) {}

type stringType string
type mapType map[string]string

func main() {
	foo(<error descr="Missing argument to make">make()</error>)
	foo(<error descr="\"notAType\" is not a type">make("notAType")</error>)
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

	type test uint32
	foo(make([]int, test(4)))
	foo(make([]int, uint64(2)))

	type c uint32
	type b c
	type a b
	type d []c
	type e d
	foo(make([]int32, a(2)))
	foo(make(e, a(4)))

	var xyz interface{} = 1
	foo(make(chan int, xyz.(int)))

	i := C.int(1)
	foo(make([]*C.char, i))
	
	foo(make(chan func(), 1.0))
	foo(make(chan func(), 1e5))
	foo(make(chan func(), '1'))
	foo(make(chan func(), <error descr="Non-integer size argument to make">true</error>))
	foo(make(chan func(), <error descr="Non-integer size argument to make">complex(17,4)</error>))
	foo(make(chan func(), <error descr="Non-integer size argument to make">"1"</error>))
}
