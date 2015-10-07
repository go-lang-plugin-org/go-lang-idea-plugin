package main

func _() {
	addr := "test"
	x := struct{ /*def*/addr string }{/*ref*/addr: addr}
	fmt.Println(x)
}