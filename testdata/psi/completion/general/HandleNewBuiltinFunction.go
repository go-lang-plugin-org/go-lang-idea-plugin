package main

func (*b) Method() {
}

type b struct {
	field int
}

func main() {
	var t = new(b)
	t.<caret>
}

/**---
field
Method
