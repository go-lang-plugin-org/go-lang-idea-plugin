package main

type T1 *string
type T2 T1

func main() {
    var (x T2; y *string)
    /*type*/x
    /*type*/y
}
