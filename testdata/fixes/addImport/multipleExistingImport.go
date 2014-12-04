package main

import "fmt"
import "strings"

func Foo() {
    println(/*begin*/p1/*end*/)
}
-----
package main

import "fmt"
import (
	"strings"
	"p1"
)

func Foo() {
    println(p1)
}