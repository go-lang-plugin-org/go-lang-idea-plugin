package main

func (*int) Pmm() {
    println("hi")
}

func F(func()) {

}

func main() {
    F(/*no ref*/Pmm)    // <-- second place
}
