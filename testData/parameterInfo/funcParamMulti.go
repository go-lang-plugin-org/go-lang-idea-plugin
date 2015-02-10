package foo

func bar(a, b, c int, d, e, f string) {
}

func test() {
	bar(1, 2, 3, "4", <caret>)
}

