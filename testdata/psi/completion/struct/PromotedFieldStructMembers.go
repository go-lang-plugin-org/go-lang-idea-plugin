package main

type Embedded struct {
	emA int
	emB int
}

type EmbeddedPointer struct {
	emPointerA int
	emPointerB int
}

type Embedding struct {
	Embedded
	*EmbeddedPointer
	a float32
	b int
}

func main() {
	var x Embedding
	x.<caret>
}
// missing are emPointerA
// missing are emPointerB
/**---
a
b
Embedded
EmbeddedPointer
