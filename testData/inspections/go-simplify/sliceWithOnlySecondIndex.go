package main

var (
  a [10]byte

  _ = a[: <warning descr="Redundant index">len(a)<caret></warning>]
)

func main (){
}