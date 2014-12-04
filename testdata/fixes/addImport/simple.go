package main

func Foo() {
    println(/*begin*/p1/*end*/)
}
-----
package main

import "p1"

func Foo() {
    println(p1)
}