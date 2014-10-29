package p1

import "issue858/p3"

//p1 is from another package
var V1 *p3.T1

var A int

func AA()int{
	return 1
}

var V2 p3.T2

var V3 T3

type T3 int

func (t T3)F3(){

}

var V4 T4

type T4 int