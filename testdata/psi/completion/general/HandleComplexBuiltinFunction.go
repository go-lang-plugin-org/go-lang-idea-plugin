package main

func (*complex128) Method() {

}

func main() {
	var t = complex(1.0, 1.0)
	t.<caret>
}

/**---
Method
