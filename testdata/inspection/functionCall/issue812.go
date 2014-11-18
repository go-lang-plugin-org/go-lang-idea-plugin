package main

// issue #812
func f1(s string, arg ...int) int {
	return 0
}
func f11()(int,int){
	return 0,0
}

func f12()(int,float64){
	return 0,0
}

func bad() {
	a := 1

	f1("", /*begin*/a/*end.cannot use a (type int) as type []int in argument to f1|CastTypeFix*/ ...)

}
func good1() {
	f1("", []int{1, 2, 3}...)
	f1("", f1("", []int{1, 2, 3}...))
}

func f2(arg ...int) int {
	return 0
}
func bad2(){
	f2(/*begin*/f11()/*end.multiple-value f11() in single-value context*/...)
	f2(/*begin*/f12()/*end.cannot use float64 as type int in argument to f2*/)
}

func good2() {
	f2()
	f2(1)
	f2(1, 2)
	f2([]int{1, 2, 3}...)
	f2(f2([]int{1, 2, 3}...))
	f2(f11())
}

func main() {}
