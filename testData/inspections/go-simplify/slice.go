package main

var (
  a [10]byte
  b [20]int

  t struct {
    s byte
  }

  _ = a[0: <warning descr="Redundant index">len(a)<caret></warning>]
)

func main (){
}