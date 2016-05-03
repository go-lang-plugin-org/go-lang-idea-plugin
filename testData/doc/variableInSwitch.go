package main

type Foo struct {}

func _() {
	var err interface{}
	switch err := err.(type) {
	case Foo:
		println(e<caret>rr)
	}
}