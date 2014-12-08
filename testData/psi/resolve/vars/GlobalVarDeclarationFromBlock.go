package main

var /*def*/x int = 10

func main() {
    if true {
        /*ref*/x = 100
    }
    println(x)
}
