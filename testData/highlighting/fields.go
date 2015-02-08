package main

var langId = "es"
func main() {
    lId := "es"

    _ = map[string]string{langId: "hola"}
    _ = map[string]string{lId: "hola"}
    _ = map[interface{}]string{foo1: "hola", }
}

func foo1() {

}