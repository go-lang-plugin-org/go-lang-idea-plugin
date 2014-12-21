package main

type T interface {
    Method1() T
    /*def*/Method2()
}

func main() {
    var x []T
    x[1].Method1()./*ref*/Method2()
}
