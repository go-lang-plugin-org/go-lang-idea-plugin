package main

import (
    "fmt"
)

var <warning>unused</warning> int = 1

var langId = "es"
func main() {
    lId := "es"

    _ = map[string]string{langId: "hola"}
    _ = map[string]string{lId: "hola"}
    _ = map[interface{}]string{foo1: "hola", }
}

func foo1() {

}

type Par []struct{
    Key   string
    Value string
}

func <warning>main1</warning>(i interface{}) {
    // it works
    t := i.(Par)
    _ = t[0].Key

    // it fails
    switch t := i.(type) {
        case Par:
        _ = t[0].Key
    }
}

type Some struct {
    field int
}

func <warning>prtMain</warning>() {
    ptr := &Some{1}
    _ = (*ptr).field
}

type User struct {
    ID int
    Name string
}

func <warning>main23</warning>() {
    members := []User{{ID: 2}, {ID: 3}}
    members2 := []*User{{ID: 2}  }
    members3 := []*struct{ID int}{{ID: 2}  }
    members4 := []*User{&User{ID: 2}, &User{ID: 3}}

    fmt.Println(len(members))
    fmt.Println(len(members2))
    fmt.Println(len(members3))
    fmt.Println(len(members4))
}   

func <warning>main345</warning>() {
    type T1 string
    fmt.Println(T1("1"))
} 