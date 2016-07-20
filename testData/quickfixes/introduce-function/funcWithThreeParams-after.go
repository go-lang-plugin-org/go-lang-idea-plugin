package main

func main() {
  var (
    i int
    r struct {i int}
    q string
   )

  asd(i, r, q)
}
func asd(i int, i2 struct {i int}, i3 string) {
 <caret>
}