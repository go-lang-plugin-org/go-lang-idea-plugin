package main

const i iota

func main() {
    i := 1
    for /*def*/i := 1;; i++  {
        y := /*ref*/i
    }
}
