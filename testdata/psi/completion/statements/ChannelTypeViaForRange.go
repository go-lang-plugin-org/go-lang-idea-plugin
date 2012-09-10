package main


type T2 struct {
    x2, y2 int
}

func Foo(myMap chan T2) {
    for t := range myMap {
        t.<caret>
    }
}
/**---
x2
y2
