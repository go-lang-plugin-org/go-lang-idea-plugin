package mainin

func good1() {
	panic(1)
	panic("hello")
	/*begin*/panic()/*end.not enough arguments in call to panic*/
}
