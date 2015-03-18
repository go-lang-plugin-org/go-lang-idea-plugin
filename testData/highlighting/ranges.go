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
    for <error descr="Unused variable 'a'">a</error>, p := range people {
        println(p.Likes)
    }

    for _, p2 := range create() {
        println(p2.Likes)
    }
    
    var p Plugin = nil
    for _, p := range p.Packets {
        println(p.path)
    }
    
    for _, <error descr="Unused variable 'd'">d</error> := range <error descr="Unresolved reference 'd'">d</error>.Packets {
    }
}
func create() []*Person {return make([]*Person, 0)}

type myStruct struct {
    MyVal bool
}

type myChanType chan myStruct

func chanFn(c myChanType) {
    for v := range c {
             fmt.Printf("Got %v\n", v.MyVal) // v.MyVal is unresolved
    }
}

func <warning>main2</warning>() {
    ch := make(myChanType)
    go chanFn(ch)
    ch <- myStruct{true}
}