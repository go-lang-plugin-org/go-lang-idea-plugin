package main

import "fmt"

func Foo() {
    println(/*begin*/p1/*end*/)
}
-----
package main

import (
	"fmt"
	"p1"
)

func Foo() {
    println(p1)
}