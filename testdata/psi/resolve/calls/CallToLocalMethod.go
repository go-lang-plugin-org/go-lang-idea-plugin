package main

type T int

func (t T) /*def*/Fyy() {
}

func main() {
    var x T
    x./*ref*/Fyy()
}
