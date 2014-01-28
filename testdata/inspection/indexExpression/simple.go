package main

const (
	EXAMPLE MyInt = 1
)

type MyInt int
type MyString string

var (
	l_slices = make([]int, 10)
	l_map    = map[string]int
)

func main() {
	var my MyString = "heyYou"
	//Slices
	l_slices[0]
	l_slices[EXAMPLE]
	l_slices[1.0]
	l_slices[/*begin*/""/*end.Expression type mismatch, the expected type is int*/]
	l_slices[/*begin*/1.5/*end.Expression type mismatch, the expected type is int*/]

	//Maps
	l_map["Hey"]
	l_map[/*begin*/my/*end.Expression type mismatch, the expected type is string|CastTypeFix*/]
	l_map[/*begin*/0/*end.Expression type mismatch, the expected type is string|CastTypeFix*/]
	l_map[/*begin*/true/*end.Expression type mismatch, the expected type is string|CastTypeFix*/]
	l_map["Hey," + "How are " + "You"]
}
