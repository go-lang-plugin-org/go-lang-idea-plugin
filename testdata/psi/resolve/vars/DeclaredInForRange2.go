package main

const i = iota

func main() {
  i := 0
  for /*def*/i, _ := range m {
      println(/*ref*/i)
  }
}
