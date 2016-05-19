package main

var (
  a [10]byte

  _ = a[: <weak_warning descr="Redundant index">len(a)<caret></weak_warning>]
)

func main (){
}