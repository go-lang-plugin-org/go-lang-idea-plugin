package main

func main() {
	if err := doSomething(); err != nil {
	}
	if err := doSomething(); err != nil { // Case 1: err variable claimed to be not new
	}
	err := doSomething() // Case 2: err variable claimed to be not new
	if err == nil {
	}
}

func doSomething() error {
	return nil
}