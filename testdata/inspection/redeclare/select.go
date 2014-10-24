package redeclare

func good1(){
	c1:=make(chan int)
	c2:=make(chan int)
	select {
	case i := <-c1:
		x:=0
		_=x
	case i:=<-c2:
		x:=0
		_=x
	}
	i:=0
	_=i
	x:=0
	_=x
}