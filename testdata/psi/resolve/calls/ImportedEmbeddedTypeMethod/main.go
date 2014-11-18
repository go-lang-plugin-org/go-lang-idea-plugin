package main

import "p"

type SomeOtherFoo struct {
    p.Embedded
}

func (s SomeOtherFoo) GetThing() string {
    return ""
}

type Foo struct {
    SomeOtherFoo
}

func test() {
    x := Foo{}
    x./*ref*/Get("asdf")
}

func main() {

}
