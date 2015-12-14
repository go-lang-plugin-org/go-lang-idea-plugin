package main

func Recursive(number int) int {
	if number == 1 {
		return number
	}
	return number + Recursive(number-1)<caret>
}
