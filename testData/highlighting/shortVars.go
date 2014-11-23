package main

import "fmt"

func main() {
    fmt.Println(test())
}

func f1() int {return 1}
func f2() (int, int) {return 1, 2}

func test() int {
    x := f1()
    y, z := f2()

    <error>x</error> := f1() // Should be error: "no new variables on left side of :="
    <error>y,   z</error>     := f2() // Should be error: "no new variables on left side of :="

    x, a := f2() // Ok: `x` is reused and `a` is new
    b, x := f2() // Ok: `b` is new and `x` is reused

    return x + y + z + a + b // Just to avoid unused variable error
}

