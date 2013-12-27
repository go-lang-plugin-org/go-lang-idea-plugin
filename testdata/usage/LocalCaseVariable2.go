package main

import "fmt"

func main() {
	var demo = 1

	var res string
	switch typ := demo.(type) {
	case uint:
		res1 := "bla"
		res = "uint"
		var dem = 2
	case int:
		res1 := "asd"
		res = "int"
		var /*def*/dem = 2
		_ = res1
		_ = /*ref*/dem
	}

	fmt.Printf("%q", res)
}
