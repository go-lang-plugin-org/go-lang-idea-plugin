package main

type T struct {
	x, y int
}

var _ = [][]int{
	([]int<caret>{}),
	([]int{1, 2}),
}

func main (){
}