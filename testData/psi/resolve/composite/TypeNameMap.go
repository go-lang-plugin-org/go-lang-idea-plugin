package main

type T struct {
    /*def*/a, b int
    c *T
}

var x = map[string]T{ "1":{/*ref*/a:1, b:2, c:nil} }
