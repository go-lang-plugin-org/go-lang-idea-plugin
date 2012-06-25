package main

func foo(a, /*begin*/b/*end.Unused parameter*/ int) int {
    var s [10]int
    var i int

    i = 1
    for i, s[i] = range [...]int{0, 1, 2, 3, 4} {
        println(i, s[i])
    }
    return a + 1
}

func main() {
    /*begin*/k/*end.Unused variable|RemoveVariableFix*/ := 5
    s := make([]int, 10)
    j := 3
    s[j] = 3
    println(true, false)
}