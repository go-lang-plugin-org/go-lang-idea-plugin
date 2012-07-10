package main

import "math"

func pow(x, n, lim float64) float64 {
    if v := x; v < lim {
        return v
    }
    /*begin*/v/*end.Unresolved symbol: 'v'|CreateLocalVariableFix*/
    return lim
}

func f() {}

func main() {
    f(pow(2, 2, 10))
}
