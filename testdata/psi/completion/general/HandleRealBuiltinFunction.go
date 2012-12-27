package main

func (*float32) Method() {

}

func main() {
	var float = real(complex(1.0, 1.0))
	float.<caret>
}

/**---
Method
