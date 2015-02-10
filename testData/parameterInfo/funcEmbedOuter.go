package foo

func bar(num int, text string) {
}

func blah(a, b, c, d int) int {
	return a + b
}

func test() {
	blah(1, 2, ba<caret>r(3, ), 4)
}

