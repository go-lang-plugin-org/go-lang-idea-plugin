package main

func main() {
  a(asd())
}
func asd() (int, string) {
 <caret>
}

func a(i int, s string) {}