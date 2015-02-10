package foo

func bar(num int, text string, more ...int) {
}

func test() {
	bar(1, "text", 2, 3, 4, <caret>)
}

