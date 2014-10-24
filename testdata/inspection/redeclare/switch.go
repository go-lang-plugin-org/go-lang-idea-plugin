package redeclare

func good1(){
	switch {
	case 1==1:
		i1:=1
		_=i1
	case 1==2:
		i1:=2
		_=i1
	}
}

func good2(){
	x:=interface{}(1)
	switch i2 := x.(type){
	case int:
		a:=i2
		_=a
	}
	i2:=5
	_=i2
}

func good3(){
	switch i3:=1;{
	case 1==1:
		a:=i3
		_=a
	}
	i3:=5
	_=i3
}
func good4(){
	switch i4:=interface{}(1);i4:=i4.(type){
	case int:
		a:=i4
		_=a
	case bool:
		a:=i4
		_=a
	}
	i4:=5
	_=i4
}