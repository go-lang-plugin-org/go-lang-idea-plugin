package main

func main() {
	v := ReturnsFunc("1")(10)("<caret>")
}

func ReturnsFunc(s string) func (int) func(string) string {
	return func (i int) func(string) string {
		return func (foo string) string {
			return "ola"
		}
	}
}