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
}