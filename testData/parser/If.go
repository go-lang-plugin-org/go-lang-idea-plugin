package main

import "fmt"

type A struct {

}

func main() {
    var err error
    if false { panic(err) }
    if err !=nil {panic(err)}
    fmt.Println("Hello, playground")
    
    x := make(map[string]int)
    x["foo"] = 1
    
    if _, ok := x["foo"]; !ok {fmt.Println("Missing key 'foo'")}
    
    type A struct {}
    if &A{} {panic("foo")} // error, it's ok
}
