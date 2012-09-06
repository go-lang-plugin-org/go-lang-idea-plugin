package main

type T struct {
	x, y int
}

func main() {
	var t = T{x: 5, y: 3}
	t.<caret>
}

/**---
x
y