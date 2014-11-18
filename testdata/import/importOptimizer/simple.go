package main

import (
    "p1"
    "p2"
)

func main() {
    p1.Println()
}

-----
package main

import "p1"

func main() {
    p1.Println()
}
