package main

func someFunc() {
	sub1 := func() { }
	sub1()
}

func someFunc2() {
	var sub1 func ()
	sub1 = func() { }
	sub1() // unresolved symbol error
}
