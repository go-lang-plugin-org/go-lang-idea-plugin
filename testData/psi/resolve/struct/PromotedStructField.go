package main

type S1 struct {
    /*def*/a, b int
}

type S2 struct {
    c, d int
    S1
}

func main() {
    var x = S2{}

    x./*ref*/a
}
