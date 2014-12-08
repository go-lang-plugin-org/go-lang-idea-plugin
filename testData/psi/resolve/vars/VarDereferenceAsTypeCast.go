package main

type Foo struct {
	Things []string
}

func doSomething() *Foo {
	return &Foo{}
}

func main() {
	/*def*/foo := doSomething()                 // foo marked as Unused
	for _, v := range (*/*ref*/foo).Things {    // foo marked as Unresolved
		_ = v
	}
}
