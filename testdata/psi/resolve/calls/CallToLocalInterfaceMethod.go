package main

type T interface {
    /*def*/Method1()
}

func main() {
    var x T
    x./*ref*/Method1()
}
