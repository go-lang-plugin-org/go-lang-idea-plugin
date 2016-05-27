package main

type T struct {
	x, y int
}

var _ = [42]T{
      10: <weak_warning descr="Redundant type declaration">T<caret></weak_warning>{1,2},
}

func main (){
}