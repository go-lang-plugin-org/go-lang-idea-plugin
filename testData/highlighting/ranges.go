package main

import "fmt"

func append1([]Type, ...Type) []Type
type Type int
type <warning descr="Type 'int' collides with builtin type">int</warning> int
type <warning descr="Type 'string' collides with builtin type">string</warning> string

func println1(o... interface {}) {
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
            likes[l] = append1(likes[l], p)
        }
    }

    for k, v := range likes {
         println1(k)
         println1(v[1].Likes)
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

func _() {
    ch := make(myChanType)
    go chanFn(ch)
    ch <- myStruct{true}
}


type sampleType struct {
    a int
}

type a_ b_
type b_ sampleChan
type sampleChan chan sampleType

func sample() a_ {
	return make(chan sampleType, 3)
}

func _() {
	c := sample()
	c <- sampleType{1}
	c <- sampleType{2}
	c <- sampleType{3}
	close(c)

	for t := range (((c))) {
		println(t.a)
	}
}