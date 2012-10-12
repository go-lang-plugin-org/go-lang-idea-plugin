package main

func foo() {
    /*def*/t := 5
    println(/*ref*/t)
    if /*ref*/t > 2 {
        println(/*ref*/t + 1)
    } else {
        t := 3
        println(t)
    }
}
