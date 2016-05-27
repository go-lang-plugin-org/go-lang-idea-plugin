package main

type T struct {
	x, y int
}

var _ = [42]T{
      10: {1,2},
}

func main (){
}