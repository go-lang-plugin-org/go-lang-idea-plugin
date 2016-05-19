package main

type T struct {
	x, y int
}

var _ = map[string]T{
	"foo": <weak_warning descr="Redundant type declaration">T<caret></weak_warning>{},
}

func main (){
}