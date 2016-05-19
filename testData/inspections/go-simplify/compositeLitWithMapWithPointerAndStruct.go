package main

var _ = map[string]*struct {
	x, y int
}{
	"bal": &struct<caret>{ x, y int }{3, 4},
}

func main (){
}