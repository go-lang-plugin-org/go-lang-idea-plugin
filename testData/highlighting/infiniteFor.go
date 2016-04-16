package infiniteFor

func _() {
	for {
		println("")
	}

	for i:=0; i<10; i++ {

	}

	for range []byte{} {
	}

	for i:=0; i<10; i++ {
		println("hello")
	}

	<error descr="Infinite for loop">for {
	}</error>
}

