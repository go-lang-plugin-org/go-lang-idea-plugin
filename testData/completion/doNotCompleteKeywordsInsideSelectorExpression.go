package main

type demo struct{}

func (d demo) FuncA() demo {
    return d
}

func (d demo) FuncB() demo {
    return d
}

func main() {
    a := demo{}
    a.FuncA().<caret>.FuncB()
}