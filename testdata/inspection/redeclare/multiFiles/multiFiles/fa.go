package multiFiles

var /*begin*/bad1/*end.Redeclare in this block*/ = 1

var /*begin*/Bad2/*end.Redeclare in this block*/ = 1

var /*begin*/Bad3/*end.Redeclare in this block*/ = 1

var /*begin*/Bad4/*end.Redeclare in this block*/ = 1

var /*begin*/Bad5/*end.Redeclare in this block*/ = 1


var good1 = 1

func good3(){
	a:=0
	_=a
}

type good5 struct{
}

func (g good5)/*begin*/bad6/*end.Redeclare in this block*/(){

}