package main

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

