package main

type T struct {
	x, y int
}

type T2 struct {
	T
}

func main() {
	t := T2{}
	t.T.<caret>
}
/**---
x
y