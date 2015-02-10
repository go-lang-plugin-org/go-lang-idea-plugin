package foo

type Dummy struct {
	a int
}

func (d* Dummy) bar(num int, text string) {
}

func test() {
	d := new(Dummy)
	d.bar(1, <caret>)
}

