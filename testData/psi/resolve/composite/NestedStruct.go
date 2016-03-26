package main

type T struct {
    /*def*/a, b int
}

type T2 struct {
    a, b int
    c T
}

var x = T2{ c:T{/*ref*/a:1, b:2} }


