package main

type T interface {
    Method1() T
    Method2()
}

type T2 int

func (t T2) F1() {
}

func (t T2) F2() T2 {
    return nil
}

func main() {
    var x T
    x.Method1().Method2().<error>Method2</error>()
    var x1 T2
    x1.F2().F1().<error>F1</error>()
}

