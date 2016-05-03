package main

import `asdf`
import (
  <error descr="Unused import">`math`</error>
  <error descr="Redeclared import"><error descr="Unused import">`math`</error></error>
)
import `fmt`

func main() {
  fmt.Printf("a2",a2)
}