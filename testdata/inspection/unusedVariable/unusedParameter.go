package main

func Foo(a int, /*begin*/b/*end.Unused parameter 'b'*/, _ int) int {
    return a + 2
}

func main() {
    println(Foo(2, 3, 4))
}
