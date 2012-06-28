package main

type T1 []string
type T2 T1


var ( x T1; y T1; x_ T2; z []string )

func main() {
    /*type*/y
    /*type*/z
    /*type*/x_
    /*type*/x + y
    /*type*/x - y
    /*type*/x * y
    /*type*/x / y
    /*type*/x % y
}
