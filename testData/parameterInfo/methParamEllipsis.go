package foo

type Dummy int

func (d *Dummy) bar(num int, text string, more ...int) {
}

func test() {
	d := new(Dummy)
	d.bar(1, "text", 2, 3, 4, <caret>)
}

