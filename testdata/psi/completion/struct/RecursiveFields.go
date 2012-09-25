package main

type T struct {
	value int
	*T
}

func main() {
	t := T{}
	t.<caret>
}
/**---
T
value
