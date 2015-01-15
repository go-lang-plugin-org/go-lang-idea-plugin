package main

type Bird interface {
	/*def*/Fly()
}

type Swan interface {
	Bird
	Swim()
}

func Play(x Swan) {
	x./*ref*/Fly()
}
