package main

const (
	AAAAA MyInt = iota
	BBBBB
	CCCCC
)

type MyInt int64

const (
	NORMAL = 45
	NOTNARMAL = BBBBB
)

func HandleFunc(pa string, handler func(int, *string)bool) {

}

func HandleInterface(a int, fn func()interface {}) {

}

type iFunc interface {

}

func HandleIFunc(fn func()iFunc) {

}

type Foo struct {

}

func Accept(f *Foo) {

}

func Run() {
	f := new(Foo)
	Accept(f)
}

func AcceptMyInt(arg MyInt) {

}

func MatchF(arg float64) {

}

func MatchI(arg int64) {

}

func main() {
	//ISSUE #357
	MatchI('5')
	MatchI(1.0 * NORMAL)
	MatchF(5 * NORMAL)
	MatchF(NORMAL)
	MatchF(45)
	MatchF(5 * 5 * 24 * 31 * 314)
	MatchI(1.0)
	MatchI(/*begin*/NOTNARMAL/*end.Expression type mismatch, the expected type is int64|CastTypeFix*/)
	MatchI(/*begin*/2.5/*end.Expression type mismatch, the expected type is int64|CastTypeFix*/)
	MatchI(/*begin*/"5"/*end.Expression type mismatch, the expected type is int64|CastTypeFix*/)
	//PR #344
	var invalid int = 1
	AcceptMyInt(BBBBB * 5)
	AcceptMyInt(5 * BBBBB * 5)
	AcceptMyInt(5 * BBBBB)
	AcceptMyInt(NORMAL * BBBBB)
	AcceptMyInt(NORMAL * BBBBB)
	AcceptMyInt(NORMAL)
	AcceptMyInt(/*begin*/invalid/*end.Expression type mismatch, the expected type is MyInt|CastTypeFix*/)
	//END

	var interfacE = interface{}
	HandleIFunc(func() iFunc {return true})
	HandleIFunc(((func() iFunc {return true})))
	HandleIFunc((func() iFunc)(nil))
	HandleIFunc(interfacE.(func() iFunc))
	HandleInterface(34, func() interface {} {return true})
	HandleInterface(34, (func() interface {} {return true}))
	indexHandler := func() {}
	var valid = func(arg int, arg2 *string) bool {return true}
	HandleFunc("/", (func(int, *string)bool)(indexHandler))
	HandleFunc("/", valid)
	HandleFunc("/", /*begin*/indexHandler/*end.Expression type mismatch, the expected type is func(int, *string)bool|CastTypeFix*/)
	HandleFunc(/*begin*/56/*end.Expression type mismatch, the expected type is string|CastTypeFix*/, valid)
}
