package main

type T16 int
type T15 T16

// we should handle recursive definition correctly
type T11 T10
type T10 T11

type T7 *chan string
type T6 T7
type T5 T6

type T3 chan string
type T2 T3
type T1 T2

type T struct {
    a int
}

func main() {
    /*begin*/make()/*end.Make type must be a slice, map or channel*/
    /*begin*/make(int)/*end.Cannot make type int*/
    /*begin*/make(T)/*end.Cannot make type T*/

    // final type is a chan string
    make(T1)

    // final type is a pointer
    /*begin*/make(T5)/*end.Cannot make type T5*/

    // recursive definition
    /*begin*/make(T10)/*end.Cannot make type T10*/

    // final type is a primitive
    /*begin*/make(T15)/*end.Cannot make type T15*/

    make([]int, 5)
    make([]int, 5, 10)
    /*begin*/make([]int)/*end.Missing len argument to make([]int)*/
    make([]int, 1, 1, /*begin*/1, 1/*end.too many arguments in call to make*/)

    make(chan int)
    make(chan int, 10)
    make(chan int, 10, /*begin*/10/*end.too many arguments in call to make*/)

    make(map[int]string)
    make(map[int]string, 15)
    make(map[int]string, 15, /*begin*/10/*end.too many arguments in call to make*/)
}
