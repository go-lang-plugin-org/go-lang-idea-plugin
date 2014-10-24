package main

func (*int) Pmm() {
    println("hi")
}

func F(func()) {

}

func main() {
    F(/*ref*/Pmm)    // <-- second place
}
