package main

type T1 string
type T2 *T1
type T3 *T2
type T4 **T1

func main() {
    var (x T4; y T3; z **string)
    /*type*/x
    /*type*/y
    /*type*/z
}
