package main

type T struct {
	x, y int
}

var _ = map[string]T{
	"foo": {},
}

func main (){
}