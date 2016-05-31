package main

type T struct {
	x, y int
}

var _ = map[string]T{
	"foo": <warning descr="Redundant type declaration">T<caret></warning>{},
}

func main (){
}