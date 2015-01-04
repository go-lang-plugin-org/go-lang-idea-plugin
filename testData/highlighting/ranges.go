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

type (
    Packet struct {
        path string
    }

    Plugin struct {
        Path    string
        Packets Packets
    }
    Packets []*Packet
)

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

    var <error descr="Unused variable 'people'">people</error> []*Person // todo: should be redeclared
    for a, p := range people {
        println(p.Likes)
    }

    for _, p2 := range create() {
        println(p2.Likes)
    }
    
    var p Plugin = nil
    for _, p := range p.Packets {
        println(p.path)
    }
    
    for _, d := range <error>d</error>.Packets {
    }
}
func create() []*Person {return make([]*Person, 0)}