package functionCall

func good1(c1 chan<- int) {
	close(c1)
}

func bad1(v int){
	close(/*begin*/v/*end.cannot use v (type int) as type chan<- Type in argument to close|CastTypeFix*/)
}
