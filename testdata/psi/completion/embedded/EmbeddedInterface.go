package main

type Bird interface {
	Fly()
}

type Swan interface {
	Bird
	Swim()
}

func Play(x Swan) {
	x.<caret>
}

/**---
Fly
Swim
