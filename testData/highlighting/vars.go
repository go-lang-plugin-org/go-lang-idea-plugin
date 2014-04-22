package main

import "fmt"

func main() {
	b := 1
	b, a := 11, 1
	fmt.Println(b, a)
	c := simple(10)
	fmt.Println(c)
}

func simple(a int) int {
	a, b := 1, 2
	return a + b
}