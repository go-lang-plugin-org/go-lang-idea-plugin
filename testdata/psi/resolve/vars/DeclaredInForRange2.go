package main

const i = iota

func main() {
  i := 0
  var s[]string
  for /*def*/i, s[i] := range m {
      println(/*ref*/i)
  }
}
