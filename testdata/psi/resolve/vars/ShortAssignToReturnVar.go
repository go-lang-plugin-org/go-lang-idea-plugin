package main

func Test(x int) (/*def*/r int) {
	/*ref*/r = x
	return
}

func Test2(x int) (/*def*/a int) {
	/*ref*/a, /*def*/b := x, 1
	_ = /*ref*/b
	return
}
