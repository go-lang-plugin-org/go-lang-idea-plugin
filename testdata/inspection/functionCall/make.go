package main

type T struct {
    a int
}

func main() {
    /*begin*/make()/*end.Make type must be a slice, map or channel*/
    /*begin*/make(int)/*end.Cannot make type int*/
    /*begin*/make(T)/*end.Cannot make type T*/

    make([]int, 5)
    make([]int, 5, 10)
    /*begin*/make([]int)/*end.Missing len argument to make([]int)*/
    make([]int, 1, 1, /*begin*/1, 1/*end.Too many arguments in call to make*/)

    make(chan int)
    make(chan int, 10)
    make(chan int, 10, /*begin*/10/*end.Too many arguments in call to make*/)

    make(map[int]string)
    make(map[int]string, 15)
    make(map[int]string, 15, /*begin*/10/*end.Too many arguments in call to make*/)
}
