package main

import "asdf"
import "encoding/json"
import (
  <error descr="Unused import">"m<caret>ath"</error>
  <error descr="Redeclared import"><error descr="Unused import">"math"</error></error>
)
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