package main

func x() (int, int) {
	return 0, 0
}

func implementedInt(a, b int) {
	_ = a + b
	_ = a - b
	_ = a | b
	_ = a ^ b

	_ = a * b
	_ = a / b
	_ = a % b
}

func floatOperands(a, b float32) {
	_ = a + b
	_ = a - b
	_ = /*begin*/a | b/*end.invalid operation: a | b (operator | not defined on float32)*/
	_ = /*begin*/a ^ b/*end.invalid operation: a ^ b (operator ^ not defined on float32)*/

	_ = a * b
	_ = a / b
	_ = /*begin*/a % b/*end.invalid operation: a % b (operator % not defined on float32)*/
}

func complexOperands(a, b complex128) {
	_ = a + b
	_ = a - b
	_ = /*begin*/a | b/*end.invalid operation: a | b (operator | not defined on complex128)*/
	_ = /*begin*/a ^ b/*end.invalid operation: a ^ b (operator ^ not defined on complex128)*/

	_ = a * b
	_ = a / b
	_ = /*begin*/a % b/*end.invalid operation: a % b (operator % not defined on complex128)*/
}

func stringOperands(a, b string) {
	_ = a + b
	_ = /*begin*/a - b/*end.invalid operation: a - b (operator - not defined on string)*/
	_ = /*begin*/a | b/*end.invalid operation: a | b (operator | not defined on string)*/
	_ = /*begin*/a ^ b/*end.invalid operation: a ^ b (operator ^ not defined on string)*/

	_ = /*begin*/a * b/*end.invalid operation: a * b (operator * not defined on string)*/
	_ = /*begin*/a / b/*end.invalid operation: a / b (operator / not defined on string)*/
	_ = /*begin*/a % b/*end.invalid operation: a % b (operator % not defined on string)*/
}

func arrayOperands(a, b [10]int) {
	_ = /*begin*/a + b/*end.invalid operation: a + b (operator + not defined on array)*/
	_ = /*begin*/a - b/*end.invalid operation: a - b (operator - not defined on array)*/
	_ = /*begin*/a | b/*end.invalid operation: a | b (operator | not defined on array)*/
	_ = /*begin*/a ^ b/*end.invalid operation: a ^ b (operator ^ not defined on array)*/

	_ = /*begin*/a * b/*end.invalid operation: a * b (operator * not defined on array)*/
	_ = /*begin*/a / b/*end.invalid operation: a / b (operator / not defined on array)*/
	_ = /*begin*/a % b/*end.invalid operation: a % b (operator % not defined on array)*/
}

func sliceOperands(a, b []int) {
	_ = /*begin*/a + b/*end.invalid operation: a + b (operator + not defined on slice)*/
	_ = /*begin*/a - b/*end.invalid operation: a - b (operator - not defined on slice)*/
	_ = /*begin*/a | b/*end.invalid operation: a | b (operator | not defined on slice)*/
	_ = /*begin*/a ^ b/*end.invalid operation: a ^ b (operator ^ not defined on slice)*/

	_ = /*begin*/a * b/*end.invalid operation: a * b (operator * not defined on slice)*/
	_ = /*begin*/a / b/*end.invalid operation: a / b (operator / not defined on slice)*/
	_ = /*begin*/a % b/*end.invalid operation: a % b (operator % not defined on slice)*/
}

func mapOperands(a, b map[int]int) {
	_ = /*begin*/a + b/*end.invalid operation: a + b (operator + not defined on map)*/
	_ = /*begin*/a - b/*end.invalid operation: a - b (operator - not defined on map)*/
	_ = /*begin*/a | b/*end.invalid operation: a | b (operator | not defined on map)*/
	_ = /*begin*/a ^ b/*end.invalid operation: a ^ b (operator ^ not defined on map)*/

	_ = /*begin*/a * b/*end.invalid operation: a * b (operator * not defined on map)*/
	_ = /*begin*/a / b/*end.invalid operation: a / b (operator / not defined on map)*/
	_ = /*begin*/a % b/*end.invalid operation: a % b (operator % not defined on map)*/
}

func pointerOperands(a, b *int) {
	_ = /*begin*/a + b/*end.invalid operation: a + b (operator + not defined on pointer)*/
	_ = /*begin*/a - b/*end.invalid operation: a - b (operator - not defined on pointer)*/
	_ = /*begin*/a | b/*end.invalid operation: a | b (operator | not defined on pointer)*/
	_ = /*begin*/a ^ b/*end.invalid operation: a ^ b (operator ^ not defined on pointer)*/

	_ = /*begin*/a * b/*end.invalid operation: a * b (operator * not defined on pointer)*/
	_ = /*begin*/a / b/*end.invalid operation: a / b (operator / not defined on pointer)*/
	_ = /*begin*/a % b/*end.invalid operation: a % b (operator % not defined on pointer)*/
}

func interfaceOperands(a, b interface{}) {
	_ = /*begin*/a + b/*end.invalid operation: a + b (operator + not defined on interface)*/
	_ = /*begin*/a - b/*end.invalid operation: a - b (operator - not defined on interface)*/
	_ = /*begin*/a | b/*end.invalid operation: a | b (operator | not defined on interface)*/
	_ = /*begin*/a ^ b/*end.invalid operation: a ^ b (operator ^ not defined on interface)*/

	_ = /*begin*/a * b/*end.invalid operation: a * b (operator * not defined on interface)*/
	_ = /*begin*/a / b/*end.invalid operation: a / b (operator / not defined on interface)*/
	_ = /*begin*/a % b/*end.invalid operation: a % b (operator % not defined on interface)*/
}

func bidiChannelOperands(a, b chan int) {
	_ = /*begin*/a + b/*end.invalid operation: a + b (operator + not defined on chan)*/
	_ = /*begin*/a - b/*end.invalid operation: a - b (operator - not defined on chan)*/
	_ = /*begin*/a | b/*end.invalid operation: a | b (operator | not defined on chan)*/
	_ = /*begin*/a ^ b/*end.invalid operation: a ^ b (operator ^ not defined on chan)*/

	_ = /*begin*/a * b/*end.invalid operation: a * b (operator * not defined on chan)*/
	_ = /*begin*/a / b/*end.invalid operation: a / b (operator / not defined on chan)*/
	_ = /*begin*/a % b/*end.invalid operation: a % b (operator % not defined on chan)*/
}

func sendChannelOperands(a, b <-chan int) {
	_ = /*begin*/a + b/*end.invalid operation: a + b (operator + not defined on chan)*/
	_ = /*begin*/a - b/*end.invalid operation: a - b (operator - not defined on chan)*/
	_ = /*begin*/a | b/*end.invalid operation: a | b (operator | not defined on chan)*/
	_ = /*begin*/a ^ b/*end.invalid operation: a ^ b (operator ^ not defined on chan)*/

	_ = /*begin*/a * b/*end.invalid operation: a * b (operator * not defined on chan)*/
	_ = /*begin*/a / b/*end.invalid operation: a / b (operator / not defined on chan)*/
	_ = /*begin*/a % b/*end.invalid operation: a % b (operator % not defined on chan)*/
}

func recvChannelOperands(a, b chan<- int) {
	_ = /*begin*/a + b/*end.invalid operation: a + b (operator + not defined on chan)*/
	_ = /*begin*/a - b/*end.invalid operation: a - b (operator - not defined on chan)*/
	_ = /*begin*/a | b/*end.invalid operation: a | b (operator | not defined on chan)*/
	_ = /*begin*/a ^ b/*end.invalid operation: a ^ b (operator ^ not defined on chan)*/

	_ = /*begin*/a * b/*end.invalid operation: a * b (operator * not defined on chan)*/
	_ = /*begin*/a / b/*end.invalid operation: a / b (operator / not defined on chan)*/
	_ = /*begin*/a % b/*end.invalid operation: a % b (operator % not defined on chan)*/
}

func constantOperands() {
	_ = 1 + 2.5
	_ = 1 - 2.5
	_ = 1 | 2.5
	_ = 1 ^ 2.5
	_ = 1 + 2.5

}
func multipleValueOperands() {
	_ = /*begin*/x()/*end.multiple-value x() in single-value context*/ + 1
	_ = 1 - /*begin*/x()/*end.multiple-value x() in single-value context*/
	_ = /*begin*/x()/*end.multiple-value x() in single-value context*/ |  1 | /*begin*/x()/*end.multiple-value x() in single-value context*/
	_ = /*begin*/x()/*end.multiple-value x() in single-value context*/ ^ 1
	
	_ = 1 * /*begin*/x()/*end.multiple-value x() in single-value context*/
	_ = /*begin*/x()/*end.multiple-value x() in single-value context*/ / 1
	_ = 1 % /*begin*/x()/*end.multiple-value x() in single-value context*/
}

func main() {
}
