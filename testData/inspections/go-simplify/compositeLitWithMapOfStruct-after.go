package main

type T struct {
	x, y int
}

var _ = map[string]struct { + test
	x, y int
}{
	"bal": struct{<caret> x, y int }{3, 4},
}

func main (){
}