package main

var (
	a = 1
	/*begin*/a/*end.Redeclare in this block*/ = 1
)

func bad1(){
	b:=1
	/*begin*/b/*end.No new variables on left side of :=*/:=1
}

func bad2(){
	b:=1
	a:=1
	if true{
		c:=2
	}
	/*begin*/b/*end.No new variables on left side of :=*/:=3
	/*begin*/a,b/*end.No new variables on left side of :=*/:=3,4

}

func good1(){
	b:=1
	if true{
		b:=2
		_=b
	}
	a,b:=1,1
	_=b
	_=a
}

type a1 struct{

}

type /*begin*/a1/*end.Redeclare in this block*/ int


type b1 int

var(
	/*begin*/b1/*end.Redeclare in this block*/=1
)

type c1 int

func /*begin*/c1/*end.Redeclare in this block*/(){

}

func a2(){

}

func /*begin*/a2/*end.Redeclare in this block*/(){

}

func b2(){

}

const /*begin*/b2/*end.Redeclare in this block*/ = 1

var _ = 1

var _ = 2

const (
	c3 = 1
	/*begin*/c3/*end.Redeclare in this block*/ = 1
)

type t1 int

func (t t1) a1() {

}

func (t t1) ta1() {

}

func (t t1) /*begin*/ta1/*end.Redeclare in this block*/() {

}

type t2 int

func (t t2) ta1() {

}
func (t t2) ta2() {

}
func (t *t2) /*begin*/ta2/*end.Redeclare in this block*/() {

}