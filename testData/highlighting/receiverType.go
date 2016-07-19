package main

import (
	"fmt"
)

type Vertex struct {
	X, Y float64
}

func main() {
	v := Vertex{3, 4}
	fmt.Print(v.Abs())
}

func (v *Vertex) Abs() float64 {
	return   (*v).X*v.X + v.Y*v.Y
}

func (<error descr="Unresolved type 'Abs'">Abs</error>) methodOnFunction() {
}