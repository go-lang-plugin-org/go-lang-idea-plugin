package main

import "fmt"

type Foo struct {
    F int
}

func bar() int {
    slice0 := make([]Foo, 10)
    slice1 := slice0[1:1:1]
    slice1[0].F = 1
    return slice1[0].F
}

func main() {
    fmt.Println(bar())
}