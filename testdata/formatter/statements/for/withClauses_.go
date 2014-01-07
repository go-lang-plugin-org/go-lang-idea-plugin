package main

func f() {
	// 34234
	// adfas
	for i := 0; i < 10; i++ {
		f(i)
	} // adsfasd

	// with init statement
	for a = 1; ; f() {
	}
}
