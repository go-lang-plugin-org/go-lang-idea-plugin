package main

type T struct {
	x, y int
}

var _ = [][][]int{
	[][]{
		[]<caret>{0, 1, 2, 3},
	},
}

func main (){
}