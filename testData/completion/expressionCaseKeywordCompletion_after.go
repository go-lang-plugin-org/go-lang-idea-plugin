package foo

func hi() {
	var a = 1
	switch a {
	case 1:
		println(1)
		return nil
	case :
		<caret>
	}
}