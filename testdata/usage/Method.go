package main

type Test struct {
	x int
}

func (t *Test) /*def*/foo() {
}


func main() {
	Test{}./*ref*/foo()
}
