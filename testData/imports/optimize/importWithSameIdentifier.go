package main

import <error descr="Redundant alias">fmt</error> "fmt"
import <error descr="Redeclared import">fmt "math"</error>
import <error descr="Redundant alias">bar</error> "foo/bar"

func main() {
  fmt.Printf("a2",a2)
  fmt.Println(bar.Hello)
}