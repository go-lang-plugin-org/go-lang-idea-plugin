package main

func main() {
	i := 1
	select {
		case /*def*/i := <-c: {
			/*ref*/i
		}
	}
}
