package main

func main() {
	// issue #480
	arr1 := []int{}
	arr2 := []string{}
	arr3 := []byte{}
	arr1 = append(arr1, 1)
	arr1 = append(arr1, 1, 2, 3)
	arr1 = append(arr1, /*begin*/1.2/*end.cannot use 1.2 (type float64) as type int in argument to append|CastTypeFix*/)
	arr1 = append(arr1, /*begin*/""/*end.cannot use "" (type string) as type int in argument to append|CastTypeFix*/)

	arr1 = append(arr1, arr1...)
	arr3 = append(arr3, /*begin*/"test"/*end. cannot use "test" (type string) as type byte in argument to append|CastTypeFix*/...)
}
