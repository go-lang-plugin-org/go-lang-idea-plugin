package main

type T struct {
	x, y int
}

var _ = []interface{}{
	T<caret>{},
	10: T{1, 2},
	20: T{3, 4},
}

func main (){
}