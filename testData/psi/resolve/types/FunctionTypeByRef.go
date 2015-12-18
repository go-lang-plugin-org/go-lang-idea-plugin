package main

import (
	"fmt"
)

type S struct {
	/*def*/a int
}

type F func() []S

var f F = func() []S {
	return make([]S, 1)
}

func main() {
	fmt.Println(f()[0]./*ref*/a)
}