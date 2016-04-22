package main

func main() {
	c := make(chan int)
	select {
	case <caret>_ := <-c:
		println("Ololo")
	default:
	}
}