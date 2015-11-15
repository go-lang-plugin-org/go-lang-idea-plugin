package demo

import "fmt"

func _() {
  fmt.Println("Hello")
}

func <error descr="import \"fmt\" redeclared in this block">fmt</error>() {}

