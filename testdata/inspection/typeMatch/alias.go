package main

func main() {
	var r rune = 'r'
	var i32 int32 = 341
	_ = r + i32
	_ = r == i32
	_ = r > i32

	var b byte = 'b'
	var ui8 uint8 = 54
	_ = b + ui8
	_ = b == ui8
	_ = b > ui8
}
