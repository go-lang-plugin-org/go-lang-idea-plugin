package p1

type TBool bool
func (t TBool) GoodBool(){

}

type TComplex complex128
func (t TComplex) GoodComplex(){

}

type TUintptr uintptr
func (t TUintptr) GoodUintptr(){

}

type TArray [3]int
func (t TArray) GoodArray(){

}

type TPointer *int
//can not add method on a named pointer type

type TSturct struct{}

func (t TSturct) Good2() {

}

type TChan chan int

func (t TChan) Good3() {

}

type TFunc func()

func (t TFunc) Good4() {

}

type TMap map[string]string

func (t TMap) Good5() {

}

type TSlice []string

func (t TSlice) Good6() {

}

type TInt int
func (t TInt) Good7() {

}

type TInterface interface{
	Good8()
}

func GetTInterface()TInterface{
	return t8(0)
}

type t8 int
func (t t8) Good8() {

}