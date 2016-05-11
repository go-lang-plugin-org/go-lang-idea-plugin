package main

func main() {
  f<caret>oo()
}

func foo(i int, f func(s string, u uint32)) (f func(s string, u uint32)) {

}