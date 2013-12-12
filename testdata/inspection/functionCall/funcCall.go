package main

func HandleFunc(pa string, handler func(int,*string)bool) {

}

func main() {
	indexHandler := func() {}
	var valid = func(arg int, arg2 *string) bool {return true}
	HandleFunc("/", (func(int, *string)bool)(indexHandler))
	HandleFunc("/", valid)
	HandleFunc("/", /*begin*/indexHandler/*end.Expression type mismatch, expeted type is func(int,*string)bool|CastTypeFix*/)
	HandleFunc(/*begin*/56/*end.Expression type mismatch, expeted type is string|CastTypeFix*/, valid)
}
