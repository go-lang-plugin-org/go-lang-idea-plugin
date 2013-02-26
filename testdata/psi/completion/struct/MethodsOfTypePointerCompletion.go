package main


type Bird struct {}

func (*Bird) Fly(){}

type Swan struct{
	*Bird
}

func Play(x *Swan) {
	x.<caret>
}

/**---
Bird
Fly
