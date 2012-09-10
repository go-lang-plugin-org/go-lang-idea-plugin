package main

type T struct {
    x, y int
}

func Foo(array []T) {
    for _, t := range array {
        t.<caret>
    }
}
/**---
x
y
