package main

func HandleFunc(pa string, handler func(int, *string)bool) {

}

func HandleInterface(a int, fn func()interface {}) {

}

type iFunc interface {

}

func HandleIFunc(fn func()iFunc) {

}

func main() {
	HandleIFunc(func() iFunc {return true})
	HandleIFunc((func() iFunc {return true}))
	HandleInterface(34, func() interface {} {return true})
	HandleInterface(34, (func() interface {} {return true}))
	indexHandler := func() {}
	var valid = func(arg int, arg2 *string) bool {return true}
	HandleFunc("/", (func(int, *string)bool)(indexHandler))
	HandleFunc("/", valid)
	HandleFunc("/", /*begin*/indexHandler/*end.Expression type mismatch, the expected type is func(int, *string)bool|CastTypeFix*/)
	HandleFunc(/*begin*/56/*end.Expression type mismatch, the expected type is string|CastTypeFix*/, valid)
}
