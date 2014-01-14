package main

var (
	_ = f(
		1, 2,
		3, 4,
		5)

	_ = f(1,
		2,
		3, 4,
		5)
)
