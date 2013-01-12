package main

func main() {
	var ch = make(chan int)
	select {
		case test := <-ch: {
			/*def*/test := 1
			print(/*ref*/test)
		}
	}
}
