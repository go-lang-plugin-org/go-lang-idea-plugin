package main

func good1(){
	for i := 0; i == 0; {
		i:=0
		_=i
		break
	}
	i := 0
	_=i
}
func good2(){
	for i:=range []int{}{
		_=i
		break
	}
	i:=0
	_=i
}

func good3(){
	for i,k:=range []int{}{
		_=i
		_=k
		break
	}
	i:=0
	_=i
	k:=0
	_=k
}