package main

type T int

func (t T) /*def*/F1() {
}

func (t T) F2() T {
}

func _(x map[int]T) {
    x[1].F2()./*ref*/F1()
}

func main() {

}
