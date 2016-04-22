package main

func main() {
	c := make(chan int)
	buzz := 0
	println(buzz)

	buzz = <-c
}