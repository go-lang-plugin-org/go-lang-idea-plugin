package main

import "bytes"

func main() {
    var /*def*/b bytes.Buffer

    for i, b := range /*ref*/b {
        _, _ = i, b
    }
}