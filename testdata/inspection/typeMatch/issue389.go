package main

func main() {
	var a uint
	var b int64
	_ = /*begin*/a + ^/*end.invalid operation: a + ^ (mismatched types uint and unknown)*/
}
