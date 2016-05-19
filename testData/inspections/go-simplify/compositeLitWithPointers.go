package main

type T struct {
	x, y int
}

var _ = []*T{
	<weak_warning descr="Redundant type declaration">&T<caret></weak_warning>{1, 2},
}

func main (){
}