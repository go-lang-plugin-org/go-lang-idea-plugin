package foo

func main() {
	x := "text"
	if <caret><error descr="Mismatched types: byte and string">x[0] == "tex"</error> {

	}
}