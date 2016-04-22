package main

func main() {
	c := make(chan int)
	for <caret>_ := range "asd"  {
		fmt.Printf("%v\n", 1)
	}
}