package main

const (
	AAAAA MyInt = iota
	BBBBB
	CCCCC
)

type MyInt int64

type MyMap map[string]string
type MyMap2 MyMap
type MySlice []string
type MySlice2 MySlice
type MyArray [3]string
type MyArray2 MyArray

type MyFunc func(a map[string]string)
type MyFunc2 MyFunc

const (
	NORMAL = 45
	NOTNARMAL = BBBBB
)

func HandleFunc(pa string, handler func(int, *string)bool) {

}

func HandleInterface(a int, fn func()interface {}) {

}

func HandleMap(a map[string]string) {

}

func HandleSlice(a []string) {

}

func HandleArray(a [3]string){

}

func HandleMyMap(a MyMap) {

}

func HandleMyMap2(a MyMap2) {

}

func HandleMySlice(a MySlice) {

}

func HandleMySlice2(a MySlice2) {

}

func HandleMyArray(a MyArray) {

}

func HandleMyArray2(a MyArray2) {

}

func HandleMyFunc(a MyFunc) {

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
	MatchI(1. * NORMAL)
	MatchF(5 * NORMAL)
	MatchF(NORMAL)
	MatchF(45)
	MatchF(5 * 5 * 24 * 31 * 314)
	MatchI(1.0)
	MatchI(1.)
	MatchI(/*begin*/NOTNARMAL/*end.Expression type mismatch, the expected type is int64|CastTypeFix*/)
	MatchI(/*begin*/2.5/*end.Expression type mismatch, the expected type is int64|CastTypeFix*/)
	MatchI(/*begin*/-2.5/*end.Expression type mismatch, the expected type is int64|CastTypeFix*/)
	MatchI(/*begin*/(-2.5)/*end.Expression type mismatch, the expected type is int64|CastTypeFix*/)
	MatchI(/*begin*/"5"/*end.Expression type mismatch, the expected type is int64|CastTypeFix*/)
	MatchI(/*begin*/-"5"/*end.Expression type mismatch, the expected type is int64|CastTypeFix*/)
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

	HandleMap(map[string]string{})
	HandleMap(MyMap{})
	HandleMap(MyMap2{})
	HandleMap(/*begin*/map[int]string{}/*end.Expression type mismatch, the expected type is map[string]string|CastTypeFix*/)
	HandleMap(/*begin*/map[string]int{}/*end.Expression type mismatch, the expected type is map[string]string|CastTypeFix*/)
	HandleMap(/*begin*/[]int{}/*end.Expression type mismatch, the expected type is map[string]string|CastTypeFix*/)

	HandleMyMap(MyMap{})
	HandleMyMap(map[string]string{})
	HandleMyMap(/*begin*/map[int]string{}/*end.Expression type mismatch, the expected type is MyMap|CastTypeFix*/)
	HandleMyMap(/*begin*/map[string]int{}/*end.Expression type mismatch, the expected type is MyMap|CastTypeFix*/)
	HandleMyMap(/*begin*/[]int{}/*end.Expression type mismatch, the expected type is MyMap|CastTypeFix*/)
	HandleMyMap(/*begin*/MyMap2{}/*end.Expression type mismatch, the expected type is MyMap|CastTypeFix*/)

	HandleMyMap2(MyMap2{})
	HandleMyMap2(map[string]string{})
	HandleMyMap2(/*begin*/map[int]string{}/*end.Expression type mismatch, the expected type is MyMap2|CastTypeFix*/)
	HandleMyMap2(/*begin*/map[string]int{}/*end.Expression type mismatch, the expected type is MyMap2|CastTypeFix*/)
	HandleMyMap2(/*begin*/[]int{}/*end.Expression type mismatch, the expected type is MyMap2|CastTypeFix*/)
	HandleMyMap2(/*begin*/MyMap{}/*end.Expression type mismatch, the expected type is MyMap2|CastTypeFix*/)


	HandleSlice([]string{})
	HandleSlice(MySlice{})
	HandleSlice(/*begin*/[]int{}/*end.Expression type mismatch, the expected type is []string|CastTypeFix*/)
	HandleSlice(/*begin*/map[string]int{}/*end.Expression type mismatch, the expected type is []string|CastTypeFix*/)

	HandleMySlice(MySlice{})
	HandleMySlice([]string{})
	HandleMySlice(/*begin*/[]int{}/*end.Expression type mismatch, the expected type is MySlice|CastTypeFix*/)
	HandleMySlice(/*begin*/map[string]int{}/*end.Expression type mismatch, the expected type is MySlice|CastTypeFix*/)
	HandleMySlice(/*begin*/MySlice2{}/*end.Expression type mismatch, the expected type is MySlice|CastTypeFix*/)

	HandleMySlice2(MySlice2{})
	HandleMySlice2([]string{})
	HandleMySlice2(/*begin*/[]int{}/*end.Expression type mismatch, the expected type is MySlice2|CastTypeFix*/)
	HandleMySlice2(/*begin*/map[string]int{}/*end.Expression type mismatch, the expected type is MySlice2|CastTypeFix*/)
	HandleMySlice2(/*begin*/MySlice{}/*end.Expression type mismatch, the expected type is MySlice2|CastTypeFix*/)

	HandleArray([3]string{})
	HandleArray(MyArray{})
	HandleArray(/*begin*/[4]string{}/*end.Expression type mismatch, the expected type is [3]string|CastTypeFix*/)
	HandleArray(/*begin*/[3]int{}/*end.Expression type mismatch, the expected type is [3]string|CastTypeFix*/)

	HandleMyArray(MyArray{})
	HandleMyArray([3]string{})
	HandleArray([...]string{"a", "b", "c"})
	HandleMyArray(/*begin*/[4]string{}/*end.Expression type mismatch, the expected type is MyArray|CastTypeFix*/)
	HandleMyArray(/*begin*/[3]int{}/*end.Expression type mismatch, the expected type is MyArray|CastTypeFix*/)
	HandleMyArray(/*begin*/MyArray2{}/*end.Expression type mismatch, the expected type is MyArray|CastTypeFix*/)

	HandleMyArray2(MyArray2{})
	HandleMyArray2([3]string{})
	HandleMyArray2(/*begin*/[4]string{}/*end.Expression type mismatch, the expected type is MyArray2|CastTypeFix*/)
	HandleMyArray2(/*begin*/[3]int{}/*end.Expression type mismatch, the expected type is MyArray2|CastTypeFix*/)
	HandleMyArray2(/*begin*/MyArray{}/*end.Expression type mismatch, the expected type is MyArray2|CastTypeFix*/)

	HandleArray([...]string{"a", "b", "c"})
	HandleArray(/*begin*/[...]string{"a", "b", "c", "d"}/*end.Expression type mismatch, the expected type is [3]string|CastTypeFix*/)
	HandleArray(/*begin*/[...]string{"a", "b",}/*end.Expression type mismatch, the expected type is [3]string|CastTypeFix*/)
	HandleArray(/*begin*/[...]int{1, 2 ,3}/*end.Expression type mismatch, the expected type is [3]string|CastTypeFix*/)

	HandleMyFunc(HandleMap)
	HandleMyFunc(HandleMyMap)
	HandleMyFunc(func(map[string]string){})
	HandleMyFunc((func(map[string]string){}))
	HandleMyFunc(/*begin*/(-(func(map[int]string){}))/*end.Expression type mismatch, the expected type is MyFunc|CastTypeFix*/)
	HandleMyFunc(/*begin*/func(map[int]string){}/*end.Expression type mismatch, the expected type is MyFunc|CastTypeFix*/)
	HandleMyFunc(MyFunc(func(map[string]string){}))
	fn := MyFunc(func(map[string]string){})
	HandleMyFunc(fn)
	HandleMyFunc(/*begin*/HandleSlice/*end.Expression type mismatch, the expected type is MyFunc|CastTypeFix*/)

	// HandleMyFunc(MyFunc2(func(map[string]string){})) TODO: should generate "Expression type mismatch, the expected type is MyFunc|CastTypeFix"

}
