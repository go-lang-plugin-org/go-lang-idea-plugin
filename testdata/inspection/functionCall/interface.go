package functionCall

func f3(arg ...interface{}) int {
	return 0
}
func good3() {
	a := []interface{}{}
	f3(a...)
}

type t1 string

func f4(arg ...t1) int {
	return 0
}
func bad4() {
	f4(/*begin*/[]string{}/*end.Expression type mismatch, the expected type is []t1*/...)
}
func good4() {
	a := []t1{}
	f4(a...)
}