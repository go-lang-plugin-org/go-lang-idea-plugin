package main

func main() {
	go func(param1, param2 string) (return1, return2 string) {
		return param1, param2
	}("1", <caret>)
}
