package main

import (
    "fmt"
    "io"
)

var (
    /*begin*/a/*end.go.global.variable*/, /*begin*/b/*end.go.global.variable*/ = 5, 6
)

const (
    /*begin*/CA/*end.go.const*/, /*begin*/CB/*end.go.const*/ = /*begin*/iota/*end.go.keyword*/, 5
)

type /*begin*/T/*end.go.type.name*/ struct {
    /*begin*/a/*end.go.variable*/ /*begin*/int/*end.go.type.name*/
    */*begin*/T/*end.go.type.name*/
}

type /*begin*/IT/*end.go.type.name*/ interface {
    /*begin*/io.Reader/*end.go.type.name*/
}

func Foo(/*begin*/a/*end.go.variable*/, /*begin*/b/*end.go.variable*/ /*begin*/int/*end.go.type.name*/, /*begin*/c/*end.go.variable*/ /*begin*/float64/*end.go.type.name*/) (/*begin*/m/*end.go.variable*/ /*begin*/int/*end.go.type.name*/, /*begin*/n/*end.go.variable*/ interface{}) {
    if /*begin*/a/*end.go.variable*/ < /*begin*/int/*end.go.keyword*/(/*begin*/c/*end.go.variable*/) {
        /*begin*/m/*end.go.variable*/ = 1
        /*begin*/n/*end.go.variable*/ = 2
        return
    }
    return /*begin*/a/*end.go.variable*/ + /*begin*/b/*end.go.variable*/, /*begin*/c/*end.go.variable*/
}

func main() {
    var /*begin*/k/*end.go.variable*/ /*begin*/bool/*end.go.type.name*/ = /*begin*/true/*end.go.keyword*/
    /*begin*/iota/*end.go.variable*/ := /*begin*/a/*end.go.global.variable*/
    /*begin*/println/*end.go.keyword*/("Hi", /*begin*/iota/*end.go.variable*/, /*begin*/b/*end.go.global.variable*/)
    /*begin*/println/*end.go.keyword*/(/*begin*/CA/*end.go.const*/, /*begin*/CB/*end.go.const*/)

    /*begin*/t/*end.go.variable*/ := /*begin*/T/*end.go.type.name*/{/*begin*/a/*end.go.variable*/: 5}
    Foo("%#v %#v\n", /*begin*/t/*end.go.variable*/, /*begin*/k/*end.go.variable*/)
}
