package main

func main() {
	var a int = 3
	var b uint = 4
	_ = /*begin*/a < b/*end.mismatched types*/
	_ = /*begin*/a > b/*end.mismatched types*/
	_ = /*begin*/a == b/*end.mismatched types*/
	_ = /*begin*/a != b/*end.mismatched types*/
	var ap *int
	var ap2 *int
	var bp *uint
	_ = /*begin*/ap == bp/*end.mismatched types*/
	_ = ap == ap2
	_ = ap != nil
	var err error
	_ = err == nil
	_ = /*begin*/err == ap/*end.mismatched types*/
}
