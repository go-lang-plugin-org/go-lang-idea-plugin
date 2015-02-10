package foo

type Dummy int

func (d *Dummy) bar() {
}

func test() {
	d := new(Dummy)
	d.bar(<caret>)
}

