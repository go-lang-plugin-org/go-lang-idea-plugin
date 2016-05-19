package main

var (
  a [10]byte
  b [20]int

  t struct {
    s []byte
  }

  _ = t.s[0: len(t.s)]
)

func main (){
}