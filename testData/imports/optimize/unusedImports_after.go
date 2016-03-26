package main

import "fmt"

func main() {
  fmt.Printf("a2",a2)
}

type testStruct struct  {
	v string
}

func test(json testStruct) {
	return json.v
}