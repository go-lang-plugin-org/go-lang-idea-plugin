package foo

func bar(num int, text string) {
}

func blah(a, b, c, d int) int {
	return a + b
}

func test() {
	blah(1, 2, bar(3, <caret>), 4)
}

