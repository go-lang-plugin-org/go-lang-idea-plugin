package main

type Test int

func (/*def*/t Test) foo() {
    /*ref*/t = 3
    /*ref*/t += 5
    println(/*ref*/t)
    if /*ref*/t > 2 {
        println(/*ref*/t + 1)
    } else {
        t := 3
        println(t)
    }
}
