package main

import "math"

// don't check type fields
type Vertex struct {
    X, Y float64
}

func (v *Vertex) Abs() float64 {
	return math.Sqrt(v.X*v.X + v.Y*v.Y)
}

func (v Vertex) Abs2() float64 {
	return math.Sqrt(v.X*v.X + v.Y*v.Y)
}

func main() {
    v1 := &Vertex{3, 4}
    v2 := &Vertex{X:3, Y:4}
    println(v1.Abs())
    println(v2.Abs2())
}