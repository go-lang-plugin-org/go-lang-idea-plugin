package main

import "fmt"
import "net/http"

func main() {
	b := 1
	b, a := 11, 1
	fmt.Println(b, a)
	c := simple(10)
	fmt.Println(c)
	Foo()
	Foo2()
        fmt.Println(http.ErrMissingFile)
}

func simple(a int) int {
	a, b := 1, 2
	return a + b
}

func Foo() {
    <error>err</error> := 1
    err = 2
}

func Foo2() {
    <error>err</error> := 1
    err,x := 2,1
    fmt.Println(x)
}

func _(p interface{}) error {
	switch <error>p</error> := p.(type) {
		case error:
		return nil
	}
	return nil
}