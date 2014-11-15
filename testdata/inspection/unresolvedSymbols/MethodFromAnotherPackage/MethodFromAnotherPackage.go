package MethodFromAnotherPackage

import (
	"p1"
)

func test1() {
	t := p1.TSturct{}
	t.Good2()

	t./*begin*/NotExist1/*end.Unresolved symbol: 'NotExist1'*/()
}
func test2() {
	t := make(p1.TChan)
	t.Good3()

	t./*begin*/NotExist2/*end.Unresolved symbol: 'NotExist2'*/()
}

func test3() {
	t := p1.TFunc(nil)
	t.Good4()

	t./*begin*/NotExist3/*end.Unresolved symbol: 'NotExist3'*/()
}
func test4() {
	t := make(p1.TMap)
	t.Good5()

	t./*begin*/NotExist4/*end.Unresolved symbol: 'NotExist4'*/()
}
func test5() {
	t := make(p1.TSlice,33)
	t.Good6()

	t./*begin*/NotExist5/*end.Unresolved symbol: 'NotExist5'*/()
}
func test6(){
	t := p1.TInt(0)
	t.Good7()

	t./*begin*/NotExist6/*end.Unresolved symbol: 'NotExist6'*/()

}

func test7(){
	t := p1.GetTInterface()
	t.Good8()

	t./*begin*/NotExist7/*end.Unresolved symbol: 'NotExist7'*/()
}

func testBool(){
	t := p1.TBool(true)
	t.GoodBool()

	t./*begin*/NotExist8/*end.Unresolved symbol: 'NotExist8'*/()
}

func testComplex(){
	t := p1.TComplex(1)
	t.GoodComplex()

	t./*begin*/NotExist9/*end.Unresolved symbol: 'NotExist9'*/()
}

func testUintptr(){
	t := p1.TUintptr(1)
	t.GoodUintptr()

	t./*begin*/NotExist10/*end.Unresolved symbol: 'NotExist10'*/()
}

func testArray(){
	t := p1.TArray([3]int{1,1,1})
	t.GoodArray()

	t./*begin*/NotExist11/*end.Unresolved symbol: 'NotExist11'*/()
}

func testPointer(){
	t := p1.TPointer(nil)

	t./*begin*/NotExist12/*end.Unresolved symbol: 'NotExist12'*/()
}