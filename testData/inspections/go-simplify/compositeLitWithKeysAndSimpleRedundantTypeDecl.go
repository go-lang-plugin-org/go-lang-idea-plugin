package main

type T struct {
	x, y int
}

var _ = [42]T{
      20: <weak_warning descr="Redundant type declaration">T</weak_warning>{},
      10: <weak_warning descr="Redundant type declaration">T<caret></weak_warning>{1,2},
}

func main (){
}