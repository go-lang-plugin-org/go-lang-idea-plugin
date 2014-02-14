package main

const (
	EXAMPLE MyInt = 1
)

const EXAMPLE2 = iota -1


type MyInt int
type MyString string

var (
	l_slices = make([]int, 10)
	l_array  = [10]int
	l_map    = map[string]int
)

func main() {
	var my MyString = "heyYou"
	l_slices[/*begin*/EXAMPLE2/*end.Invalid index -1 (index must be non-negative)*/]

	//Slices
	l_slices[0]
	l_slices[EXAMPLE]
	l_slices[1.0]
	l_slices[1.5 + 1.5]
	l_slices[/*begin*/""/*end.Expression type mismatch, the expected type is int*/]
	l_slices[/*begin*/1.5/*end.Expression type mismatch, the expected type is int*/]
	l_slices[/*begin*/-10/*end.Invalid index -10 (index must be non-negative)*/]
	l_slices[/*begin*/5-6/*end.Invalid index -1 (index must be non-negative)*/]
	l_slices[/*begin*/EXAMPLE2/*end.Invalid index -1 (index must be non-negative)*/]

	//Array
	l_array[0]
	l_array[EXAMPLE]
	l_array[1.0]
	l_array[1.5 + 1.5]
	l_array[/*begin*/""/*end.Expression type mismatch, the expected type is int*/]
	l_array[/*begin*/1.5/*end.Expression type mismatch, the expected type is int*/]
	l_array[/*begin*/-10/*end.Invalid index -10 (index must be non-negative)*/]
	l_array[/*begin*/5-6/*end.Invalid index -1 (index must be non-negative)*/]
	l_array[/*begin*/EXAMPLE2/*end.Invalid index -1 (index must be non-negative)*/]

	//Maps
	l_map["Hey"]
	l_map[/*begin*/my/*end.Expression type mismatch, the expected type is string|CastTypeFix*/]
	l_map[/*begin*/0/*end.Expression type mismatch, the expected type is string|CastTypeFix*/]
	l_map[/*begin*/true/*end.Expression type mismatch, the expected type is string|CastTypeFix*/]
	l_map["Hey," + "How are " + "You"]
}
