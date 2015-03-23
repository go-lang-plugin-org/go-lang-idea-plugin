package main

type A struct {
    b int
    c int
}

func NewA(/*def*/b int) *A {
    return &A{
        b: /*ref*/b,
        c: 1,
    }
}