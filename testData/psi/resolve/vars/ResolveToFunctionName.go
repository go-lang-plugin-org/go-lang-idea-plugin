package main

func /*def*/Pff() {
    println("hi")
}

func F(func()) {

}

func main() {
    F(/*ref*/Pff)    // <-- second place
}
