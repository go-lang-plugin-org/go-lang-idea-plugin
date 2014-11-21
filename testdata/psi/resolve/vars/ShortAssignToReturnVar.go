package main

func Test(x int) (/*def*/r int) {
	/*ref*/r, a := x, 1
	_ = a
	return
}
