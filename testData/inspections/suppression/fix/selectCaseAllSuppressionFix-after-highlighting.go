package main

func _() {
	select {
	//noinspection ALL
	case foo <- bar:
		println(fo<caret>o)
	}
}