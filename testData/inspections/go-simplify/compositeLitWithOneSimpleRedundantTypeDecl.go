package main

type T struct {
	x, y int
}

var _ = [42]T{
	<warning descr="Redundant type declaration">T<caret></warning>{},
}

func main (){
}