package main

type My<caret>Type struct {}

func _() {
	var s MyType = "asdf"
	println(s)
}