package main

type One struct {}

func (m *One) Method() {}

type Two struct {
    One
}

type Three struct {
    One
}

func (m *Three) /*def*/Method() {}

func main() {
    one := One{}
    one.Method()
    two := Two{}
    two.Method()
    three := Three{}
    three./*ref*/Method()
}