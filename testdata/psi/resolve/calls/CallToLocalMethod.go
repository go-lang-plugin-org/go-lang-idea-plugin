package main

type T int

func (t T) /*def*/f() {
}

func main() {
    /*ref*/f()
}
