package main

type A struct {
    /*def*/b int
    c int
}

func NewA(b int) *A {
    return &A{
        /*ref*/b: b,
        c: 1,
    }
}