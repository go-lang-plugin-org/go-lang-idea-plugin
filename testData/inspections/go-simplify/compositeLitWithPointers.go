package main

type T struct {
	x, y int
}

var _ = []*T{
	<warning descr="Redundant type declaration">&T<caret></warning>{1, 2},
}

func main (){
}