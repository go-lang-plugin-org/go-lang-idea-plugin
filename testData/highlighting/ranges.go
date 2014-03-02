package main

import "fmt"

func append(slice []Type, elems ...Type) []Type
func make(Type, size int) Type
type Type int
type int int
type string string

func println(o... interface {}) {
    fmt.Println(o)
}
type Person struct {
    Name  string
    Likes []string
}
func main() {
    var people []*Person

    likes := make(map[string][]*Person)
    for _, p := range people {
        for _, l := range p.Likes {
            likes[l] = append(likes[l], p)
        }
    }

    for k, v := range likes {
         println(k)
         println(v[1].Likes)
    }

    var people []*Person
    for a, p := range people {
        println(p.Likes)
    }

    for _, p2 := range create() {
        println(p2.Likes)
    }

}
func create() []*Person {return make([]*Person, 0)}