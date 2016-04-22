package main

import "fmt"

type Foo struct {
    F int
}

func bar() int {
    slice0 := make([]Foo, 10)
    slice1 := slice0[1:1:1]
    slice1[0].F = 1
    return slice1[0].F
}

func main() {
    fmt.Println(bar())
    main2()
}

type Foo2 struct {
    Test int
}

func main2() {
    var a *[]Foo2
    (*a)[0].Test // todo: should be an error
    var b *[]Foo2
    b[0].Test // todo: should be an error

    test(a)
}

func test(a *[]Foo2) {
    fmt.Println((*a)[0].Test)

    for _, c := range *a {
        fmt.Println(c.Test)
    }
}

type Param struct {
    Id string
}

type Params []Param

func <warning>hola</warning>(params Params) {
    params[0].Id
}

type Params []Param

func <warning>hola2</warning>(params []Param) {
    params[0].Id  // the inspector find the Id field
}

type File struct {
    Contents string
}

func <warning>sourceReader</warning>(files <-chan *File) {
    for file := range files {
        file.Contents
    }
}

type FooSl struct {
	a int
}

type Baz [5]FooSl
type ZOO Baz

func _(){
	b := &ZOO{}
	b[0].a = 1
}

type typ1 struct {
}

type typ2 struct {
	t1 typ1
}

func (t typ1) F() {
	print("t1.F()")
}

func _() {
	t2 := typ2{t1:typ1{}}
	t1 := &t2.t1
	t1.F()
}