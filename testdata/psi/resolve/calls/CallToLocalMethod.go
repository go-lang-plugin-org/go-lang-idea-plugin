package main

type T int

func (t T) /*def*/F() {
}

func main() {
    var x T
    x./*ref*/F()
}
