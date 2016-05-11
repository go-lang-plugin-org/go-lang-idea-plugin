package main;

func withParameter(int) { }
func withEmptyResult() () { }
func withReturnValue() int { return 1 }

func main() {
	go withParameter(2)
	defer withParameter(2)

	println(<error descr="withParameter(3) used as value">withParameter(3)</error>)
	<error descr="withParameter(1) used as value">withParameter(1)</error> = 2
	withParameter(2)

	println(<error descr="withEmptyResult() used as value">withEmptyResult()</error>)
	<error descr="withEmptyResult() used as value">withEmptyResult()</error> = 2
	withEmptyResult()


	println(<error descr="func() {}() used as value">func() {}()</error>)
	<error descr="func() {}() used as value">func() {}()</error> = 2
	func() {}()

	println(withReturnValue())
	withReturnValue() = 2
	withReturnValue

	println(func() int { return 1 }())
	func() int { return 1 }() = 2
	func() int { return 1 }()
}