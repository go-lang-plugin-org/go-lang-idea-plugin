package p1

func Test1() {

	// i should see T1 (it's defined inside a test file)
	_ = T1(1)

	// i should see MainT1 (it's defined by the main test)
	_ = MainT1(1)
}
