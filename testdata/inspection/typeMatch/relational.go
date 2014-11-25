package main

func main() {
	var a int = 3
	var b uint = 4
	_ = /*begin*/a < b/*end.invalid operation: a < b (mismatched types int and uint)*/
	_ = /*begin*/a > b/*end.invalid operation: a > b (mismatched types int and uint)*/
	_ = /*begin*/a == b/*end.invalid operation: a == b (mismatched types int and uint)*/
	_ = /*begin*/a != b/*end.invalid operation: a != b (mismatched types int and uint)*/
	var ap *int
	var ap2 *int
	var bp *uint
	_ = /*begin*/ap == bp/*end.invalid operation: ap == bp (mismatched types *int and *uint)*/
	_ = ap == ap2
	_ = ap != nil
	var err error
	_ = err == nil
	_ = /*begin*/err == ap/*end.invalid operation: err == ap (mismatched types error and *int)*/
}
