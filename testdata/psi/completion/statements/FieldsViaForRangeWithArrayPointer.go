package main

type T struct {
    x, y int
}

func Foo(array *[10]T) {
    for _, t := range array {
        t.<caret>
    }
}
/**---
x
y
