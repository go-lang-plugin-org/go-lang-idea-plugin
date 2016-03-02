package main

import "fmt"

type TestType struct{}

func (t TestType) func1() {
	fmt.Println("func1!!!")
}

func test2() (
TestType,
error ) {
	return TestType{}, nil
}

func test() (
TestType,
error,
) {
	return TestType{}, nil
}

func main() {
	t, _ := test()
	t.func1() // Unresolved reference here
}