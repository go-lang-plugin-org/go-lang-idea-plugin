package main

type T struct {
	x, y int
}

var _ = []*T{
	&T{1, 2},
}

func main (){
}