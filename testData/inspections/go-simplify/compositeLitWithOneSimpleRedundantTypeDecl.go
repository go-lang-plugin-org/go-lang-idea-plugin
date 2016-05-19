package main

type T struct {
	x, y int
}

var _ = [42]T{
	<weak_warning descr="Redundant type declaration">T<caret></weak_warning>{},
}

func main (){
}