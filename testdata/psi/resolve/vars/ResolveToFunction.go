package main

/*def*/func P() {
    println("hi")
}

func F(func()) {

}

func main() {
    F(/*ref*/P)    // <-- second place
}
