package main

func (*int) P() {
    println("hi")
}

func F(func()) {

}

func main() {
    F(/*ref*/P)    // <-- second place
}
