package main

type T struct {
    /*def*/a, b int
    c *T
}

var x = [1]T{ {/*ref*/a:1, b:2, c:nil} }
