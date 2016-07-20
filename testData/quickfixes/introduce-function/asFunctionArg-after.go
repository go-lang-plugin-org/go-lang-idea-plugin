package main

func main() {
  a(1,asd())
}
func asd() string {
 <caret>
}

func a(i int, s string) {}