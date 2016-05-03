package main

import "asdf"
import <error descr="Redeclared import">"asdf"</error>
import <error descr="Redeclared import"><error descr="Redundant alias">asdf</error> "asdf"</error>

func main() {
  fmt.Printf("a2",a2)
}