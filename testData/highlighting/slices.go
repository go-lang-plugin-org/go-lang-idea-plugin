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
    main2()
}

type Foo2 struct {
    Test int
}

func main2() {
    var a *[]Foo2
    (*a)[0].Test
    var b *[]Foo2
    b[0].<error>Test</error>

    test(a)
}

func test(a *[]Foo2) {
    fmt.Println((*a)[0].Test)

    for _, c := range *a {
        fmt.Println(c.Test)
    }
}