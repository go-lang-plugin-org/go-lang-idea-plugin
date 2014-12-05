package main

const (
	EXAMPLE MyInt = 1
)

const EXAMPLE2 = iota -1


type MyInt int
type MyString string

var (
	l_slices = make([]int, 10)
	l_array  [10]int
	l_map    map[string]int
)

func main() {
	var my MyString = "heyYou"
	l_slices[/*begin*/EXAMPLE2/*end.invalid slice index expression EXAMPLE2 with value -1 (index must be non-negative)*/]

	//Slices
	l_slices[0]
	l_slices[EXAMPLE]
	l_slices[1.0]
	l_slices[1.5 + 1.5]
	l_slices[/*begin*/""/*end.non-integer slice index ""*/]
	l_slices[/*begin*/1.5/*end.non-integer slice index 1.5*/]
	l_slices[/*begin*/-10/*end.invalid slice index expression -10 with value -10 (index must be non-negative)*/]
	l_slices[/*begin*/5-6/*end.invalid slice index expression 5-6 with value -1 (index must be non-negative)*/]
	l_slices[/*begin*/EXAMPLE2/*end.invalid slice index expression EXAMPLE2 with value -1 (index must be non-negative)*/]

	//Array
	l_array[0]
	l_array[EXAMPLE]
	l_array[1.0]
	l_array[1.5 + 1.5]
	l_array[/*begin*/""/*end.non-integer array index ""*/]
	l_array[/*begin*/1.5/*end.non-integer array index 1.5*/]
	l_array[/*begin*/-10/*end.invalid array index expression -10 with value -10 (index must be non-negative)*/]
	l_array[/*begin*/5-6/*end.invalid array index expression 5-6 with value -1 (index must be non-negative)*/]
	l_array[/*begin*/EXAMPLE2/*end.invalid array index expression EXAMPLE2 with value -1 (index must be non-negative)*/]

	//Maps
	l_map["Hey"]
	l_map[/*begin*/my/*end.cannot use my (type MyString) as type string in map index|CastTypeFix*/]
	l_map[/*begin*/0/*end.cannot use 0 (type int) as type string in map index|CastTypeFix*/]
	l_map[/*begin*/true/*end.cannot use true (type bool) as type string in map index|CastTypeFix*/]
	l_map["Hey," + "How are " + "You"]

	/*begin*/int("")[100]/*end.invalid operation: int("")[100] (type int does not support indexing)*/
}

func testGlitch(b []byte) {
	for i := range b {
		if i < 0 && b[i] == ' ' {
		}
	}
}
