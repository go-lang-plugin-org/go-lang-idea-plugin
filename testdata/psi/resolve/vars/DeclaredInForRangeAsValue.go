package main

const key2 = iota

func main() {
    key1 := 1
    for key1, /*def*/key2 := range {
        x := /*ref*/key2
    }
}
