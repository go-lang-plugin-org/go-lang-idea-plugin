package nil

func <warning>nilAssign</warning>() {
	var a, b, c = 1, <error>nil</error>, 2
	_, _, _ = a, b, c

	d, e, f := 1, <error>nil</error>, 2
	_, _, _ = d, e, f

	const g, h, i = 1, <error>nil</error>, 2
	_, _, _ = g, h, i
}

func <warning>nilVar</warning>() {
	var nil = 123
	var a = nil
	_ = a
}
