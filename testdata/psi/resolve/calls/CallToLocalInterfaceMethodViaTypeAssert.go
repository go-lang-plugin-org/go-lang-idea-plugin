package main

type T interface {
    Method1() T
    /*def*/Method2()
}

func main() {
    x.(T).Method1()./*ref*/Method2()
}
