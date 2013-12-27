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
		/*def*/res1 := "asd"
		res = "int"
		var dem = 2
		_ = /*ref*/res1
	}

	fmt.Printf("%q", res)
}
