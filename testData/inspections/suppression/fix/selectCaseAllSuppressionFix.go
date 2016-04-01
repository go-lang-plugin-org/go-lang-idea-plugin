package main

func _() {
	select {
	case foo <- bar:
		println(fo<caret>o)
	}
}