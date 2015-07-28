package a

import "fmt"

func a() {
	a := getFmt()<caret>
}

func getFmt() int {
	return 1
}