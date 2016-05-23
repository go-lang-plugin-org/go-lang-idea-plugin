package main
type t struct { s string }
var (
  p = t {"asd"}
  _ = p.s[1:2<error descr="Invalid operation p.s[1:2:3] (3-index slice of string)">:3</error>]
)
func main(){

}