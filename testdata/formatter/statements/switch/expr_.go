package main

func f() {
	switch e {
	case a:
		return
	}
}

func f() {
	switch ; e {
	case a:
		return
	}
}

func f() {
	switch e1, e2 := 2, 3; e1 {
	case a:
		return 1
	}
}
