package main

const key = iota

func main() {
    key := 1
    for /*def*/key, val := range m {
        y := /*ref*/key
    }
}
