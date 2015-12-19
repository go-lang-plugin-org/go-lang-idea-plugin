package main

import (
	"fmt"
	"reflect"
)

type b func(intParam int)
type a b

func c() {
	aFunc := generateFunc()
	if f, ok := aFunc.(a); ok {
		fmt.Println(reflect.TypeOf(f))
		f(<caret>)
	}
}

func generateFunc() interface{} {
	return a(func (intParam int) { })
}

func main() {
	c()
}
