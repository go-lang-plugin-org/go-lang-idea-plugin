package main

type S struct {
    /*def*/a int
}

func main() {
    var a interface{}
    switch t := a.(type) {
    case S:
        println(t./*ref*/a)
    }
}
