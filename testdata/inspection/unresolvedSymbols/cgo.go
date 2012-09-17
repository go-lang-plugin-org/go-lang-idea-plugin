package main

import "C"

type Test struct {
    a C.SomeType
}

func main() {
    // Don't report error for package "C". It's for Cgo.
    println(C.bar)
    println(C.foo())
}
