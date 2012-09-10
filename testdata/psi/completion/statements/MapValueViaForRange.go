package main

type T1 struct {
    x1, y1 int
}

type T2 struct {
    x2, y2 int
}

func Foo(myMap map[T1]T2) {
    for key, val := range myMap {
        val.<caret>
    }
}
/**---
x2
y2
