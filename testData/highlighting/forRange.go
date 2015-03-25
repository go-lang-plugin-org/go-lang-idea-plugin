package main

import (
    "fmt"
    "time"
)

func main() {
    var tick = 0
    for range time.NewTicker(1 * time.Second).C {
        fmt.Println("Tick")
        tick++
        if tick > 3 {
            break
        }
    }
}