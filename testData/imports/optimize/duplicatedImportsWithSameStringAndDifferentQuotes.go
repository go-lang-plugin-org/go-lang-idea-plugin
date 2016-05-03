package foo

import `fmt`
import <error descr="Redeclared import">"fmt"</error>

func main() {
  fmt.Println("Hi")
}