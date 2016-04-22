package main

func main() {
	c := make(chan int)
	select {
	case _ = <-c:
		println("Ololo")
	default:
	}
}