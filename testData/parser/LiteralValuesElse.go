package main

import (
    "fmt"
)

func main() {
    s := struct{ Username, Password string }{
        "User",
        "Password",
    }

    if s != struct{ Username, Password string }{} {
        fmt.Println("yes")
    } else {
        fmt.Println("yes")
    }
}