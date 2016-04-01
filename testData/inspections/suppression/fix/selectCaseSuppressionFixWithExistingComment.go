package main

func _() {
	select {
	//noinspection BlaBlaBla
	case foo <- bar:
		println(fo<caret>o)
	}
}