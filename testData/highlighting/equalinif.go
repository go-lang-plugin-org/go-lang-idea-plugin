package equalinif

func _() {
	a := 1
	b := 2
	if <error descr="a = b used as value">a = b</error> {
		println("a = b")
	} else {
		println("a != b")
	}

	if <error descr="a := b used as value"><error descr="Unused variable 'a'">a</error> := b</error> {
		println("a = b")
	} else {
		println("a != b")
	}

	if a = b; a != 1 {
		println("a = b")
	} else {
		println("a != b")
	}

	if a := b; a == 1 {
		println("a = b")
	} else {
		println("a != b")
	}
}
