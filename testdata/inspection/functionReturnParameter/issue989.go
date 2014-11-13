package unresolvedSymbols

func F1() (x, y int) {
	return F2()
}

func F2() (int, int) {
	return 1, 1
}