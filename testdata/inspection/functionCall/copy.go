package main

type x []int

func main() {
	arr1 := []int{}
	arr2 := []string{}
	arr3 := []byte{}

	/*begin*/copy()/*end.missing arguments to copy*/
	/*begin*/copy(1)/*end.missing arguments to copy*/
	copy(arr1, arr1)
	copy(arr1, x{})
	copy(arr1, /*begin*/arr2/*end.arguments to copy have different element types: []int and []string|CastTypeFix*/)
	copy(arr1, arr1, /*begin*/1/*end.extra argument to copy*/, /*begin*/2/*end.extra argument to copy*/)
}
