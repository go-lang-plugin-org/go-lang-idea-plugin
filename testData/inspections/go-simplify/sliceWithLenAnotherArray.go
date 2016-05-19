package main

var (
  a [10]byte
  b [20]int

  t struct {
    s byte
  }

  _ = b[0: len(a)]
)

func main (){
}