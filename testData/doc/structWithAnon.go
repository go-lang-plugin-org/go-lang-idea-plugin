package main

var st struct {
  int64
  int
  *string
)

func Test() {
  s<caret>t = nil
}