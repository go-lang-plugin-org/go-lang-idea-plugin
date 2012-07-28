package main

type T int

func (t T) /*def*/F1() {
}

func (t T) F2() T {
}

func main() {
    var x T
    x.F2()./*ref*/F1()
}
