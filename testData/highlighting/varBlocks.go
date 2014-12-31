package main

import "fmt"

func main() {
    fmt.Println(test())
    fmt.Println(test2())
}

func foo() int {
    return 1
}

func test() func() int {
    fff := foo
    return func() int {
        r := fff()
        return r
    }
}

func test2() func () int {
    f := foo
    return func() int {
        r := 1
        r = f()
        return r
    }
}