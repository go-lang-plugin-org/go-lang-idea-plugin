package main

type T int
func (T) M() {}
var w *T
var x *T
var y **T
var z ***T

func main() {
  w.M()
  x.M()
  y.M()
  z.<error>M</error>()
}

type Name struct {
	aaa int
}

type B *Name

func _(b *B) {
	b.<error>aaa</error>
}

func _() {
        b := new(B)
	b.<error>aaa</error>
}