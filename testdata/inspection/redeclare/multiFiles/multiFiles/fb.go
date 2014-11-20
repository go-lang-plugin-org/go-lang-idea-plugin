package multiFiles

func /*begin*/bad1/*end.Redeclare in this block*/(){}

var /*begin*/Bad2/*end.Redeclare in this block*/ = 1

type /*begin*/Bad3/*end.Redeclare in this block*/ struct{

}

type /*begin*/Bad4/*end.Redeclare in this block*/ interface{
}

const /*begin*/Bad5/*end.Redeclare in this block*/ = 1

var good2=1

func good4(){
	a:=0
	_=a
}

func (g good5)/*begin*/bad6/*end.Redeclare in this block*/(){

}