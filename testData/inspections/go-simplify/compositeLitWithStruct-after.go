package main

type T struct {
	x, y int
}

var _ = map[string]struct {
	x, y int
}{
	{3, 4},
}

func main (){
}