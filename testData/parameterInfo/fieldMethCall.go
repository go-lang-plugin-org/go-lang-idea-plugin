package main

type Outer struct {
    inner Inner
}

type Inner struct {}

func (i* Inner) f(a, b int) {}

func (o* Outer) Test() {
    o.inner.f(<caret>)
}
