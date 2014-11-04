package redeclare


func bad1(x int) (r int) {
	/*begin*/r/*end.No new variables on left side of :=*/ := x
	return
}

func bad2(x int) (r int) {
	/*begin*/x/*end.No new variables on left side of :=*/ :=1
	return
}

func bad3(){
	bad3:=1
	_=bad3
}

type t1 int
func (t *t1)bad4(){
	/*begin*/t/*end.No new variables on left side of :=*/ :=1
	return
}

func bad5(){
	a := func(x int)(r int){
		/*begin*/x/*end.No new variables on left side of :=*/ :=1
		/*begin*/r/*end.No new variables on left side of :=*/ :=1
		return
	}
}

func Foo() int {
	return 1
}

func Redeclared1(a, _ int) (/*begin*/a/*end.Redeclare in this block*/, _, b int){
	return 1, 2, 3
}

func Redeclared2(a int) (/*begin*/a/*end.Redeclare in this block*/ int){
	return 1
}

func DuplicateArg1(a int, b int, /*begin*/a/*end.Redeclare in this block*/ int) {
}

func DuplicateArg2(a, b, /*begin*/a/*end.Redeclare in this block*/, _, _ int) {
}