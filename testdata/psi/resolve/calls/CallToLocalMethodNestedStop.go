package main

type T int

func (t T) F1() {
}

func (t T) F2() T {
}

func main() {
    var x T
    x.F2().F1()./*no ref*/F1()
}
