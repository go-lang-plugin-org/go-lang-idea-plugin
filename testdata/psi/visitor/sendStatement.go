package main

func main() {
	ch <- 3
	select {
		case ch2 <- 3: {

		}
	}
}
/**----
GoSendStatement
/**----
ch <- 3
ch2 <- 3
