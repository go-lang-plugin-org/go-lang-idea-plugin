package main

func Foo() int {
    return 5
}

func main() {
    /*def*/f := Foo
    println(Foo(), /*ref*/f())
}
