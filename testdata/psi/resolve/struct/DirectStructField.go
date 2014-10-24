package main

type S struct {
    b, c, /*def*/a int
}

func main() {
    var x = S{}
    x./*ref*/a
}
