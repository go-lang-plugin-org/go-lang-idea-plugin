package main

func main() {
	<error descr="Unused variable 'sum1'">sum1</error> := 0
	sum1 = 10

	var <error descr="Unused variable 'sum'">sum</error> = 0
	sum = 10

	var sum3 = 0
	sum3 += 10

	sum4 := 0
	sum4 += 10
	
	var       i int
	f(func() { i  = 0; println("test") })
}

func f(m func()) {
	m()
}

func foo() (int, int) {
	return 4, 5
}

func returnTwo() (int, int) {
	return 1, 2
}

func _() {
	a, b := ((returnTwo()))
	_, _ = a, b
}

func _() {
	a, b := <error descr="Multiple-value (returnTwo())() in single-value context">(returnTwo())</error>, 1
	_, _ = a, b
}

func _() {
	<error descr="Assignment count mismatch: 1 = 2"><error descr="Unused variable 'x'">x</error> := foo(), foo()</error>
	<error descr="Assignment count mismatch: 1 = 2"><error descr="Unresolved reference 'y'">y</error> = foo(), foo()</error>
}
