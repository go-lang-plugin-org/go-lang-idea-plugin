package main

func f([]byte) {

}

func main() {
    f([]byte("string"))) // should not be highlighed as an error
}
