package main

/*def*/func (*int) P() {
    println("hi")
}

func F(func()) {

}

func main() {
    F(/*ref*/P)    // <-- second place
}
