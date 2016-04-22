package main

func main() {
	c := make(chan int)
	for _ = range "asd" {
		fmt.Printf("%v\n", 1)
	}
}