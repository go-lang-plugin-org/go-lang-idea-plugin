package main

func _() {
	select {
	//noinspection ALL
	case foo <- bar:
		println(foo)
	}
}