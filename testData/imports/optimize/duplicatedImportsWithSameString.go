package main

import "asdf"
import <error descr="Redundant alias">fmt</error> "fmt"
import <error descr="Redeclared import"><error descr="Redundant alias">fmt</error> "fmt"</error>
import <error descr="Redeclared import">"fmt"</error>

func main() {
  fmt.Printf("a2",a2)
}