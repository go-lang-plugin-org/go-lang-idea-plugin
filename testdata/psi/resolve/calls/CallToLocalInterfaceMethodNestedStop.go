package main

type T interface {
    Method1() T
    Method2()
}

func main() {
    var x T
    x.Method1().Method2()./*no ref*/Method2()
}
