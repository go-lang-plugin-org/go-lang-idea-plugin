package main

func (*int) /*def*/Pmm() {
    println("hi")
}

func F(func()) {

}

func main() {
    F(/*ref*/Pmm)    // <-- second place
}
