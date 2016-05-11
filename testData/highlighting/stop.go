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
    <error descr="x.Method1().Method2() used as value">x.Method1().Method2()</error>.<error descr="Unresolved reference 'Method2'">Method2</error>()
    var x1 T2
    <error descr="x1.F2().F1() used as value">x1.F2().F1()</error>.<error descr="Unresolved reference 'F1'">F1</error>()
}

