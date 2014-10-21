package main

type Foo struct {
	Things []string
}

func main() {
	foo := doSomething()  // foo marked as Unused
	for _, v := range (*foo).Things { // foo marked as Unresolved
		_ = v
	}
}

func doSomething() *Foo {
	return &Foo{}
}