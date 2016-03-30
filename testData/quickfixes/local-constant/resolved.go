package foo

func main() {
	if true {
		a := 123
		_ = a
	}
	b := a<caret>
}