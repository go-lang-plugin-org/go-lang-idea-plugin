package main

func main() {
	// issue #480
	arr1 := []int{}
	arr2 := []string{}
	arr3 := []byte{}
	arr1 = append(arr1, 1)
	arr1 = append(arr1, 1, 2, 3)
	arr1 = append(arr1, /*begin*/1.2/*end.Expression type mismatch, the expected type is int|CastTypeFix*/)
	arr1 = append(arr1, /*begin*/""/*end.Expression type mismatch, the expected type is int|CastTypeFix*/)

	copy(arr1, arr1)
	copy(arr1, /*begin*/arr2/*end.Expression type mismatch, the expected type is []int|CastTypeFix*/)

	map1 := map[string]int{}
	delete(map1, "key")
	delete(map1, /*begin*/1/*end.Expression type mismatch, the expected type is string|CastTypeFix*/)

	// This test will be broken when issue #609 is fixed
	arr1 = append(arr1, /*begin*/arr1/*end.Expression type mismatch, the expected type is int|CastTypeFix*/...)
	arr3 = append(arr3, /*begin*/"test"/*end.Expression type mismatch, the expected type is byte|CastTypeFix*/...)

}
