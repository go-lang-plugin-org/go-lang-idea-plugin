package main

func Return1(a int) int {
    return a
}

func Return2() (int, int) {
    return 1, 2
}

func main() {
    /*begin*/Return1(1, 1)/*end.too many arguments in call to Return1*/
    /*begin*/Return1(Return2())/*end.too many arguments in call to Return1*/

    println(Return1(1), /*begin*/Return2()/*end.Multiple-value Return2() in single-value context*/)
}
