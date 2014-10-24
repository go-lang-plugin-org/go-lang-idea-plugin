package main

type T1 int

type T2 struct {
    a, b, c int
    /*def*/T1
}

func main() {
    var x = T2{}

    x./*ref*/T1
}
