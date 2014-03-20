package main

const (
	AAAAA MyInt = iota
	BBBBB
	CCCCC
)

const (
	ZERO = iota
	ONE
	TWO = iota
	THREE
	THREE2 = iota - 1
	FOUR
	THREE3 = TWO + 1
	THREE4 = GOOD_ARRAY
	THREE5 = BAD_ARRAY - 1
	THREE6 = ZERO + ONE + TWO
	THREE7 = THREE
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

type MyStruct struct{
	a int
	b string
}

func (t *MyStruct) TestHandle(arg int , arg1 *string) bool {
	return true
}

const (
	NORMAL = 45
	NOTNARMAL = BBBBB
	GOOD_ARRAY = 1 + 1 + 2 - 1 // 3
	BAD_ARRAY = 2 + 2 // 4
	UNTYPED_INT = 15 / 4 // 3
	FLOAT_CONST = 15 / 4.0 // 3.75
	FLOAT_CONST2 = 12 / 4.0 // 3
)

const (
	GOOD_ARRAY1, BAD_ARRAY1 = 3 , 4
	GOOD_ARRAY2, BAD_ARRAY2
)

func HandleFunc(pa string, handler func(int, *string)bool) {

}

func HandleInterface(a int, fn func()interface {}) {

}

func HandleMap(a map[string]string) {

}

func HandleMapInterface(a map[string]interface{}){

}

func TestLiteralFunc(arg func(string,string)(bool,bool)) {

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

func HandleChan(c chan string){

}

func HandleInChan(c <-chan string){

}

func HandleOutChan(c chan<- string){

}

func HandleStruct(c *struct{a int; b string}){

}

func HandleStructWithTag(c *struct{a int `first`; b string}){

}

func HandleMyStruct(c MyStruct){

}

type iFunc interface {

}

func HandleIFunc(fn func()iFunc) {

}

func HandleVariadic(c string, arg... int) {

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

func MatchByte(arg byte){

}

func MatchUint8(arg uint8){

}

func MatchInt16(arg int16){

}

func MatchUint16(arg uint16){

}

func MatchInt32(arg int32){

}

func MatchRune(arg rune){

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
	MatchI(0.5 + 0.5)
	MatchI(UNTYPED_INT)
	MatchI(/*begin*/FLOAT_CONST/*end.Expression type mismatch, the expected type is int64|CastTypeFix*/)
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

	// issue #474
	fVal := 0.1
	MatchF(fVal)

	MatchByte(1)
	MatchByte(/*begin*/-128/*end.Expression type mismatch, the expected type is byte|CastTypeFix*/)
	MatchByte(128)
	MatchByte(10.0)
	MatchByte(0.5 + 0.5)
	MatchByte(/*begin*/0.5 - 1.5/*end.Expression type mismatch, the expected type is byte|CastTypeFix*/)

	MatchUint8(1)
	MatchUint8(/*begin*/-128/*end.Expression type mismatch, the expected type is uint8|CastTypeFix*/)
	MatchUint8(128)
	MatchUint8(10.0)
	MatchUint8(/*begin*/0.5 - 1.5/*end.Expression type mismatch, the expected type is uint8|CastTypeFix*/)

	MatchInt16(0)
	MatchInt16(-32768)
	MatchInt16(32767)
	MatchInt16(10.0)
	MatchInt16(/*begin*/1.1/*end.Expression type mismatch, the expected type is int16|CastTypeFix*/)
	MatchInt16(/*begin*/-32769/*end.Expression type mismatch, the expected type is int16|CastTypeFix*/)
	MatchInt16(/*begin*/32769/*end.Expression type mismatch, the expected type is int16|CastTypeFix*/)

	MatchUint16(0)
	MatchUint16(/*begin*/-128/*end.Expression type mismatch, the expected type is uint16|CastTypeFix*/)
	MatchUint16(65535)
	MatchUint16(10.0)
	MatchUint16(/*begin*/0.5 - 1.5/*end.Expression type mismatch, the expected type is uint16|CastTypeFix*/)

	MatchInt32(0)
	MatchInt32(-2147483648)
	MatchInt32(2147483647)
	MatchInt32(10.0)
	MatchInt32(/*begin*/1.1/*end.Expression type mismatch, the expected type is int32|CastTypeFix*/)

	// GoLiteralIntegerImpl does not return proper value now
	//	MatchInt32(-2147483649)
	//	MatchInt32(2147483649)

	MatchRune(0)
	MatchRune(-2147483648)
	MatchRune(2147483647)
	MatchRune(10.0)
	MatchRune(/*begin*/1.1/*end.Expression type mismatch, the expected type is rune|CastTypeFix*/)

	// GoLiteralIntegerImpl does not return proper value now
	//	MatchRune(-2147483649)
	//	MatchRune(2147483649)

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

	// ISSUE 528
	HandleMapInterface(map[string]interface{}{"x": "whatever"})
	HandleMapInterface(/*begin*/map[int]interface{}{"x": "whatever"}/*end.Expression type mismatch, the expected type is map[string]interface{}|CastTypeFix*/)


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

	HandleArray([1 + 2]string{})
	HandleArray([3.0]string{})
	HandleArray(/*begin*/[1 + 2 + 2]string{}/*end.Expression type mismatch, the expected type is [3]string|CastTypeFix*/)
	HandleArray([GOOD_ARRAY]string{})
	HandleArray([GOOD_ARRAY + 1 - 1]string{})
	HandleArray([BAD_ARRAY - 1]string{})
	HandleArray(/*begin*/[BAD_ARRAY]string{}/*end.Expression type mismatch, the expected type is [3]string|CastTypeFix*/)
	HandleArray([GOOD_ARRAY1]string{})
	HandleArray(/*begin*/[BAD_ARRAY1]string{}/*end.Expression type mismatch, the expected type is [3]string|CastTypeFix*/)
	HandleArray([GOOD_ARRAY2]string{})
	HandleArray(/*begin*/[BAD_ARRAY2]string{}/*end.Expression type mismatch, the expected type is [3]string|CastTypeFix*/)
	HandleArray([THREE]string{})
	HandleArray([THREE2]string{})
	HandleArray([THREE3]string{})
	HandleArray([THREE4]string{})
	HandleArray([THREE5]string{})
	HandleArray([THREE6]string{})
	HandleArray([THREE7]string{})
	HandleArray([TWO + 1]string{})
	HandleArray(/*begin*/[TWO]string{}/*end.Expression type mismatch, the expected type is [3]string|CastTypeFix*/)
	HandleArray(/*begin*/[FOUR]string{}/*end.Expression type mismatch, the expected type is [3]string|CastTypeFix*/)

	HandleArray([UNTYPED_INT]string{})
	HandleArray(/*begin*/[FLOAT_CONST]string{}/*end.Expression type mismatch, the expected type is [3]string|CastTypeFix*/)
	HandleArray([FLOAT_CONST2]string{})


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
	//issue #520
	HandleIFunc(nil)
	HandleArray(/*begin*/nil/*end.Expression type mismatch, the expected type is [3]string|CastTypeFix*/)
	HandleMap(nil)
	HandleSlice(nil)
	HandleChan(nil)
	HandleInChan(nil)
	HandleOutChan(nil)
	Accept(nil)


	// issue #454
	HandleChan(make(chan string))

	chStr := make(chan string)
	chInt := make(chan int)
	chStrIn := make(<-chan string)
	chIntIn := make(<-chan int)
	chStrOut := make(chan<- string)
	chIntOut := make(chan<- int)

	HandleChan(chStr)
	HandleChan(/*begin*/chInt/*end.Expression type mismatch, the expected type is chan string|CastTypeFix*/)
	HandleChan(/*begin*/chStrIn/*end.Expression type mismatch, the expected type is chan string|CastTypeFix*/)
	HandleChan(/*begin*/chStrOut/*end.Expression type mismatch, the expected type is chan string|CastTypeFix*/)

	HandleInChan(chStrIn)
	HandleInChan(chStr)
	HandleInChan(/*begin*/chInt/*end.Expression type mismatch, the expected type is <-chan string|CastTypeFix*/)
	HandleInChan(/*begin*/chIntIn/*end.Expression type mismatch, the expected type is <-chan string|CastTypeFix*/)
	HandleInChan(/*begin*/chIntOut/*end.Expression type mismatch, the expected type is <-chan string|CastTypeFix*/)
	HandleInChan(/*begin*/chStrOut/*end.Expression type mismatch, the expected type is <-chan string|CastTypeFix*/)

	HandleOutChan(chStrOut)
	HandleOutChan(chStr)
	HandleOutChan(/*begin*/chInt/*end.Expression type mismatch, the expected type is chan<- string|CastTypeFix*/)
	HandleOutChan(/*begin*/chIntIn/*end.Expression type mismatch, the expected type is chan<- string|CastTypeFix*/)
	HandleOutChan(/*begin*/chIntOut/*end.Expression type mismatch, the expected type is chan<- string|CastTypeFix*/)
	HandleOutChan(/*begin*/chStrIn/*end.Expression type mismatch, the expected type is chan<- string|CastTypeFix*/)

	 HandleChan(make(chan int))  //TODO: should generate "Expression type mismatch, the expected type is chan int|CastTypeFix"

	// issue #522

	HandleStruct(&struct{a int; b string}{})
	HandleStruct(/*begin*/&struct{a int; b int}{}/*end.Expression type mismatch, the expected type is *struct{a int; b string}|CastTypeFix*/)

	HandleStructWithTag(&struct{a int `first`; b string}{})
	HandleStructWithTag(/*begin*/&struct{a int; b int}{}/*end.Expression type mismatch, the expected type is *struct{a int `first`; b string}|CastTypeFix*/)
	HandleStructWithTag(/*begin*/&struct{a int `second`; b int}{}/*end.Expression type mismatch, the expected type is *struct{a int `first`; b string}|CastTypeFix*/)
	HandleStructWithTag(/*begin*/&struct{a int; b int `first`}{}/*end.Expression type mismatch, the expected type is *struct{a int `first`; b string}|CastTypeFix*/)

	HandleMyStruct(struct{a int; b string}{})
	myStruct := MyStruct{}
	HandleFunc("/",myStruct.TestHandle)

	// issue #586
	HandleVariadic("", 1, 2, 3)
	HandleVariadic("", 1, 2, /*begin*/"4"/*end.Expression type mismatch, the expected type is int|CastTypeFix*/)
	HandleVariadic("", /*begin*/"4"/*end.Expression type mismatch, the expected type is int|CastTypeFix*/)
	wrongTypeFunctionTest := func(a string) bool {return false}
	TestLiteralFunc(/*begin*/wrongTypeFunctionTest/*end.Expression type mismatch, the expected type is func(string,string)(bool,bool)|CastTypeFix*/)
	myNewFnForTest := func(a string, b string) (bool,bool,bool) {return false,false,false}
	TestLiteralFunc(/*begin*/myNewFnForTest/*end.Expression type mismatch, the expected type is func(string,string)(bool,bool)|CastTypeFix*/)
	TestLiteralFunc(func(a string,b string)(bool,bool) {return false,true})
}
