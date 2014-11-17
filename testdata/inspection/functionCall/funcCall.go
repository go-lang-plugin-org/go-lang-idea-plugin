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

const (
	StringVal = "string"
	IntVal int = 10
)

const (
	A, B = "string", 10
	C, D
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
	MatchI(/*begin*/FLOAT_CONST/*end.cannot use FLOAT_CONST (type float64) as type int64 in argument to MatchI|CastTypeFix*/)
	MatchI(/*begin*/NOTNARMAL/*end.cannot use NOTNARMAL (type MyInt) as type int64 in argument to MatchI|CastTypeFix*/)
	MatchI(/*begin*/2.5/*end.cannot use 2.5 (type float64) as type int64 in argument to MatchI|CastTypeFix*/)
	MatchI(/*begin*/-2.5/*end.cannot use -2.5 (type float64) as type int64 in argument to MatchI|CastTypeFix*/)
	MatchI(/*begin*/(-2.5)/*end.cannot use (-2.5) (type float64) as type int64 in argument to MatchI|CastTypeFix*/)
	MatchI(/*begin*/"5"/*end.cannot use "5" (type string) as type int64 in argument to MatchI|CastTypeFix*/)
	MatchI(/*begin*/-"5"/*end.cannot use -"5" (type unknown) as type int64 in argument to MatchI|CastTypeFix*/)
	//PR #344
	var invalid int = 1
	AcceptMyInt(BBBBB * 5)
	AcceptMyInt(5 * BBBBB * 5)
	AcceptMyInt(5 * BBBBB)
	AcceptMyInt(NORMAL * BBBBB)
	AcceptMyInt(NORMAL * BBBBB)
	AcceptMyInt(NORMAL)
	AcceptMyInt(/*begin*/invalid/*end.cannot use invalid (type int) as type MyInt in argument to AcceptMyInt|CastTypeFix*/)
	//END

	// issue #474
	fVal := 0.1
	MatchF(fVal)

	MatchByte(1)
	MatchByte(/*begin*/-128/*end.cannot use -128 (type int) as type byte in argument to MatchByte|CastTypeFix*/)
	MatchByte(128)
	MatchByte(10.0)
	MatchByte(0.5 + 0.5)
	MatchByte(/*begin*/0.5 - 1.5/*end.cannot use 0.5 - 1.5 (type float64) as type byte in argument to MatchByte|CastTypeFix*/)

	MatchUint8(1)
	MatchUint8(/*begin*/-128/*end.cannot use -128 (type int) as type uint8 in argument to MatchUint8|CastTypeFix*/)
	MatchUint8(128)
	MatchUint8(10.0)
	MatchUint8(/*begin*/0.5 - 1.5/*end.cannot use 0.5 - 1.5 (type float64) as type uint8 in argument to MatchUint8|CastTypeFix*/)

	MatchInt16(0)
	MatchInt16(-32768)
	MatchInt16(32767)
	MatchInt16(10.0)
	MatchInt16(/*begin*/1.1/*end.cannot use 1.1 (type float64) as type int16 in argument to MatchInt16|CastTypeFix*/)
	MatchInt16(/*begin*/-32769/*end.cannot use -32769 (type int) as type int16 in argument to MatchInt16|CastTypeFix*/)
	MatchInt16(/*begin*/32769/*end.cannot use 32769 (type int) as type int16 in argument to MatchInt16|CastTypeFix*/)

	MatchUint16(0)
	MatchUint16(/*begin*/-128/*end.cannot use -128 (type int) as type uint16 in argument to MatchUint16|CastTypeFix*/)
	MatchUint16(65535)
	MatchUint16(10.0)
	MatchUint16(/*begin*/0.5 - 1.5/*end.cannot use 0.5 - 1.5 (type float64) as type uint16 in argument to MatchUint16|CastTypeFix*/)

	MatchInt32(0)
	MatchInt32(-2147483648)
	MatchInt32(2147483647)
	MatchInt32(10.0)
	MatchInt32(/*begin*/1.1/*end.cannot use 1.1 (type float64) as type int32 in argument to MatchInt32|CastTypeFix*/)
	MatchInt32(/*begin*/-2147483649/*end.cannot use -2147483649 (type int) as type int32 in argument to MatchInt32|CastTypeFix*/)
	MatchInt32(/*begin*/2147483649/*end.cannot use 2147483649 (type int) as type int32 in argument to MatchInt32|CastTypeFix*/)

	MatchRune(0)
	MatchRune(-2147483648)
	MatchRune(2147483647)
	MatchRune(10.0)
	MatchRune(/*begin*/1.1/*end.cannot use 1.1 (type float64) as type rune in argument to MatchRune|CastTypeFix*/)
	MatchRune(/*begin*/-2147483649/*end.cannot use -2147483649 (type int) as type rune in argument to MatchRune|CastTypeFix*/)
	MatchRune(/*begin*/2147483649/*end.cannot use 2147483649 (type int) as type rune in argument to MatchRune|CastTypeFix*/)

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
	HandleFunc("/", /*begin*/indexHandler/*end.cannot use indexHandler (type func()) as type func(int,*string)bool in argument to HandleFunc|CastTypeFix*/)
	HandleFunc(/*begin*/56/*end.cannot use 56 (type int) as type string in argument to HandleFunc|CastTypeFix*/, valid)

	HandleMap(map[string]string{})
	HandleMap(MyMap{})
	HandleMap(MyMap2{})
	HandleMap(/*begin*/map[int]string{}/*end.cannot use map[int]string{} (type map[int]string) as type map[string]string in argument to HandleMap|CastTypeFix*/)
	HandleMap(/*begin*/map[string]int{}/*end.cannot use map[string]int{} (type map[string]int) as type map[string]string in argument to HandleMap|CastTypeFix*/)
	HandleMap(/*begin*/[]int{}/*end.cannot use []int{} (type []int) as type map[string]string in argument to HandleMap|CastTypeFix*/)

	HandleMyMap(MyMap{})
	HandleMyMap(map[string]string{})
	HandleMyMap(/*begin*/map[int]string{}/*end.cannot use map[int]string{} (type map[int]string) as type MyMap in argument to HandleMyMap|CastTypeFix*/)
	HandleMyMap(/*begin*/map[string]int{}/*end.cannot use map[string]int{} (type map[string]int) as type MyMap in argument to HandleMyMap|CastTypeFix*/)
	HandleMyMap(/*begin*/[]int{}/*end.cannot use []int{} (type []int) as type MyMap in argument to HandleMyMap|CastTypeFix*/)
	HandleMyMap(/*begin*/MyMap2{}/*end.cannot use MyMap2{} (type MyMap2) as type MyMap in argument to HandleMyMap|CastTypeFix*/)

	HandleMyMap2(MyMap2{})
	HandleMyMap2(map[string]string{})
	HandleMyMap2(/*begin*/map[int]string{}/*end.cannot use map[int]string{} (type map[int]string) as type MyMap2 in argument to HandleMyMap2|CastTypeFix*/)
	HandleMyMap2(/*begin*/map[string]int{}/*end.cannot use map[string]int{} (type map[string]int) as type MyMap2 in argument to HandleMyMap2|CastTypeFix*/)
	HandleMyMap2(/*begin*/[]int{}/*end.cannot use []int{} (type []int) as type MyMap2 in argument to HandleMyMap2|CastTypeFix*/)
	HandleMyMap2(/*begin*/MyMap{}/*end.cannot use MyMap{} (type MyMap) as type MyMap2 in argument to HandleMyMap2|CastTypeFix*/)

	// ISSUE 528
	HandleMapInterface(map[string]interface{}{"x": "whatever"})
	HandleMapInterface(/*begin*/map[int]interface{}{"x": "whatever"}/*end.cannot use map[int]interface{}{"x": "whatever"} (type map[int]interface{}) as type map[string]interface{} in argument to HandleMapInterface|CastTypeFix*/)


	HandleSlice([]string{})
	HandleSlice(MySlice{})
	HandleSlice(/*begin*/[]int{}/*end.cannot use []int{} (type []int) as type []string in argument to HandleSlice|CastTypeFix*/)
	HandleSlice(/*begin*/map[string]int{}/*end.cannot use map[string]int{} (type map[string]int) as type []string in argument to HandleSlice|CastTypeFix*/)

	HandleMySlice(MySlice{})
	HandleMySlice([]string{})
	HandleMySlice(/*begin*/[]int{}/*end.cannot use []int{} (type []int) as type MySlice in argument to HandleMySlice|CastTypeFix*/)
	HandleMySlice(/*begin*/map[string]int{}/*end.cannot use map[string]int{} (type map[string]int) as type MySlice in argument to HandleMySlice|CastTypeFix*/)
	HandleMySlice(/*begin*/MySlice2{}/*end.cannot use MySlice2{} (type MySlice2) as type MySlice in argument to HandleMySlice|CastTypeFix*/)

	HandleMySlice2(MySlice2{})
	HandleMySlice2([]string{})
	HandleMySlice2(/*begin*/[]int{}/*end.cannot use []int{} (type []int) as type MySlice2 in argument to HandleMySlice2|CastTypeFix*/)
	HandleMySlice2(/*begin*/map[string]int{}/*end.cannot use map[string]int{} (type map[string]int) as type MySlice2 in argument to HandleMySlice2|CastTypeFix*/)
	HandleMySlice2(/*begin*/MySlice{}/*end.cannot use MySlice{} (type MySlice) as type MySlice2 in argument to HandleMySlice2|CastTypeFix*/)

	HandleArray([3]string{})
	HandleArray(MyArray{})
	HandleArray(/*begin*/[4]string{}/*end.cannot use [4]string{} (type [4]string) as type [3]string in argument to HandleArray|CastTypeFix*/)
	HandleArray(/*begin*/[3]int{}/*end.cannot use [3]int{} (type [3]int) as type [3]string in argument to HandleArray|CastTypeFix*/)

	HandleArray([1 + 2]string{})
	HandleArray([3.0]string{})
	HandleArray(/*begin*/[1 + 2 + 2]string{}/*end.cannot use [1 + 2 + 2]string{} (type [5]string) as type [3]string in argument to HandleArray|CastTypeFix*/)
	HandleArray([GOOD_ARRAY]string{})
	HandleArray([GOOD_ARRAY + 1 - 1]string{})
	HandleArray([BAD_ARRAY - 1]string{})
	HandleArray(/*begin*/[BAD_ARRAY]string{}/*end.cannot use [BAD_ARRAY]string{} (type [4]string) as type [3]string in argument to HandleArray|CastTypeFix*/)
	HandleArray([GOOD_ARRAY1]string{})
	HandleArray(/*begin*/[BAD_ARRAY1]string{}/*end.cannot use [BAD_ARRAY1]string{} (type [4]string) as type [3]string in argument to HandleArray|CastTypeFix*/)
	HandleArray([GOOD_ARRAY2]string{})
	HandleArray(/*begin*/[BAD_ARRAY2]string{}/*end.cannot use [BAD_ARRAY2]string{} (type [4]string) as type [3]string in argument to HandleArray|CastTypeFix*/)
	HandleArray([THREE]string{})
	HandleArray([THREE2]string{})
	HandleArray([THREE3]string{})
	HandleArray([THREE4]string{})
	HandleArray([THREE5]string{})
	HandleArray([THREE6]string{})
	HandleArray([THREE7]string{})
	HandleArray([TWO + 1]string{})
	HandleArray(/*begin*/[TWO]string{}/*end.cannot use [TWO]string{} (type [2]string) as type [3]string in argument to HandleArray|CastTypeFix*/)
	HandleArray(/*begin*/[FOUR]string{}/*end.cannot use [FOUR]string{} (type [4]string) as type [3]string in argument to HandleArray|CastTypeFix*/)

	HandleArray([UNTYPED_INT]string{})
	HandleArray(/*begin*/[FLOAT_CONST]string{}/*end.cannot use [FLOAT_CONST]string{} (type [-1]string) as type [3]string in argument to HandleArray|CastTypeFix*/)
	HandleArray([FLOAT_CONST2]string{})


	HandleMyArray(MyArray{})
	HandleMyArray([3]string{})
	HandleArray([...]string{"a", "b", "c"})
	HandleMyArray(/*begin*/[4]string{}/*end.cannot use [4]string{} (type [4]string) as type MyArray in argument to HandleMyArray|CastTypeFix*/)
	HandleMyArray(/*begin*/[3]int{}/*end.cannot use [3]int{} (type [3]int) as type MyArray in argument to HandleMyArray|CastTypeFix*/)
	HandleMyArray(/*begin*/MyArray2{}/*end.cannot use MyArray2{} (type MyArray2) as type MyArray in argument to HandleMyArray|CastTypeFix*/)

	HandleMyArray2(MyArray2{})
	HandleMyArray2([3]string{})
	HandleMyArray2(/*begin*/[4]string{}/*end.cannot use [4]string{} (type [4]string) as type MyArray2 in argument to HandleMyArray2|CastTypeFix*/)
	HandleMyArray2(/*begin*/[3]int{}/*end.cannot use [3]int{} (type [3]int) as type MyArray2 in argument to HandleMyArray2|CastTypeFix*/)
	HandleMyArray2(/*begin*/MyArray{}/*end.cannot use MyArray{} (type MyArray) as type MyArray2 in argument to HandleMyArray2|CastTypeFix*/)

	HandleArray([...]string{"a", "b", "c"})
	HandleArray(/*begin*/[...]string{"a", "b", "c", "d"}/*end.cannot use [...]string{"a", "b", "c", "d"} (type [4]string) as type [3]string in argument to HandleArray|CastTypeFix*/)
	HandleArray(/*begin*/[...]string{"a", "b",}/*end.cannot use [...]string{"a", "b",} (type [2]string) as type [3]string in argument to HandleArray|CastTypeFix*/)
	HandleArray(/*begin*/[...]int{1, 2 ,3}/*end.cannot use [...]int{1, 2 ,3} (type [3]int) as type [3]string in argument to HandleArray|CastTypeFix*/)

	HandleMyFunc(HandleMap)
	HandleMyFunc(/*begin*/HandleMyMap/*end.cannot use HandleMyMap (type func(MyMap)) as type MyFunc in argument to HandleMyFunc|CastTypeFix*/)
	HandleMyFunc(func(map[string]string){})
	HandleMyFunc((func(map[string]string){}))
	HandleMyFunc(/*begin*/(-(func(map[int]string){}))/*end.cannot use (-(func(map[int]string){})) (type func(map[int]string)) as type MyFunc in argument to HandleMyFunc|CastTypeFix*/)
	HandleMyFunc(/*begin*/func(map[int]string){}/*end.cannot use func(map[int]string){} (type func(map[int]string)) as type MyFunc in argument to HandleMyFunc|CastTypeFix*/)
	HandleMyFunc(MyFunc(func(map[string]string){}))
	fn := MyFunc(func(map[string]string){})
	HandleMyFunc(fn)
	HandleMyFunc(/*begin*/HandleSlice/*end.cannot use HandleSlice (type func([]string)) as type MyFunc in argument to HandleMyFunc|CastTypeFix*/)

	// HandleMyFunc(MyFunc2(func(map[string]string){})) TODO: should generate "Expression type mismatch, the expected type is MyFunc|CastTypeFix"
	//issue #520
	HandleIFunc(nil)
	HandleArray(/*begin*/nil/*end.cannot use nil (type nil) as type [3]string in argument to HandleArray|CastTypeFix*/)
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
	HandleChan(/*begin*/chInt/*end.cannot use chInt (type chan int) as type chan string in argument to HandleChan|CastTypeFix*/)
	HandleChan(/*begin*/chStrIn/*end.cannot use chStrIn (type <-chan string) as type chan string in argument to HandleChan|CastTypeFix*/)
	HandleChan(/*begin*/chStrOut/*end.cannot use chStrOut (type chan<- string) as type chan string in argument to HandleChan|CastTypeFix*/)

	HandleInChan(chStrIn)
	HandleInChan(chStr)
	HandleInChan(/*begin*/chInt/*end.cannot use chInt (type chan int) as type <-chan string in argument to HandleInChan|CastTypeFix*/)
	HandleInChan(/*begin*/chIntIn/*end.cannot use chIntIn (type <-chan int) as type <-chan string in argument to HandleInChan|CastTypeFix*/)
	HandleInChan(/*begin*/chIntOut/*end.cannot use chIntOut (type chan<- int) as type <-chan string in argument to HandleInChan|CastTypeFix*/)
	HandleInChan(/*begin*/chStrOut/*end.cannot use chStrOut (type chan<- string) as type <-chan string in argument to HandleInChan|CastTypeFix*/)

	HandleOutChan(chStrOut)
	HandleOutChan(chStr)
	HandleOutChan(/*begin*/chInt/*end.cannot use chInt (type chan int) as type chan<- string in argument to HandleOutChan|CastTypeFix*/)
	HandleOutChan(/*begin*/chIntIn/*end.cannot use chIntIn (type <-chan int) as type chan<- string in argument to HandleOutChan|CastTypeFix*/)
	HandleOutChan(/*begin*/chIntOut/*end.cannot use chIntOut (type chan<- int) as type chan<- string in argument to HandleOutChan|CastTypeFix*/)
	HandleOutChan(/*begin*/chStrIn/*end.cannot use chStrIn (type <-chan string) as type chan<- string in argument to HandleOutChan|CastTypeFix*/)

	HandleChan(/*begin*/make(chan int)/*end.cannot use make(chan int) (type chan int) as type chan string in argument to HandleChan|CastTypeFix*/)

	// issue #522

	HandleStruct(&struct{a int; b string}{})
	HandleStruct(/*begin*/&struct{a int; b int}{}/*end.cannot use &struct{a int; b int}{} (type *struct{aint;bint}) as type *struct{aint;bstring} in argument to HandleStruct|CastTypeFix*/)

	HandleStructWithTag(&struct{a int `first`; b string}{})
	HandleStructWithTag(/*begin*/&struct{a int; b int}{}/*end.cannot use &struct{a int; b int}{} (type *struct{aint;bint}) as type *struct{aint Identifier;bstring} in argument to HandleStructWithTag|CastTypeFix*/)
	HandleStructWithTag(/*begin*/&struct{a int `second`; b int}{}/*end.cannot use &struct{a int `second`; b int}{} (type *struct{aint Identifier;bint}) as type *struct{aint Identifier;bstring} in argument to HandleStructWithTag|CastTypeFix*/)
	HandleStructWithTag(/*begin*/&struct{a int; b int `first`}{}/*end.cannot use &struct{a int; b int `first`}{} (type *struct{aint;bint Identifier}) as type *struct{aint Identifier;bstring} in argument to HandleStructWithTag|CastTypeFix*/)

	HandleMyStruct(struct{a int; b string}{})
	myStruct := MyStruct{}
	HandleFunc("/",myStruct.TestHandle)

	// issue #586
	HandleVariadic("", 1, 2, 3)
	HandleVariadic("", 1, 2, /*begin*/"4"/*end.cannot use "4" (type string) as type int in argument to HandleVariadic|CastTypeFix*/)
	HandleVariadic("", /*begin*/"4"/*end.cannot use "4" (type string) as type int in argument to HandleVariadic|CastTypeFix*/)
	wrongTypeFunctionTest := func(a string) bool {return false}
	TestLiteralFunc(/*begin*/wrongTypeFunctionTest/*end.cannot use wrongTypeFunctionTest (type func(string)bool) as type func(string,string)(bool,bool) in argument to TestLiteralFunc|CastTypeFix*/)
	myNewFnForTest := func(a string, b string) (bool,bool,bool) {return false,false,false}
	TestLiteralFunc(/*begin*/myNewFnForTest/*end.cannot use myNewFnForTest (type func(string,string)(bool,bool,bool)) as type func(string,string)(bool,bool) in argument to TestLiteralFunc|CastTypeFix*/)
	TestLiteralFunc(func(a string,b string)(bool,bool) {return false,true})

	AcceptMyInt(CCCCC)
	HandleVariadic(/*begin*/CCCCC/*end.cannot use CCCCC (type MyInt) as type string in argument to HandleVariadic|CastTypeFix*/)
	HandleVariadic(StringVal, 1)
	HandleVariadic(A, B)
	HandleVariadic(C, D)
	HandleVariadic(A, /*begin*/C/*end.cannot use C (type string) as type int in argument to HandleVariadic|CastTypeFix*/)
	HandleVariadic(/*begin*/B/*end.cannot use B (type int) as type string in argument to HandleVariadic|CastTypeFix*/, D)
}
