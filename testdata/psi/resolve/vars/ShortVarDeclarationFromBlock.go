package main

func main() {
    /*def*/x, test := 10, true
    if test {
        /*ref*/x = 11
    }
    println(x)
}
