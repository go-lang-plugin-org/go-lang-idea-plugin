package main

var (
  a [10]byte
  b [20]int

  t struct {
    s byte
  }

  _ = a[0: <weak_warning descr="Redundant index">len(a)<caret></weak_warning>]
)

func main (){
}