package main

func _() {
	/*def*/addr := "test"
	x := struct{ addr string }{addr: /*ref*/addr}
	fmt.Println(x)
}