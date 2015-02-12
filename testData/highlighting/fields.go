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