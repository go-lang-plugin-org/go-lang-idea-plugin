package main

type T struct {
	x, y int
}

var _ = [][]int{
	[]int<caret>{3, 4},
}

func main (){
}