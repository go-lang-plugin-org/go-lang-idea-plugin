package main

func main() {
	var ch = make(chan int)
	select {
		case ch <- 1: {
			/*def*/test := 1
			print(/*ref*/test)
		}
	}
}
