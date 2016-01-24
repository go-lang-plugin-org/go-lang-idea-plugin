package main

import "fmt"

func main() {
    c := Parent{&Child{}, &impl{}}
    c.Get(1,1,1) // too many arguments in call to c.Get
}

type Parent struct {
    *Child
    Interface
}
type Child struct { Unimplemented }
type Interface interface { Get(...int) }
type impl struct { }
func (r impl) Get(...int) { fmt.Println("HERE") }
type Unimplemented interface { Get(s string) }