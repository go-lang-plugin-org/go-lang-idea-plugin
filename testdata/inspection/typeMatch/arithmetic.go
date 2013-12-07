package main

func main() {
	var a uint
	var b int64
	_ = /*begin*/a + b/*end.mismatched types*/
	_ = /*begin*/a - b/*end.mismatched types*/
	_ = /*begin*/a * b/*end.mismatched types*/
	_ = /*begin*/a / b/*end.mismatched types*/
	_ = /*begin*/a % b/*end.mismatched types*/
	_ = b << a
	_ = /*begin*/a << b/*end.shift count type int64, must be unsigned integer*/
	_ = b + 6
	_ = 7 - a

	type Foo int64
	var foo Foo
	_ = /*begin*/b - foo/*end.mismatched types*/
	var foo2 Foo
	_ = foo - foo2

	type Bar interface {
	}
	var bar Bar
	_ = /*begin*/b * bar/*end.operator * not defined on interface*/
	var bp *int64
	var bp2 *int64
	_ = /*begin*/bp + bp2/*end.operator + not defined on pointer*/

}
